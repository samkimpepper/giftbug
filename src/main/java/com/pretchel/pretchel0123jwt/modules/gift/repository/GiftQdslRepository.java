package com.pretchel.pretchel0123jwt.modules.gift.repository;


import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.payments.message.QMessage;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.pretchel.pretchel0123jwt.modules.gift.domain.QGift.*;
import static com.pretchel.pretchel0123jwt.modules.payments.message.QMessage.*;
import static com.querydsl.core.types.ExpressionUtils.count;
import static com.querydsl.jpa.JPAExpressions.select;


@Repository
@RequiredArgsConstructor
public class GiftQdslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<Gift> findByDeadLine() {
        return jpaQueryFactory.selectFrom(gift)
                .where(Expressions.currentDate().after(gift.deadLine)).fetch();
    }

    public List<Gift> findGiftsWithMostMessages() {
        JPAQuery<Gift> query = jpaQueryFactory
                .select(gift)
                .from(gift)
                .leftJoin(message).on(gift.id.eq(message.gift.id))
                .groupBy(gift.id)
                .orderBy(message.id.count().desc());

        return query.fetch();
    }
}
