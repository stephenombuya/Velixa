package com.velixa.workspaceservice.repository;

import com.velixa.workspaceservice.model.Workspace;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends MongoRepository<Workspace, String> {

    Optional<Workspace> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Workspace> findByOwnerId(String ownerId);

    @Query("{ 'members.userId': ?0, 'active': true }")
    List<Workspace> findByMemberUserId(String userId);

    @Query("{ $or: [ { 'ownerId': ?0 }, { 'members.userId': ?0 } ], 'active': true }")
    List<Workspace> findAllAccessibleByUserId(String userId);
}
