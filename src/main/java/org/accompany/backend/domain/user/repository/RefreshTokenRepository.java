package org.accompany.backend.domain.user.repository;

import org.accompany.backend.domain.user.entity.RefreshToken;
import org.accompany.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @EntityGraph(attributePaths = "user")
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    void deleteByUser(User user);
    void deleteByRefreshToken(String refreshToken);
}
