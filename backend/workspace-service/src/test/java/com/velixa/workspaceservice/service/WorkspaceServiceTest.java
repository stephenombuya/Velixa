package com.velixa.workspaceservice.service;

import com.velixa.workspaceservice.dto.WorkspaceDtos.*;
import com.velixa.workspaceservice.exception.VelixaException;
import com.velixa.workspaceservice.model.*;
import com.velixa.workspaceservice.repository.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {

    @Mock
    private WorkspaceRepository workspaceRepository;

    @InjectMocks
    private WorkspaceService workspaceService;

    private Workspace testWorkspace;
    private final String OWNER_ID = "owner-1";
    private final String MEMBER_ID = "member-1";

    @BeforeEach
    void setUp() {
        WorkspaceMember owner = WorkspaceMember.builder()
                .userId(OWNER_ID).role(WorkspaceRole.OWNER).joinedAt(Instant.now()).build();
        WorkspaceMember member = WorkspaceMember.builder()
                .userId(MEMBER_ID).email("member@test.com").role(WorkspaceRole.MEMBER)
                .joinedAt(Instant.now()).build();

        testWorkspace = Workspace.builder()
                .id("ws-1")
                .name("Test Workspace")
                .slug("test-workspace")
                .ownerId(OWNER_ID)
                .visibility(WorkspaceVisibility.PRIVATE)
                .active(true)
                .members(new ArrayList<>(List.of(owner, member)))
                .build();
    }

    @Test
    void createWorkspace_newSlug_createsSuccessfully() {
        CreateWorkspaceRequest req = new CreateWorkspaceRequest(
                "My Workspace", "my-workspace", "desc", null, WorkspaceVisibility.PRIVATE);

        when(workspaceRepository.existsBySlug("my-workspace")).thenReturn(false);
        when(workspaceRepository.save(any())).thenReturn(testWorkspace);

        WorkspaceResponse result = workspaceService.createWorkspace(req, OWNER_ID);

        assertThat(result).isNotNull();
        assertThat(result.ownerId()).isEqualTo(OWNER_ID);
        verify(workspaceRepository).save(any(Workspace.class));
    }

    @Test
    void createWorkspace_duplicateSlug_throwsConflict() {
        CreateWorkspaceRequest req = new CreateWorkspaceRequest(
                "My Workspace", "test-workspace", null, null, null);

        when(workspaceRepository.existsBySlug("test-workspace")).thenReturn(true);

        assertThatThrownBy(() -> workspaceService.createWorkspace(req, OWNER_ID))
                .isInstanceOf(VelixaException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void getMyWorkspaces_returnsUserWorkspaces() {
        when(workspaceRepository.findAllAccessibleByUserId(OWNER_ID))
                .thenReturn(List.of(testWorkspace));

        List<WorkspaceResponse> result = workspaceService.getMyWorkspaces(OWNER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).slug()).isEqualTo("test-workspace");
    }

    @Test
    void updateWorkspace_byAdmin_updatesName() {
        // Make OWNER also an ADMIN-level
        when(workspaceRepository.findById("ws-1")).thenReturn(Optional.of(testWorkspace));
        when(workspaceRepository.save(any())).thenReturn(testWorkspace);

        UpdateWorkspaceRequest req = new UpdateWorkspaceRequest("New Name", null, null, null);
        WorkspaceResponse result = workspaceService.updateWorkspace("ws-1", req, OWNER_ID);

        assertThat(result).isNotNull();
        verify(workspaceRepository).save(any());
    }

    @Test
    void removeMember_byOwner_removesMember() {
        when(workspaceRepository.findById("ws-1")).thenReturn(Optional.of(testWorkspace));
        when(workspaceRepository.save(any())).thenReturn(testWorkspace);

        MessageResponse result = workspaceService.removeMember("ws-1", MEMBER_ID, OWNER_ID);

        assertThat(result.success()).isTrue();
        assertThat(testWorkspace.getMembers()).noneMatch(m -> m.getUserId().equals(MEMBER_ID));
    }

    @Test
    void removeMember_owner_throwsBadRequest() {
        when(workspaceRepository.findById("ws-1")).thenReturn(Optional.of(testWorkspace));

        assertThatThrownBy(() -> workspaceService.removeMember("ws-1", OWNER_ID, OWNER_ID))
                .isInstanceOf(VelixaException.class)
                .hasMessageContaining("Cannot remove workspace owner");
    }

    @Test
    void deleteWorkspace_byOwner_deactivates() {
        when(workspaceRepository.findById("ws-1")).thenReturn(Optional.of(testWorkspace));
        when(workspaceRepository.save(any())).thenReturn(testWorkspace);

        MessageResponse result = workspaceService.deleteWorkspace("ws-1", OWNER_ID);

        assertThat(result.success()).isTrue();
        assertThat(testWorkspace.isActive()).isFalse();
    }

    @Test
    void deleteWorkspace_byNonOwner_throwsForbidden() {
        when(workspaceRepository.findById("ws-1")).thenReturn(Optional.of(testWorkspace));

        assertThatThrownBy(() -> workspaceService.deleteWorkspace("ws-1", MEMBER_ID))
                .isInstanceOf(VelixaException.class)
                .hasMessageContaining("Only the workspace owner");
    }
}
