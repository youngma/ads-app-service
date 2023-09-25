package com.ads.main.repository.template;

import com.ads.main.vo.campaign.req.RptAdClick;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdClickTemplate {

    private final JdbcTemplate jdbcTemplate;
    private int BATCH_SIZE = 1000;

    public void setBatchSize(int batchSize) {
        this.BATCH_SIZE = batchSize;
    }

    public void saveAll(List<? extends RptAdClick> items) {
        int batchCount = 0;

        List<RptAdClick> subItems = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            subItems.add(items.get(i));
            if ((i + 1) % BATCH_SIZE == 0) {
                batchCount = batchInsert(BATCH_SIZE, batchCount, subItems);
            }
        }
        if (!subItems.isEmpty()) {
            batchCount = batchInsert(BATCH_SIZE, batchCount, subItems);
        }
    }

    private int batchInsert(int batchSize, int batchCount, List<RptAdClick> subItems) {
        jdbcTemplate.batchUpdate(
        """
                insert into RPT_AD_CLICK
                 (REQUEST_ID, TARGET, USER_AGENT, REMOTE_IP, CLICK_AT)
                values
                 (? , ?, ?, ?, current_timestamp)
                on DUPLICATE KEY UPDATE
                USER_AGENT = values(USER_AGENT),
                REMOTE_IP = values(REMOTE_IP)
                """,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, subItems.get(i).requestId());
                        ps.setObject(2, subItems.get(i).target());
                        ps.setObject(3, subItems.get(i).userAgent());
                        ps.setObject(4, subItems.get(i).remoteIp());
                    }
                    @Override
                    public int getBatchSize() {
                        return subItems.size();
                    }
                });
        subItems.clear();
        batchCount++;
        return batchCount;
    }
}
