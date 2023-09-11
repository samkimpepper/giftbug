package com.pretchel.pretchel0123jwt.modules.info.service;

import com.pretchel.pretchel0123jwt.global.exception.BadRequestException;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.info.dto.address.AddressCreateDto;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.info.dto.address.AddressListDto;
import com.pretchel.pretchel0123jwt.modules.info.repository.AddressRepository;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public Address findById(String addressId) {
        return addressRepository.findById(addressId).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void createAddress(AddressCreateDto dto, Users user) {
        Address address = Address.builder()
                .name(dto.getName())
                .postCode(dto.getPostCode())
                .roadAddress(dto.getRoadAddress())
                .detailAddress(dto.getDetailAddress())
                .phoneNum(dto.getPhoneNum())
                .isDefault(dto.getIsDefault())
                .users(user)
                .build();

        user.addAddress(address);
        addressRepository.save(address);
    }

    @Transactional
    public List<AddressListDto> getAllMyAddresses(Users user) {

        List<Address> addressList = user.getAddresses();

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
