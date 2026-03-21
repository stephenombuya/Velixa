package com.velixa.authservice.repository;

import com.velixa.authservice.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(String userId);

    void deleteByToken(String token);

    long countByUserIdAndRevokedFalse(String userId);
}
