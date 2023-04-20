package com.pretchel.pretchel0123jwt.global.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class MultiValueMapConverter {

    public static MultiValueMap<String, String> convert(ObjectMapper objectMapper, Object dto) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            Map<String, String> map  = objectMapper.convertValue(dto, new TypeReference<Map<String, String>>() {});
            params.setAll(map);

            return params;
        } catch (Exception e) {
            log.error("DTO -> MultiValueMap 변환 과정에서 오류 발생. DTO={}", dto, e);
            throw new IllegalStateException("DTO -> MultiValueMap 변환 과정에서 오류 발생");
       }
    }
}
