package com.pretchel.pretchel0123jwt.modules.account.repository;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
}
