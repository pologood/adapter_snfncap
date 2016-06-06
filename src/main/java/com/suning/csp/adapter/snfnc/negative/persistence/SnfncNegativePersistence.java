package com.suning.csp.adapter.snfnc.negative.persistence;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeResponseMessage;

/**
 * 
 * 反向持久化
 * 
 * @author zhangpengcheng
 */
public class SnfncNegativePersistence {
    
    private JdbcTemplate jdbcTemplate;

    public String queryNegativeMessageId(SnfncNegativeResponseMessage negativeResponseMessage) {
        String sql = "SELECT ID FROM NEGATIVE_CLIENT_MESSAGE WHERE SERIAL_NUMBER=? ";
        return this.jdbcTemplate.queryForObject(sql, new Object[] { negativeResponseMessage.getSerialNumber() },
                String.class);
    }

    /**
     * 根据正向表ID（对应付款凭证）找到表中唯一的记录
     * 
     * @param positiveID 正向表id
     * @return 交易代码和流水号Map
     */
    public Map<String, Object> queryPayMessageByID(String positiveID) {
        String positiveSql = "SELECT BUSINESS_CODE,SERIAL_NUMBER FROM POSITIVE_CLIENT_MESSAGE WHERE ID = ? ";
        return this.jdbcTemplate.queryForMap(positiveSql, new Object[] { positiveID });
    }
    
    /**
     *   根据ID在对应采集表中找到记录的bank_code
     *
     * @param hostID 内部采集表主键
     * @param businessCode 交易代码
     * @return 银行代码
     */
    public String queryBankCodeByHostID(String hostID, String businessCode) {
        String hostSql = "SELECT BANK_CODE FROM HOST_" + businessCode + "_MESSAGE WHERE ID = ?";
        return this.jdbcTemplate.queryForObject(hostSql, new Object[] { hostID }, String.class);
    }
    
    /**
     * 
     * 查询正向代理客户代码
     *
     * @param negativeId 内部表ID
     * @return 正向代理客户代码
     */
    public String queryAgentPositiveCode(String id){
        return this.jdbcTemplate.queryForObject("SELECT AGENT_POSITIVE_CLIENT_CODE FROM HOST_MESSAGE WHERE ID=? ", new Object[]{id}, String.class);
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
