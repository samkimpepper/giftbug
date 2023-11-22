package com.pretchel.pretchel0123jwt.modules.gift.repository;


import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pretchel.pretchel0123jwt.modules.event.domain.QEvent.event;
import static com.pretchel.pretchel0123jwt.modules.gift.domain.QGift.*;
import static com.pretchel.pretchel0123jwt.modules.payments.message.QMessage.*;
import static com.querydsl.core.types.ExpressionUtils.count;


@Repository
@RequiredArgsConstructor
public class GiftQdslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<Gift> findByDeadLine() {
        return jpaQueryFactory.selectFrom(gift)
                .where(Expressions.currentDate().after(gift.deadLine)).fetch();
    }

    public List<Gift> findByDeadLineFetchJoin() {
        return jpaQueryFactory.selectFrom(gift)
                .join(gift.event, event).fetchJoin()
                .join(event.users).fetchJoin()
                .where(Expressions.currentDate().after(gift.deadLine))
                .limit(1000)
                .fetch();
    }

//    public List<Gift> findGiftsWithMostMessages() {
//        JPAQuery<Gift> query = jpaQueryFactory
//                .select(gift)
//                .from(gift)
//                .leftJoin(message).on(gift.id.eq(message.gift.id))
//                .groupBy(gift.id)
//                .orderBy(message.id.count().desc());
//
//        return query.fetch();
//    }

//    public List<Gift> findGiftsWithMostMessages() {
//        JPAQuery<Gift> query = jpaQueryFactory
//                .select(message.gift)
//                .from(message)
//                .leftJoin(gift)
//                .on(gift.id.eq(message.gift.id))
//                .fetchJoin()
//                .groupBy(gift.id)
//                .orderBy(message.id.count().desc());
//
//        return query.fetch();
//    }
    public List<Gift> findGiftsWithMostMessages() {
        return jpaQueryFactory
                .select(message.gift)
                .from(message)
                .groupBy(message.gift.id)
                .orderBy(message.id.count().desc())
                .fetch();
    }


}