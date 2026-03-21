package com.velixa.activityservice.service;

import com.velixa.activityservice.dto.ActivityDtos.*;
import com.velixa.activityservice.exception.VelixaException;
import com.velixa.activityservice.model.Activity;
import com.velixa.activityservice.model.ActivityType;
import com.velixa.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;

    // ── Write ─────────────────────────────────────────────────────────────────

    public ActivityResponse record(CreateActivityRequest request) {
        Activity activity = Activity.builder()
                .workspaceId(request.workspaceId())
                .actorId(request.actorId())
                .actorUsername(request.actorUsername())
                .actorAvatarUrl(request.actorAvatarUrl())
                .type(request.type())
                .resourceId(request.resourceId())
                .resourceType(request.resourceType())
                .resourceName(request.resourceName())
                .metadata(request.metadata())
                .build();

        Activity saved = activityRepository.save(activity);
        log.debug("Recorded activity {} for workspace {}", request.type(), request.workspaceId());
        return toResponse(saved);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    public PagedActivitiesResponse getWorkspaceActivities(String workspaceId,
                                                           int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Activity> result = activityRepository
                .findByWorkspaceIdOrderByCreatedAtDesc(workspaceId, pageable);
        return toPagedResponse(result);
    }

    public PagedActivitiesResponse getWorkspaceActivitiesByType(String workspaceId,
                                                                  ActivityType type,
                                                                  int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Activity> result = activityRepository
                .findByWorkspaceIdAndTypeOrderByCreatedAtDesc(workspaceId, type, pageable);
        return toPagedResponse(result);
    }

    public PagedActivitiesResponse getUserActivities(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Activity> result = activityRepository
                .findByActorIdOrderByCreatedAtDesc(userId, pageable);
        return toPagedResponse(result);
    }

    public ActivityResponse getActivity(String activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> VelixaException.notFound("Activity not found: " + activityId));
        return toResponse(activity);
    }

    public ActivityStatsResponse getStats(String workspaceId) {
        Instant now = Instant.now();
        Instant minus24h = now.minus(24, ChronoUnit.HOURS);
        Instant minus7d  = now.minus(7,  ChronoUnit.DAYS);

        long total = activityRepository.countByWorkspaceIdAndCreatedAtAfter(
                workspaceId, Instant.EPOCH);
        long last24h = activityRepository.countByWorkspaceIdAndCreatedAtAfter(
                workspaceId, minus24h);
        long last7d  = activityRepository.countByWorkspaceIdAndCreatedAtAfter(
                workspaceId, minus7d);

        Map<String, Long> byType = Arrays.stream(ActivityType.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        t -> activityRepository.countByWorkspaceIdAndCreatedAtAfter(
                                workspaceId, Instant.EPOCH)
                ));

        return new ActivityStatsResponse(workspaceId, total, last24h, last7d, byType);
    }

    // ── Admin ─────────────────────────────────────────────────────────────────

    public void purgeWorkspaceActivities(String workspaceId) {
        activityRepository.deleteByWorkspaceId(workspaceId);
        log.info("Purged all activities for workspace {}", workspaceId);
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private ActivityResponse toResponse(Activity a) {
        return new ActivityResponse(
                a.getId(), a.getWorkspaceId(), a.getActorId(),
                a.getActorUsername(), a.getActorAvatarUrl(),
                a.getType(), a.getResourceId(), a.getResourceType(),
                a.getResourceName(), a.getMetadata(), a.getCreatedAt()
        );
    }

    private PagedActivitiesResponse toPagedResponse(Page<Activity> page) {
        return new PagedActivitiesResponse(
                page.getContent().stream().map(this::toResponse).collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
