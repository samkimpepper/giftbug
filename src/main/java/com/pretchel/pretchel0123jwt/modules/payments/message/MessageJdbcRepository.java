package com.pretchel.pretchel0123jwt.modules.payments.message;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MessageJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void batchUpdateMessages(List<Message> messages) {
        String sql = "INSERT INTO message (nickname, content, amount, payments_id, gift_id, create_date, modified_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Message message = messages.get(i);
                UUID uuid = UUID.randomUUID();
                LocalDateTime now = LocalDateTime.now();

                ps.setString(1, message.getNickname());
                ps.setString(2, message.getContent());
                ps.setInt(3, message.getAmount());
                ps.setString(4, message.getPayments().getMerchant_uid());
                ps.setString(5, message.getGift().getId());
                ps.setDate(6, Date.valueOf(now.toLocalDate()));
                ps.setDate(7, Date.valueOf(now.toLocalDate()));

            }

            @Override
            public int getBatchSize() {
                return messages.size();
            }
        });
    }
}
