package com.pretchel.pretchel0123jwt.modules.oauth2.service;

import com.pretchel.pretchel0123jwt.modules.oauth2.domain.OAuth2UserInfo;
import com.pretchel.pretchel0123jwt.modules.oauth2.domain.OAuth2UserInfoFactory;
import com.pretchel.pretchel0123jwt.modules.oauth2.domain.OAuth2Provider;
import com.pretchel.pretchel0123jwt.modules.account.domain.UserPrincipal;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(), oauth2User.getAttributes());
        if(StringUtils.isEmpty(oauth2UserInfo.getEmail())) {

        }

        Optional<Users> usersOptional = userRepository.findByEmail(oauth2UserInfo.getEmail());
        Users users;
        if(usersOptional.isPresent()) {
            users = usersOptional.get();
            if(!users.getProvider().equals(OAuth2Provider.valueOf(userRequest.getClientRegistration().getRegistrationId()))) {

            }
            //users = updateExistingUser(users, oauth2UserInfo);
        } else {
            users = registerNewUser(userRequest, oauth2UserInfo);
        }

        return UserPrincipal.create(users, oauth2User.getAttributes());
    }

    private Users registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo userInfo) {
        Users users = new Users();

        users.setProvider(OAuth2Provider.valueOf(userRequest.getClientRegistration().getRegistrationId()));
        users.setProviderId(userInfo.getId());
        users.setEmail(userInfo.getEmail());

        return userRepository.save(users);
    }


}
