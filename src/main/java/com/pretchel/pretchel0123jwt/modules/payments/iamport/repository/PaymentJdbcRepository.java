package com.pretchel.pretchel0123jwt.modules.payments.iamport.repository;

import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void batchUpdatePayments(List<IamportPayment> payments) {
        String sql = "INSERT INTO iamport_payment (merchant_uid, imp_uid, amount, status, gift_id, create_date, modified_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

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
    }
}
