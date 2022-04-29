package com.pretchel.pretchel0123jwt.v1.repository;

import com.pretchel.pretchel0123jwt.entity.Account;
import com.pretchel.pretchel0123jwt.entity.Users;
import com.pretchel.pretchel0123jwt.v1.dto.account.AccountMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("select a from Account a where a.users = ?1")
    List<AccountMapping> findAllByUserId(Users users);
}
