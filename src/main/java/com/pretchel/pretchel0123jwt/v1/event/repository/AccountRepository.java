package com.pretchel.pretchel0123jwt.v1.event.repository;

import com.pretchel.pretchel0123jwt.v1.event.domain.Account;
import com.pretchel.pretchel0123jwt.v1.account.domain.Users;
import com.pretchel.pretchel0123jwt.v1.account.dto.account.AccountMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("select a from Account a where a.users = ?1")
    List<AccountMapping> findAllByUserId(Users users);
}
