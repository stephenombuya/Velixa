package com.velixa.workspaceservice.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "workspaces")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workspace {

    @Id
    private String id;

    @Indexed(unique = true)
    private String slug;

    private String name;
    private String description;
    private String logoUrl;
    private String ownerId;

    @Builder.Default
    private List<WorkspaceMember> members = new ArrayList<>();

    @Builder.Default
    private WorkspaceVisibility visibility = WorkspaceVisibility.PRIVATE;

    @Builder.Default
    private boolean active = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
