package com.pretchel.pretchel0123jwt.modules.gift.repository;


import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pretchel.pretchel0123jwt.modules.gift.domain.QGift.*;

@Repository
@RequiredArgsConstructor
public class GiftQdslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<Gift> findByDeadLine() {
        return jpaQueryFactory.selectFrom(gift)
                .where(Expressions.currentDate().after(gift.deadLine)).fetch();
    }


}
