package com.pretchel.pretchel0123jwt.modules.account.domain;

import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.model.BaseTime;
import com.pretchel.pretchel0123jwt.modules.oauth2.domain.OAuth2Provider;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
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
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @Column(unique = true, nullable = false)
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

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name="account_id")
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name="address_id")
    private List<Address> addresses = new ArrayList<>();

    @Column
    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;

    @Column
    private String providerId;

    public void update(Date birthday, String phoneNumber) {
        if (birthday != null)
            this.birthday = birthday;
        if (phoneNumber != null)
            this.phoneNumber = phoneNumber;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void addAccount(Account account) {
        if(account.getIsDefault()) {
            Account preDefaultAccount = getDefaultAccount();
            if(preDefaultAccount != null) {
                preDefaultAccount.setIsDefault(false);
            }
        }
        accounts.add(account);
    }

    public void addAddress(Address address) {
        if(address.getIsDefault()) {
            Address preDefaultAddress = getDefaultAddress();
            if(preDefaultAddress != null) {
                preDefaultAddress.setIsDefault(false);
            }
        }
        addresses.add(address);
    }

    public Account getDefaultAccount() {
        if(accounts.isEmpty()) {
            return null;
        }

        for(Account account : accounts) {
            if(account.getIsDefault()) {
                return account;
            }
        }

        // 일단 default=true인 account가 꼭 있을 거란 전제긴 하지만
        // 만약 그렇지 않으면 추가한 순서대로.
        return null;
    }

    public Address getDefaultAddress() {
        if(addresses.isEmpty()) {
            return null;
        }

        for(Address address : addresses) {
            if(address.getIsDefault()) {
                return address;
            }
        }

        return null;
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
