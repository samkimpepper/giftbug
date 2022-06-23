package com.pretchel.pretchel0123jwt.v1.account.service;

import com.pretchel.pretchel0123jwt.v1.account.domain.Users;
import com.pretchel.pretchel0123jwt.v1.account.dto.account.AccountMapping;
import com.pretchel.pretchel0123jwt.global.Response;
import com.pretchel.pretchel0123jwt.v1.account.repository.AccountRepository;
import com.pretchel.pretchel0123jwt.v1.account.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UsersRepository usersRepository;
    private final Response responseDto;
    private final UsersService usersService;

    @Transactional
    public ResponseEntity<?> getAllMyAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        Users users = usersService.getUsersByEmail(userDetails);
        if(users == null) {
            responseDto.fail("존재하지 않는 유저임 ㅠㅠ", HttpStatus.BAD_REQUEST);
        }

        List<AccountMapping> accounts = accountRepository.findAllByUserId(users);

        return responseDto.success(accounts, "계좌들", HttpStatus.OK);
    }
}
