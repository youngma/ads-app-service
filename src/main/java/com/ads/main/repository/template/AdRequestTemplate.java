package com.ads.main.repository.template;

import com.ads.main.vo.campaign.req.RptAdRequest;
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
public class AdRequestTemplate {

    private final JdbcTemplate jdbcTemplate;
    private int BATCH_SIZE = 1000;

    public void setBatchSize(int batchSize) {
        this.BATCH_SIZE = batchSize;
    }

    public void saveAll(List<? extends RptAdRequest> items) {
        int batchCount = 0;

        List<RptAdRequest> subItems = new ArrayList<>();

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

    private int batchInsert(int batchSize, int batchCount, List<RptAdRequest> subItems) {
        jdbcTemplate.batchUpdate(
        """
                insert into RPT_AD_REQUEST
                 (REQUEST_ID, GROUP_CODE,  CAMPAIGN_CODE, USER_AGENT, REMOTE_IP, REQUEST_AT, AD_COST, USER_COMMISSION, PARTNER_COMMISSION, AD_REWORD, USER_KEY)
                values
                 (? , ?, ?, ?, ?, current_timestamp, ?, ?, ?, ?, ?)
                on DUPLICATE KEY UPDATE
                USER_AGENT = values(USER_AGENT),
                REMOTE_IP = values(REMOTE_IP)
                """,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, subItems.get(i).getRequestId());
                        ps.setObject(2, subItems.get(i).getGroupCode());
                        ps.setObject(3, subItems.get(i).getCampaignCode());
                        ps.setObject(4, subItems.get(i).getUserAgent());
                        ps.setObject(5, subItems.get(i).getRemoteIp());
                        ps.setInt(6, subItems.get(i).getAdPrice());
                        ps.setObject(7, subItems.get(i).getUserCommission());
                        ps.setObject(8, subItems.get(i).getPartnerCommission());
                        ps.setObject(9, subItems.get(i).getAdReword());
                        ps.setObject(10, subItems.get(i).getUserKey());
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
