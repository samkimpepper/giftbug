package com.pretchel.pretchel0123jwt.v1.repository;

import com.pretchel.pretchel0123jwt.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
}
