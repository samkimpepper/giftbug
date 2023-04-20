package com.pretchel.pretchel0123jwt.modules.info;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
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

    @Transactional
    public Address createAddress(String name, Users user, Boolean isDefault) {
        Address address = Address.builder()
                .name(name)
                .postCode("12345")
                .roadAddress("냥냥로 1길 1")
                .detailAddress("냥냥펀치주택 101호")
                .phoneNum("01012345678")
                .users(user)
                .isDefault(isDefault)
                .build();

        addressRepository.save(address);
        if(isDefault) {
            user.setDefaultAddress(address);
        }
        return address;
    }

    public Account createAccount(String name, Users user, Boolean isDefault) {
        Account account = Account.builder()
                .name(name)
                .accountNum("1001400410014004")
                .bank("신한")
                .bankCode("020")
                .birthday("1999-01-01")
                .build();

        accountRepository.save(account);
        if(isDefault) {
            user.setDefaultAccount(account);
        }
        return account;
    }
}
