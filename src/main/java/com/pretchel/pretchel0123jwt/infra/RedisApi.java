package com.pretchel.pretchel0123jwt.infra;

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
public class RedisApi {
    private final RedisTemplate redisTemplate;

    public void setValues(String key, String value, Long expirationTime) {
//        redisTemplate.opsForValue()
//                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue()
                .set(key, value, expirationTime, TimeUnit.MILLISECONDS);
    }

    public String getValues(String key) {
        //         String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
