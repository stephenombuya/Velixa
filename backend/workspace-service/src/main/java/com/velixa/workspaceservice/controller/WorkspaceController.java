package com.velixa.workspaceservice.controller;

import com.velixa.workspaceservice.dto.WorkspaceDtos.*;
import com.velixa.workspaceservice.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
@Tag(name = "Workspaces", description = "Workspace management")
@SecurityRequirement(name = "bearerAuth")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    @Operation(summary = "Create a new workspace")
    public ResponseEntity<WorkspaceResponse> create(
            @Valid @RequestBody CreateWorkspaceRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workspaceService.createWorkspace(request, userId));
    }

    @GetMapping
    @Operation(summary = "List all workspaces accessible by the current user")
    public ResponseEntity<List<WorkspaceResponse>> list(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(workspaceService.getMyWorkspaces(userId));
    }

    @GetMapping("/{workspaceId}")
    @Operation(summary = "Get workspace by ID")
    public ResponseEntity<WorkspaceDetailResponse> getById(
            @PathVariable String workspaceId,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(workspaceService.getWorkspace(workspaceId, userId));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get workspace by slug")
    public ResponseEntity<WorkspaceDetailResponse> getBySlug(
            @PathVariable String slug,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(workspaceService.getWorkspaceBySlug(slug, userId));
    }

    @PatchMapping("/{workspaceId}")
    @Operation(summary = "Update workspace details")
    public ResponseEntity<WorkspaceResponse> update(
            @PathVariable String workspaceId,
            @RequestBody UpdateWorkspaceRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(workspaceService.updateWorkspace(workspaceId, request, userId));
    }

    @DeleteMapping("/{workspaceId}")
    @Operation(summary = "Soft-delete a workspace")
    public ResponseEntity<MessageResponse> delete(
            @PathVariable String workspaceId,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(workspaceService.deleteWorkspace(workspaceId, userId));
    }

    @PostMapping("/{workspaceId}/members")
    @Operation(summary = "Invite a member to the workspace")
    public ResponseEntity<WorkspaceDetailResponse> inviteMember(
            @PathVariable String workspaceId,
            @Valid @RequestBody InviteMemberRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workspaceService.inviteMember(workspaceId, request, userId));
    }

    @PatchMapping("/{workspaceId}/members/{memberId}")
    @Operation(summary = "Update a member's role")
    public ResponseEntity<WorkspaceDetailResponse> updateMemberRole(
            @PathVariable String workspaceId,
            @PathVariable String memberId,
            @RequestBody UpdateMemberRoleRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(
                workspaceService.updateMemberRole(workspaceId, memberId, request, userId));
    }

    @DeleteMapping("/{workspaceId}/members/{memberId}")
    @Operation(summary = "Remove a member from the workspace")
    public ResponseEntity<MessageResponse> removeMember(
            @PathVariable String workspaceId,
            @PathVariable String memberId,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(workspaceService.removeMember(workspaceId, memberId, userId));
    }
}
