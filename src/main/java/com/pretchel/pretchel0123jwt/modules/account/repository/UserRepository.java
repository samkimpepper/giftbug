package com.pretchel.pretchel0123jwt.modules.account.repository;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("select u from Users u left join fetch u.accounts where u.email = :email")
    Optional<Users> findByEmailFetchJoinAccounts(String email);

    @Query("select u from Users u left join fetch u.addresses where u.email = :email")
    Optional<Users> findByEmailFetchJoinAddresses(String email);

    @Query("select u from Users u left join fetch u.addresses join fetch u.accounts where u.email = :email")
    Optional<Users> findByEmailFetchJoinAccountsAndAddress(String email);
}
