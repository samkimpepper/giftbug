package com.pretchel.pretchel0123jwt.v1.repository;

import com.pretchel.pretchel0123jwt.entity.Address;
import com.pretchel.pretchel0123jwt.entity.Users;
import com.pretchel.pretchel0123jwt.v1.dto.address.AddressMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, String> {

    @Query("select a from Address a where a.users = ?1")
    List<AddressMapping> findAllByUserId(Users users);
}
