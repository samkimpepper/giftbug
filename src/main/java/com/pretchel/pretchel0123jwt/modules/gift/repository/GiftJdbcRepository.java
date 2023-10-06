package com.pretchel.pretchel0123jwt.modules.gift.repository;

import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GiftJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void batchUpdateGifts(List<Gift> gifts) {
        String sql = "INSERT INTO gift (name, price, deadline, funded, link, story, state, event_id, account_id, address_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Gift gift = gifts.get(i);

                ps.setString(1, gift.getName());
                ps.setInt(2, gift.getPrice());
                ps.setDate(3, new java.sql.Date(gift.getDeadLine().getTime()));
                ps.setInt(4, gift.getFunded());
                ps.setString(5, gift.getLink());
                ps.setString(6, gift.getStory());
                ps.setString(7, gift.getState().toString());
                ps.setString(8, gift.getEvent().getId());
                ps.setString(9, gift.getAccount().getId());
                ps.setString(10, gift.getAddress().getId());
            }

            @Override
            public int getBatchSize() {

                return gifts.size();
            }
        });
    }
}
