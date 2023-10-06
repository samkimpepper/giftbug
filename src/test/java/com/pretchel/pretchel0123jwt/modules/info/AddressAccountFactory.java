package com.pretchel.pretchel0123jwt.modules.info;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.info.repository.AccountRepository;
import com.pretchel.pretchel0123jwt.modules.info.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AddressAccountFactory {
    @Autowired
    AddressRepository addressRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public Address createAddress(String name, Users user, Boolean isDefault) {
        user = userRepository.findByEmailFetchJoinAddresses("duck12@gmail.com").orElseThrow();

        Address address = Address.builder()
                .name(name)
                .postCode("12345")
                .roadAddress("냥냥로 1길 1")
                .detailAddress("냥냥펀치주택 101호")
                .phoneNum("01012345678")
                .users(user)
                .isDefault(isDefault)
                .build();
        user.addAddress(address);
        addressRepository.save(address);

        return address;
    }

    public Address generateAddress(String name, Users user, Boolean isDefault) {

        Address address = Address.builder()
                .name(name)
                .postCode("12345")
                .roadAddress("냥냥로 1길 1")
                .detailAddress("냥냥펀치주택 101호")
                .phoneNum("01012345678")
                .users(user)
                .isDefault(isDefault)
                .build();

        return address;
    }


    public Account createAccount(String name, Users user, Boolean isDefault) {
        user = userRepository.findByEmailFetchJoinAccounts("duck12@gmail.com").orElseThrow();
        Account account = Account.builder()
                .name(name)
                .accountNum("1001400410014004")
                .bank("신한")
                .bankCode("020")
                .birthday("1999-01-01")
                .users(user)
                .isDefault(isDefault)
                .build();

        accountRepository.save(account);
        user.addAccount(account);
        return account;
    }

    public Account generateAccount(String name, Users user, Boolean isDefault) {
        Account account = Account.builder()
                .name(name)
                .accountNum("1001400410014004")
                .bank("신한")
                .bankCode("020")
                .birthday("1999-01-01")
                .isDefault(isDefault)
                .users(user)
                .build();

        return account;
    }

    @Transactional
    public void deleteAll() {
        addressRepository.deleteAll();
        accountRepository.deleteAll();
    }
}
