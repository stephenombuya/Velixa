package com.velixa.activityservice.controller;

import com.velixa.activityservice.dto.ActivityDtos.*;
import com.velixa.activityservice.model.ActivityType;
import com.velixa.activityservice.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
@Tag(name = "Activities", description = "Activity feed and audit log")
@SecurityRequirement(name = "bearerAuth")
public class ActivityController {

    private final ActivityService activityService;

    /**
     * Internal endpoint called by other services to record an event.
     * In production this would be secured to service-to-service calls only.
     */
    @PostMapping
    @Operation(summary = "Record a new activity event (internal)")
    public ResponseEntity<ActivityResponse> record(
            @Valid @RequestBody CreateActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(activityService.record(request));
    }

    @GetMapping("/{activityId}")
    @Operation(summary = "Get a single activity by ID")
    public ResponseEntity<ActivityResponse> getById(@PathVariable String activityId) {
        return ResponseEntity.ok(activityService.getActivity(activityId));
    }

    @GetMapping("/workspace/{workspaceId}")
    @Operation(summary = "List all activities for a workspace (newest first)")
    public ResponseEntity<PagedActivitiesResponse> listForWorkspace(
            @PathVariable String workspaceId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                activityService.getWorkspaceActivities(workspaceId, page, size));
    }

    @GetMapping("/workspace/{workspaceId}/type/{type}")
    @Operation(summary = "List workspace activities filtered by type")
    public ResponseEntity<PagedActivitiesResponse> listByType(
            @PathVariable String workspaceId,
            @PathVariable ActivityType type,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                activityService.getWorkspaceActivitiesByType(workspaceId, type, page, size));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List activities performed by a specific user")
    public ResponseEntity<PagedActivitiesResponse> listForUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(activityService.getUserActivities(userId, page, size));
    }

    @GetMapping("/workspace/{workspaceId}/stats")
    @Operation(summary = "Get activity statistics for a workspace")
    public ResponseEntity<ActivityStatsResponse> stats(@PathVariable String workspaceId) {
        return ResponseEntity.ok(activityService.getStats(workspaceId));
    }

    @DeleteMapping("/workspace/{workspaceId}")
    @Operation(summary = "Purge all activity records for a workspace (admin)")
    public ResponseEntity<Void> purge(@PathVariable String workspaceId) {
        activityService.purgeWorkspaceActivities(workspaceId);
        return ResponseEntity.noContent().build();
    }
}
