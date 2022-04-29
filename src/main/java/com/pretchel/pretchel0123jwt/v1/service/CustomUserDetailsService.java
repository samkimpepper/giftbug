package com.pretchel.pretchel0123jwt.v1.service;

import com.pretchel.pretchel0123jwt.entity.Users;
import com.pretchel.pretchel0123jwt.v1.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usersRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다"));

    }

    private UserDetails createUserDetails(Users users) {
        return new User(users.getUsername(), users.getPassword(), users.getAuthorities());
    }
}
