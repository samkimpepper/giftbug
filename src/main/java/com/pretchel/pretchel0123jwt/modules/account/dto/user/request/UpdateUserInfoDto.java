package com.pretchel.pretchel0123jwt.modules.account.dto.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ApiModel(value = "사용자 정보 변경 요청", description = "생일 혹은 전화번호 변경")
public class UpdateUserInfoDto {
    @ApiModelProperty(value = "생일", dataType = "String", example = "2023-08-15")
    private String birthday;

    @ApiModelProperty(value = "전화번호", dataType = "String", example = "01012345678")
    private String phoneNumber;
}
