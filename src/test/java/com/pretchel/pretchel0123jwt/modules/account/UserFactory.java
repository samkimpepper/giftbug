package com.pretchel.pretchel0123jwt.modules.account;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class UserFactory {

    @Autowired
    UserRepository userRepository;

    public Users createUser(String email) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(sdf.parse("2022-08-21").getTime());

        Users user = Users.builder()
                .email(email)
                .password("password")
                .birthday(date)
                .phoneNumber("01012345678")
                .gender("FEMALE")
                .build();

        userRepository.save(user);
        return user;
    }
}
