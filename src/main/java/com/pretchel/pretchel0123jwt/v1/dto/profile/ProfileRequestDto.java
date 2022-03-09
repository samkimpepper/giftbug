package com.pretchel.pretchel0123jwt.v1.dto.profile;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public class ProfileRequestDto {

    @Getter
    @Setter
    public static class Save {
        private String nickName;
        private String eventType;
        private MultipartFile[] images;
    }
}
