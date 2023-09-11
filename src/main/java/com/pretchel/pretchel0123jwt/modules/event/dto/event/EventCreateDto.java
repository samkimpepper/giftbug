package com.pretchel.pretchel0123jwt.modules.event.dto.event;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Builder
@ApiModel(value = "이벤트 생성 요청")
public class EventCreateDto implements Serializable {
    @ApiModelProperty(value = "이벤트 닉네임", notes = "이벤트의 프로필 닉네임 설정", dataType = "String", required = true)
    private String nickName;
    @ApiModelProperty(value = "이벤트 유형", dataType = "String", example = "생일", required = true)
    private String eventType;
    @ApiModelProperty(value = "이벤트 프로필 사진, 배경 사진", dataType = "MultipartFile")
    private MultipartFile[] images;
    @ApiModelProperty(value = "마감기한", notes = "펀딩 마감기한", dataType = "String", example = "2023-08-17", required = true)
    private String deadLine;

    public int imagesCount() {
        return images.length;
    }
}
