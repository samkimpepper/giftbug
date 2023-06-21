package com.pretchel.pretchel0123jwt.modules.info.repository;

import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.info.dto.account.AccountMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findAllByUsers(Users users);


    List<Account> findAllByUsersAndIsDefault(Users users, Boolean isDefault);

    @Query("select a from Account a where a.users = ?1")
    List<AccountMapping> findAllByUserId(Users users);
}
