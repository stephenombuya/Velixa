package com.velixa.activityservice.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "workspace_created", def = "{'workspaceId': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "actor_created",     def = "{'actorId': 1, 'createdAt': -1}")
})
public class Activity {

    @Id
    private String id;

    @Indexed
    private String workspaceId;

    @Indexed
    private String actorId;

    private String actorUsername;
    private String actorAvatarUrl;

    private ActivityType type;

    private String resourceId;
    private String resourceType;
    private String resourceName;

    /** Flexible extra payload — diff, metadata, etc. */
    private Map<String, Object> metadata;

    @CreatedDate
    private Instant createdAt;
}
