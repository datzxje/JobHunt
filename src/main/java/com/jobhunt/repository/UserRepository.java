package com.jobhunt.repository;

import com.jobhunt.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String username);

    Optional<User> findByRefreshTokenAndEmail(String refreshToken, String email);

    Optional<User> findByUsername(String username);
}
