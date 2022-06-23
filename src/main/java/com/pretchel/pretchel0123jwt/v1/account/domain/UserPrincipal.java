package com.pretchel.pretchel0123jwt.v1.account.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Getter
public class UserPrincipal implements OAuth2User {
    private String id;
    private String email;
    private String password;
    private List<String> roles = new ArrayList<>();
    private Map<String, Object> attributes;

    public UserPrincipal(String id, String email, String password, List<String> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public static UserPrincipal create(Users users) {
        return new UserPrincipal(
                users.getId(),
                users.getEmail(),
                users.getPassword(),
                Collections.singletonList(Authority.ROLE_USER.name())
        );
    }

    public static UserPrincipal create(Users users, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(users);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }


}
