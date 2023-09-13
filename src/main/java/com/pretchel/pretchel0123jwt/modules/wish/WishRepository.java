package com.pretchel.pretchel0123jwt.modules.wish;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

    @Query("SELECT w.gift FROM Wish w WHERE w.user = :user")
    List<Gift> findGiftsByUser(Users user);

    @Query("select w from Wish w left join fetch w.user where w.user = :user")
    List<Wish> findAllByUserFetchJoin(Users user);

    @Query("select w from Wish w left join fetch w.gift where w.gift = :gift")
    List<Wish> findAllByGiftFetchJoin(Gift gift);

    @Query("select w from Wish w left join fetch w.gift where w.user = :user and w.gift = :gift")
    Wish findByUserAndGift(Users user, Gift gift);
}
