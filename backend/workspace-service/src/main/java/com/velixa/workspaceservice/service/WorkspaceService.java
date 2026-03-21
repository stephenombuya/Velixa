package com.velixa.workspaceservice.service;

import com.velixa.workspaceservice.dto.WorkspaceDtos.*;
import com.velixa.workspaceservice.exception.VelixaException;
import com.velixa.workspaceservice.model.*;
import com.velixa.workspaceservice.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceResponse createWorkspace(CreateWorkspaceRequest request, String userId) {
        if (workspaceRepository.existsBySlug(request.slug())) {
            throw VelixaException.conflict("Workspace slug '" + request.slug() + "' already exists");
        }

        WorkspaceMember ownerMember = WorkspaceMember.builder()
                .userId(userId)
                .role(WorkspaceRole.OWNER)
                .joinedAt(Instant.now())
                .build();

        Workspace workspace = Workspace.builder()
                .name(request.name())
                .slug(request.slug())
                .description(request.description())
                .logoUrl(request.logoUrl())
                .ownerId(userId)
                .visibility(request.visibility() != null ? request.visibility() : WorkspaceVisibility.PRIVATE)
                .members(List.of(ownerMember))
                .active(true)
                .build();

        workspace = workspaceRepository.save(workspace);
        log.info("Created workspace '{}' for user {}", workspace.getSlug(), userId);
        return toResponse(workspace, userId);
    }

    public List<WorkspaceResponse> getMyWorkspaces(String userId) {
        return workspaceRepository.findAllAccessibleByUserId(userId).stream()
                .map(w -> toResponse(w, userId))
                .collect(Collectors.toList());
    }

    public WorkspaceDetailResponse getWorkspace(String workspaceId, String userId) {
        Workspace workspace = findWorkspaceById(workspaceId);
        assertAccess(workspace, userId);
        return toDetailResponse(workspace);
    }

    public WorkspaceDetailResponse getWorkspaceBySlug(String slug, String userId) {
        Workspace workspace = workspaceRepository.findBySlug(slug)
                .orElseThrow(() -> VelixaException.notFound("Workspace not found: " + slug));
        assertAccess(workspace, userId);
        return toDetailResponse(workspace);
    }

    public WorkspaceResponse updateWorkspace(String workspaceId, UpdateWorkspaceRequest request, String userId) {
        Workspace workspace = findWorkspaceById(workspaceId);
        assertRole(workspace, userId, WorkspaceRole.ADMIN);

        if (request.name() != null) workspace.setName(request.name());
        if (request.description() != null) workspace.setDescription(request.description());
        if (request.logoUrl() != null) workspace.setLogoUrl(request.logoUrl());
        if (request.visibility() != null) workspace.setVisibility(request.visibility());

        return toResponse(workspaceRepository.save(workspace), userId);
    }

    public MessageResponse deleteWorkspace(String workspaceId, String userId) {
        Workspace workspace = findWorkspaceById(workspaceId);
        assertOwner(workspace, userId);
        workspace.setActive(false);
        workspaceRepository.save(workspace);
        log.info("Deactivated workspace {} by user {}", workspaceId, userId);
        return MessageResponse.ok("Workspace deleted successfully");
    }

    public WorkspaceDetailResponse inviteMember(String workspaceId, InviteMemberRequest request, String userId) {
        Workspace workspace = findWorkspaceById(workspaceId);
        assertRole(workspace, userId, WorkspaceRole.ADMIN);

        boolean alreadyMember = workspace.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(request.userId()));
        if (alreadyMember) {
            throw VelixaException.conflict("User is already a member of this workspace");
        }

        WorkspaceMember newMember = WorkspaceMember.builder()
                .userId(request.userId())
                .email(request.email())
                .displayName(request.displayName())
                .role(request.role() != null ? request.role() : WorkspaceRole.MEMBER)
                .joinedAt(Instant.now())
                .build();

        workspace.getMembers().add(newMember);
        return toDetailResponse(workspaceRepository.save(workspace));
    }

    public WorkspaceDetailResponse updateMemberRole(String workspaceId, String memberId,
                                                     UpdateMemberRoleRequest request, String userId) {
        Workspace workspace = findWorkspaceById(workspaceId);
        assertOwner(workspace, userId);

        workspace.getMembers().stream()
                .filter(m -> m.getUserId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> VelixaException.notFound("Member not found"))
                .setRole(request.role());

        return toDetailResponse(workspaceRepository.save(workspace));
    }

    public MessageResponse removeMember(String workspaceId, String memberId, String userId) {
        Workspace workspace = findWorkspaceById(workspaceId);
        assertRole(workspace, userId, WorkspaceRole.ADMIN);

        if (workspace.getOwnerId().equals(memberId)) {
            throw VelixaException.badRequest("Cannot remove workspace owner");
        }

        boolean removed = workspace.getMembers().removeIf(m -> m.getUserId().equals(memberId));
        if (!removed) throw VelixaException.notFound("Member not found");

        workspaceRepository.save(workspace);
        return MessageResponse.ok("Member removed successfully");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Workspace findWorkspaceById(String id) {
        return workspaceRepository.findById(id)
                .filter(Workspace::isActive)
                .orElseThrow(() -> VelixaException.notFound("Workspace not found: " + id));
    }

    private void assertAccess(Workspace workspace, String userId) {
        if (workspace.getVisibility() == WorkspaceVisibility.PUBLIC) return;
        boolean isMember = workspace.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(userId));
        if (!isMember && !workspace.getOwnerId().equals(userId)) {
            throw VelixaException.forbidden("Access denied to workspace");
        }
    }

    private void assertRole(Workspace workspace, String userId, WorkspaceRole minimumRole) {
        Optional<WorkspaceMember> member = workspace.getMembers().stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst();

        if (member.isEmpty()) throw VelixaException.forbidden("Not a member of this workspace");

        WorkspaceRole role = member.get().getRole();
        int userLevel = roleLevel(role);
        int requiredLevel = roleLevel(minimumRole);

        if (userLevel < requiredLevel) {
            throw VelixaException.forbidden("Insufficient role: requires " + minimumRole);
        }
    }

    private void assertOwner(Workspace workspace, String userId) {
        if (!workspace.getOwnerId().equals(userId)) {
            throw VelixaException.forbidden("Only the workspace owner can perform this action");
        }
    }

    private int roleLevel(WorkspaceRole role) {
        return switch (role) {
            case VIEWER -> 1;
            case MEMBER -> 2;
            case ADMIN -> 3;
            case OWNER -> 4;
        };
    }

    private WorkspaceResponse toResponse(Workspace w, String userId) {
        WorkspaceRole currentRole = w.getMembers().stream()
                .filter(m -> m.getUserId().equals(userId))
                .map(WorkspaceMember::getRole)
                .findFirst()
                .orElse(null);

        return new WorkspaceResponse(
                w.getId(), w.getSlug(), w.getName(), w.getDescription(),
                w.getLogoUrl(), w.getOwnerId(), w.getVisibility(),
                w.isActive(), w.getMembers().size(), currentRole,
                w.getCreatedAt(), w.getUpdatedAt()
        );
    }

    private WorkspaceDetailResponse toDetailResponse(Workspace w) {
        return new WorkspaceDetailResponse(
                w.getId(), w.getSlug(), w.getName(), w.getDescription(),
                w.getLogoUrl(), w.getOwnerId(), w.getVisibility(),
                w.getMembers(), w.getCreatedAt(), w.getUpdatedAt()
        );
    }
}
