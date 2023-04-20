package com.pretchel.pretchel0123jwt;

import com.pretchel.pretchel0123jwt.global.config.QuerydslConfig;
import com.pretchel.pretchel0123jwt.modules.account.domain.Authority;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import static com.pretchel.pretchel0123jwt.modules.account.domain.QUsers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
public class QuerydslTest {
    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    EntityManager em;


    @BeforeEach
    void init() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(sdf.parse("2022-08-21").getTime());

        Users user = Users.builder()
                .email("duck12@gmail.com")
            .password(encoder.encode("password"))
            .birthday(date)
            .phoneNumber("01012345678")
            .gender("FEMALE")
            .roles(Collections.singletonList(Authority.ROLE_USER.name()))
            .build();

        userRepository.save(user);
    }

    @Test
    void select() {
        Users result = jpaQueryFactory.select(users)
                .from(users).fetchOne();

        // selectFrom으로 합쳐서 쓸수도 있대.
        /*

        * fetchOne() 단 건 조회. 여러개 조회되면 에러
        * fetchFirst() 단 건 조회. 여러개 조회되면 걍 첫 번째 거 리턴.
        * fetch() 리스트
        *
        * */
    }

    @Test
    void where() {
        Users result = jpaQueryFactory.select(users)
                .from(users)
                .where(users.email.eq("duck12@gmail.com"))
                .fetchOne();

        //assertThat(result.getPhoneNumber()).isEqualTo("01012345678");
    }
}
