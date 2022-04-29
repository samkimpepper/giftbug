package com.pretchel.pretchel0123jwt.v1.dto.gift;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class GiftRequestDto {

    @Getter
    @Setter
    public static class Save {
        private String name;
        private int price;
        //private MultipartFile Image;
        private String link;
        private String story;
        private String eventId;
        private String accountId;
        private String addressId;
    }
}
