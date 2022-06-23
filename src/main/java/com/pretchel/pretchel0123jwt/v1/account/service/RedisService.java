package com.pretchel.pretchel0123jwt.v1.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/*
* 로그인할 때 리프레시 저장
*
* */

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;

    public void setValues(String token, String email, Long expirationTime) {
//        redisTemplate.opsForValue()
//                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue()
                .set("RT:" + email, token, expirationTime, TimeUnit.MILLISECONDS);
    }

    public String getValues(String email) {
        //         String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
        return (String) redisTemplate.opsForValue().get("RT:" + email);
    }
}
