package com.pretchel.pretchel0123jwt.modules.info.service;

import com.pretchel.pretchel0123jwt.global.exception.BadRequestException;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.info.dto.address.AddressCreateDto;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.info.dto.address.AddressListDto;
import com.pretchel.pretchel0123jwt.global.Response;
import com.pretchel.pretchel0123jwt.modules.info.repository.AddressRepository;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final GiftRepository giftRepository;
    private final Response responseDto;

    public Address findById(String addressId) {
        return addressRepository.findById(addressId).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void createAddress(AddressCreateDto dto, String email) {
        Users users = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("잘못된 유저 이메일"));

        // TODO: 내 생각엔 이것도 도메인에 메서드를 만들던지 하는게 나을 듯.
        Address address = Address.builder()
                .name(dto.getName())
                .postCode(dto.getPostCode())
                .roadAddress(dto.getRoadAddress())
                .detailAddress(dto.getDetailAddress())
                .phoneNum(dto.getPhoneNum())
                .users(users)
                .build();

        addressRepository.save(address);
        if(dto.getIsDefault()) {
            users.setDefaultAddress(address);
        }
    }

    @Transactional(readOnly = true)
    public List<AddressListDto> getAllMyAddresses(String email) {
        Users users = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);

        List<Address> addressList = addressRepository.findAllByUsers(users);

        return addressList
                .stream()
                .map(address -> {
                    return AddressListDto.fromAddress(address);
                })
                .collect(Collectors.toList());
    }

    public void delete(String email, String addressId) {
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        Address address = addressRepository.findById(addressId).orElseThrow(NotFoundException::new);

        if(giftRepository.existsByAddress(address)) {
            throw new BadRequestException("Gift에 묶여 있어서 삭제 불가. 메시지 뭐라 카냐");
        }
        if(!Objects.equals(address.getUsers().getEmail(), user.getEmail())) {
            throw new BadRequestException("User가 소유한 address가 아님");
        }

        addressRepository.delete(address);
    }

}
