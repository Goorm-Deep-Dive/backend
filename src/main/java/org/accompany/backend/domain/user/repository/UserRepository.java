package org.accompany.backend.domain.user.repository;

import org.accompany.backend.domain.user.entity.Provider;
import org.accompany.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderAndProviderUserId(Provider provider, String providerUserId);

    @EntityGraph(attributePaths = "activeDeceasedProfile")
    @Query("select u from User u where u.userId = :userId")
    Optional<User> findByIdWithActiveDeceasedProfile(@Param("userId") Long userId);
}
