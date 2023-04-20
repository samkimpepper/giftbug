//package com.pretchel.pretchel0123jwt.modules.payments.iamport.repository;
//
//import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.PaymentsType;
//import com.querydsl.core.types.dsl.DateTemplate;
//import com.querydsl.core.types.dsl.Expressions;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import javax.persistence.EntityManager;
//import java.time.LocalDateTime;
//
//import static com.pretchel.pretchel0123jwt.modules.payments.domain.QPayments.*;
//
//@Repository
//@RequiredArgsConstructor
//public class PaymentsQdslRepository {
//    private final JPAQueryFactory jpaQueryFactory;
//    private final EntityManager em;
//
//    public long deleteWastedPayments() {
//        LocalDateTime time = LocalDateTime.now().minusHours(2);
//        DateTemplate<LocalDateTime> twoHoursAgoTime = Expressions.dateTemplate(LocalDateTime.class, "{0}", time);
//
//        long cnt = jpaQueryFactory.delete(payments)
//                .where(payments.createdAt.before(twoHoursAgoTime)
//                    .and(payments.type.eq(PaymentsType.iamport))
//                        .and(payments.imp_uid.isNull())
//                        .and(payments.amount.isNull()))
//                .execute();
//
//        em.clear();
//        em.flush();
//
//        return cnt;
//    }
//}
