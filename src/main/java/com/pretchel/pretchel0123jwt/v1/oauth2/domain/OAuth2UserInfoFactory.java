package com.pretchel.pretchel0123jwt.v1.oauth2.domain;

import com.pretchel.pretchel0123jwt.v1.account.domain.OAuth2Provider;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(OAuth2Provider.google.toString())) {
            return new GoogleUserInfo(attributes);
        } else if(registrationId.equalsIgnoreCase(OAuth2Provider.kakao.toString())){
            return new KakaoUserInfo(attributes);
        } else {
            return null;
        }
    }
}
