package com.velixa.activityservice.model;

public enum ActivityType {

    // Workspace
    WORKSPACE_CREATED,
    WORKSPACE_UPDATED,
    WORKSPACE_DELETED,

    // Members
    MEMBER_INVITED,
    MEMBER_REMOVED,
    MEMBER_ROLE_UPDATED,

    // Generic resource events
    RESOURCE_CREATED,
    RESOURCE_UPDATED,
    RESOURCE_DELETED,
    RESOURCE_COMMENTED,
    RESOURCE_ARCHIVED,

    // User
    USER_LOGGED_IN,
    USER_PROFILE_UPDATED
}
