package com.velixa.workspaceservice.dto;

import com.velixa.workspaceservice.model.WorkspaceMember;
import com.velixa.workspaceservice.model.WorkspaceRole;
import com.velixa.workspaceservice.model.WorkspaceVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public final class WorkspaceDtos {

    private WorkspaceDtos() {}

    // ── Requests ──────────────────────────────────────────────────────────────

    public record CreateWorkspaceRequest(
            @NotBlank(message = "Workspace name is required")
            @Size(min = 2, max = 80)
            String name,

            @NotBlank(message = "Slug is required")
            @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug may only contain lowercase letters, digits, and hyphens")
            @Size(min = 2, max = 50)
            String slug,

            String description,
            String logoUrl,
            WorkspaceVisibility visibility
    ) {}

    public record UpdateWorkspaceRequest(
            @Size(min = 2, max = 80)
            String name,
            String description,
            String logoUrl,
            WorkspaceVisibility visibility
    ) {}

    public record InviteMemberRequest(
            @NotBlank
            String userId,
            @NotBlank
            String email,
            String displayName,
            WorkspaceRole role
    ) {}

    public record UpdateMemberRoleRequest(
            WorkspaceRole role
    ) {}

    // ── Responses ─────────────────────────────────────────────────────────────

    public record WorkspaceResponse(
            String id,
            String slug,
            String name,
            String description,
            String logoUrl,
            String ownerId,
            WorkspaceVisibility visibility,
            boolean active,
            int memberCount,
            WorkspaceRole currentUserRole,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record WorkspaceDetailResponse(
            String id,
            String slug,
            String name,
            String description,
            String logoUrl,
            String ownerId,
            WorkspaceVisibility visibility,
            List<WorkspaceMember> members,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record MessageResponse(
            String message,
            boolean success
    ) {
        public static MessageResponse ok(String message) {
            return new MessageResponse(message, true);
        }
    }
}
