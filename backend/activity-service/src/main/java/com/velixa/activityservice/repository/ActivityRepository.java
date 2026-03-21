package com.velixa.activityservice.repository;

import com.velixa.activityservice.model.Activity;
import com.velixa.activityservice.model.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {

    Page<Activity> findByWorkspaceIdOrderByCreatedAtDesc(String workspaceId, Pageable pageable);

    Page<Activity> findByWorkspaceIdAndTypeOrderByCreatedAtDesc(
            String workspaceId, ActivityType type, Pageable pageable);

    Page<Activity> findByActorIdOrderByCreatedAtDesc(String actorId, Pageable pageable);

    List<Activity> findByWorkspaceIdAndCreatedAtAfterOrderByCreatedAtDesc(
            String workspaceId, Instant since);

    long countByWorkspaceIdAndCreatedAtAfter(String workspaceId, Instant since);

    void deleteByWorkspaceId(String workspaceId);
}
