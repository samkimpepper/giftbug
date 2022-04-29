package com.pretchel.pretchel0123jwt.v1.service;

import com.pretchel.pretchel0123jwt.entity.Address;
import com.pretchel.pretchel0123jwt.entity.Users;
import com.pretchel.pretchel0123jwt.v1.dto.address.AddressMapping;
import com.pretchel.pretchel0123jwt.v1.dto.address.AddressRequestDto;
import com.pretchel.pretchel0123jwt.v1.dto.user.Response;
import com.pretchel.pretchel0123jwt.v1.repository.AddressRepository;
import com.pretchel.pretchel0123jwt.v1.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UsersRepository usersRepository;
    private final Response responseDto;
    private final UsersService usersService;

    @Transactional
    public ResponseEntity<?> getAllMyAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        Users users = usersService.getUsersByEmail(userDetails);
        if(users == null) {
            responseDto.fail("존재하지 않는 유저임 ㅠㅠ", HttpStatus.BAD_REQUEST);
        }

        List<AddressMapping> addresses = addressRepository.findAllByUserId(users);

        return responseDto.success(addresses, "배송지들", HttpStatus.OK);
    }


}
