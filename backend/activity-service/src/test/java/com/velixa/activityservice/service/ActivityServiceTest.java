package com.velixa.activityservice.service;

import com.velixa.activityservice.dto.ActivityDtos.*;
import com.velixa.activityservice.exception.VelixaException;
import com.velixa.activityservice.model.Activity;
import com.velixa.activityservice.model.ActivityType;
import com.velixa.activityservice.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    private Activity testActivity;

    @BeforeEach
    void setUp() {
        testActivity = Activity.builder()
                .id("act-1")
                .workspaceId("ws-1")
                .actorId("user-1")
                .actorUsername("johndoe")
                .type(ActivityType.WORKSPACE_CREATED)
                .resourceId("ws-1")
                .resourceType("WORKSPACE")
                .resourceName("Test Workspace")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void record_validRequest_savesAndReturnsActivity() {
        CreateActivityRequest req = new CreateActivityRequest(
                "ws-1", "user-1", "johndoe", null,
                ActivityType.WORKSPACE_CREATED, "ws-1", "WORKSPACE",
                "Test Workspace", null);

        when(activityRepository.save(any())).thenReturn(testActivity);

        ActivityResponse response = activityService.record(req);

        assertThat(response.id()).isEqualTo("act-1");
        assertThat(response.type()).isEqualTo(ActivityType.WORKSPACE_CREATED);
        verify(activityRepository).save(any(Activity.class));
    }

    @Test
    void getActivity_existingId_returnsActivity() {
        when(activityRepository.findById("act-1")).thenReturn(Optional.of(testActivity));

        ActivityResponse response = activityService.getActivity("act-1");

        assertThat(response.actorUsername()).isEqualTo("johndoe");
        assertThat(response.workspaceId()).isEqualTo("ws-1");
    }

    @Test
    void getActivity_missingId_throwsNotFound() {
        when(activityRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.getActivity("bad-id"))
                .isInstanceOf(VelixaException.class)
                .hasMessageContaining("Activity not found");
    }

    @Test
    void getWorkspaceActivities_returnsPagedResponse() {
        var page = new PageImpl<>(List.of(testActivity), PageRequest.of(0, 20), 1);
        when(activityRepository.findByWorkspaceIdOrderByCreatedAtDesc(eq("ws-1"), any()))
                .thenReturn(page);

        PagedActivitiesResponse response = activityService.getWorkspaceActivities("ws-1", 0, 20);

        assertThat(response.content()).hasSize(1);
        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.last()).isTrue();
    }

    @Test
    void getUserActivities_returnsPagedResponse() {
        var page = new PageImpl<>(List.of(testActivity), PageRequest.of(0, 20), 1);
        when(activityRepository.findByActorIdOrderByCreatedAtDesc(eq("user-1"), any()))
                .thenReturn(page);

        PagedActivitiesResponse response = activityService.getUserActivities("user-1", 0, 20);

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).actorId()).isEqualTo("user-1");
    }

    @Test
    void purgeWorkspaceActivities_callsDeleteByWorkspaceId() {
        doNothing().when(activityRepository).deleteByWorkspaceId("ws-1");

        activityService.purgeWorkspaceActivities("ws-1");

        verify(activityRepository).deleteByWorkspaceId("ws-1");
    }
}
