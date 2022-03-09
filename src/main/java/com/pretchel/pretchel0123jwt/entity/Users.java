package com.pretchel.pretchel0123jwt.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Users extends BaseTime implements UserDetails, OAuth2User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column
    private String password;

    @Column(nullable = false)
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Column
    private String gender;

    @Column
    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Column
    private String phoneNumber;

    @Column
    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;

    @Column
    private String providerId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="default_address")
    private Address defaultAddress;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="default_account")
    private Account defaultAccount;

    public void update(Date birthday, String phoneNumber) {
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
    }

    public void updatePassword(String password) {
        this.password = password;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return email;
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }
}
