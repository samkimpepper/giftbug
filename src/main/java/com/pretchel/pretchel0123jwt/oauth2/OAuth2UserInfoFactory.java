package com.pretchel.pretchel0123jwt.oauth2;

import com.pretchel.pretchel0123jwt.entity.OAuth2Provider;
import org.springframework.mail.MailParseException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

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
