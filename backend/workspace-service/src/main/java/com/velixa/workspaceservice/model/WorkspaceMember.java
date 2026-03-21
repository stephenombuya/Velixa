package com.velixa.workspaceservice.model;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceMember {
    private String userId;
    private String email;
    private String displayName;
    private WorkspaceRole role;
    private Instant joinedAt;
}
