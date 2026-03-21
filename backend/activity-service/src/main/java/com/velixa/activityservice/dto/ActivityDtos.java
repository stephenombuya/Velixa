package com.velixa.activityservice.dto;

import com.velixa.activityservice.model.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class ActivityDtos {

    private ActivityDtos() {}

    // ── Requests ──────────────────────────────────────────────────────────────

    public record CreateActivityRequest(
            @NotBlank  String workspaceId,
            @NotBlank  String actorId,
                       String actorUsername,
                       String actorAvatarUrl,
            @NotNull   ActivityType type,
                       String resourceId,
                       String resourceType,
                       String resourceName,
                       Map<String, Object> metadata
    ) {}

    // ── Responses ─────────────────────────────────────────────────────────────

    public record ActivityResponse(
            String id,
            String workspaceId,
            String actorId,
            String actorUsername,
            String actorAvatarUrl,
            ActivityType type,
            String resourceId,
            String resourceType,
            String resourceName,
            Map<String, Object> metadata,
            Instant createdAt
    ) {}

    public record PagedActivitiesResponse(
            List<ActivityResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean last
    ) {}

    public record ActivityStatsResponse(
            String workspaceId,
            long totalActivities,
            long activitiesLast24h,
            long activitiesLast7d,
            Map<String, Long> byType
    ) {}
}
