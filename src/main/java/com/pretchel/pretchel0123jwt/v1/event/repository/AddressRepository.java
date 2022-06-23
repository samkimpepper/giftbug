package com.pretchel.pretchel0123jwt.v1.event.repository;

import com.pretchel.pretchel0123jwt.v1.event.domain.Address;
import com.pretchel.pretchel0123jwt.v1.account.domain.Users;
import com.pretchel.pretchel0123jwt.v1.event.dto.address.AddressMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, String> {

    @Query("select a from Address a where a.users = ?1")
    List<AddressMapping> findAllByUserId(Users users);
}
