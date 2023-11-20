package com.pretchel.pretchel0123jwt.modules.payments.iamport.repository;

import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PaymentJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    private final PlatformTransactionManager transactionManager;
    public void batchUpdatePayments(List<IamportPayment> payments) {
        String sql = "INSERT INTO iamport_payment (merchant_uid, imp_uid, amount, status, gift_id, create_date, modified_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        //DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        //TransactionStatus status = transactionManager.getTransaction(def);

        try {
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    IamportPayment payment = payments.get(i);
                    LocalDateTime now = LocalDateTime.now();

                    ps.setString(1, payment.getMerchant_uid());
                    ps.setString(2, payment.getImp_uid());
                    ps.setInt(3, payment.getAmount());
                    ps.setString(4, payment.getStatus().toString());
                    ps.setString(5, payment.getGift().getId());
                    ps.setDate(6, Date.valueOf(now.toLocalDate()));
                    ps.setDate(7, Date.valueOf(now.toLocalDate()));
                }

                @Override
                public int getBatchSize() {
                    return payments.size();
                }
            });
            //transactionManager.commit(status);
            //log.info("트랜잭션 status:", status);
        } catch(Exception ex) {
            log.info("씨발새끼들아악");
            log.error(String.valueOf(ex));
            //transactionManager.rollback(status);
            throw ex;
        }
    }
}
