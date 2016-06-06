package com.suning.csp.adapter.snfnc.positive.persistence;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gs.csp.framework.message.AbstractMessage;
import com.gs.csp.framework.toolkit.Toolkit;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;

/**
 * 正向持久化
 * 
 * @author zhangpengcheng
 */
public class SnfncPositivePersistence {

    private JdbcTemplate jdbcTemplate;

    /**
     * 日志
     */
    private Logger logger = Logger.getLogger(this.getClass());

    /**
     * 
     * 根据ID找到对应正向表的流水号
     * 
     * @param message 响应消息对象
     * @return 正向表中的流水号
     */
    public String querySerialNumber(AbstractMessage message) {
        String sql = "SElECT SERIAL_NUMBER FROM POSITIVE_CLIENT_MESSAGE WHERE ID = ?";
        try {
            return this.jdbcTemplate.queryForObject(sql, new Object[] { message.getMessageId() }, String.class);
        } catch (Exception e) {
            this.logger.error("select serial number exception: " + message, e);
            throw new RuntimeException(e);
        }
    }
    
    
    /**
     * 
     * 根据ID找到对应正向表的凭证号带回
     * 
     * @param message 响应消息对象
     * @return 正向表中的流水号
     */
    public String queryTransactVoucher(AbstractMessage message) {
        String sql = "SElECT TRANSACT_VOUCHER FROM POSITIVE_CLIENT_MESSAGE WHERE ID = ?";
        try {
            return this.jdbcTemplate.queryForObject(sql, new Object[] { message.getMessageId() }, String.class);
        } catch (Exception e) {
            this.logger.error("select serial number exception: " + message, e);
            throw new RuntimeException(e);
        }
    }
    
    public String querySerialNumber(String id) {
        String sql = "SElECT SERIAL_NUMBER FROM POSITIVE_CLIENT_MESSAGE WHERE ID = ?";
        try {
            return this.jdbcTemplate.queryForObject(sql, new Object[] { id }, String.class);
        } catch (Exception e) {
            this.logger.error("select serial number exception: " + id, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * 更新正向表的交易凭证
     * 
     * @param message 消息
     * @param transactVoucher 交易凭证
     */
    public void updateTransactVoucher(AbstractMessage message, String transactVoucher) {
        String sql = "UPDATE POSITIVE_CLIENT_MESSAGE SET TRANSACT_VOUCHER = ? WHERE ID = ? ";
        try {
            int result = this.jdbcTemplate.update(sql, new Object[] { transactVoucher, message.getMessageId() });
            if (result != 1) {
                logger.error("update transact voucher failed @result=" + result);
                throw new RuntimeException("update transact voucher fail @result= " + result);
            }
        } catch (Exception e) {
            logger.error("update transact_voucher failed, @message:" + message, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据付款凭证及交易代码找到对应付款的主键ID
     * 
     * @param message 正向请求消息对象
     * @param transactVoucher 应用端付款凭证
     * @return 对应付款的主键ID(即内部付款凭证)
     */
    public String queryPayIDByTV(SnfncPositiveRequestMessage message, String transactVoucher) {
        // 根据付款凭证以及交易代码(排除自身对应查询类的交易代码)找到唯一的付款记录
        String querySql = "SELECT ID FROM POSITIVE_CLIENT_MESSAGE WHERE TRANSACT_VOUCHER = ? AND CLIENT_CODE = ?  AND BUSINESS_CODE != ?";
        try {
            return this.jdbcTemplate.queryForObject(querySql, new Object[] { transactVoucher, message.getClientCode(),
                    message.getBusinessCode() }, String.class);
        } catch (Exception e) {
            logger.error("select pay record exception " + message, e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 根据付款凭证及交易代码找到对应付款的主键ID
     * (如，集团理财模块签约查询和申购都属于签约的查询凭证)
     * 
     * @param message 正向请求消息对象
     * @param transactVoucher 应用端付款凭证
     * @param busiCode 对应交易的编码
     * @return 对应付款的主键ID(即内部付款凭证)
     */
    public String queryPayIDByTV(SnfncPositiveRequestMessage message, String transactVoucher,String busiCode) {
        // 根据付款凭证以及自身交易代码找到唯一的付款记录
        String querySql = "SELECT ID FROM POSITIVE_CLIENT_MESSAGE WHERE TRANSACT_VOUCHER = ? AND CLIENT_CODE = ?  AND BUSINESS_CODE = ?";
        try {
            return this.jdbcTemplate.queryForObject(querySql, new Object[] { transactVoucher, message.getClientCode(),busiCode}, String.class);
        } catch (Exception e) {
            logger.error("select pay record exception " + message, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * 初始化付款单号表
     * 
     * @param message 正向请求消息对象
     * @param transactNo 付款单号
     */
    public void initializePayMessage(SnfncPositiveRequestMessage message, String transactNo) {
        try {
            String insertSql = "INSERT INTO POSITIVE_TRANSACT_STATUS (ID, BUSINESS_CODE, TRANSACT_NO, SERIAL_NUMBER, CLIENT_CODE) VALUES (?, ?, ?, ?, ?)";
            int updateResult = this.jdbcTemplate.update(insertSql, message.getMessageId(), message.getBusinessCode(),
                    transactNo, message.getSerialNumber(), message.getClientCode());
            if (updateResult != 1) {
                throw new RuntimeException("result: " + updateResult);
            }
            if (this.logger.isInfoEnabled()) {
                this.logger.info("sava positive_transact_message success: " + message);
            }
        } catch (Exception e) {
            this.logger.error("save positive_transact_message fail: " + message);
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * 根据付款单号，交易代码以及客户号查询非此笔交易的付款表记录
     * 
     * @param message 正向请求消息对象
     * @param transactNo 付款单号
     * @return 付款单号表对应的记录
     */
    public List<Map<String, Object>> queryPositiveTransactMessage(SnfncPositiveRequestMessage message, String transactNo) {
        String queryTransactIDSql = "SELECT ID FROM POSITIVE_TRANSACT_STATUS WHERE TRANSACT_NO = ? AND CLIENT_CODE = ? AND BUSINESS_CODE = ? AND ID != ? ";
        try {
            return this.jdbcTemplate.queryForList(queryTransactIDSql, new Object[] { transactNo,
                    message.getClientCode(), message.getBusinessCode(), message.getMessageId() });
        } catch (Exception e) {
            this.logger.error("query pay id exception : " + message, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * 付款状态校验 ，若不重复，返回true，若重复返回false
     * 
     * @param businessCode 内部交易代码
     * @param id 内部采集表主键ID
     * @return 校验是否重复
     */
    public boolean validatePayStatus(String businessCode, String id) {
        String queryHostSql = "SELECT REVISED_STATUS, TRANSACT_STATUS FROM HOST_" + businessCode
                + "_MESSAGE WHERE ID = ?";
        List<Map<String, Object>> result = this.jdbcTemplate.queryForList(queryHostSql, new Object[] { id });
        if (result.size() == 0) { // 找不到内部采集表记录，表示内部还未处理
            return false;
        } else if (result.size() < 0 || result.size() > 1) {
            throw new RuntimeException("result size:" + result.size());
        }
        Object transactStatus = result.get(0).get("REVISED_STATUS") == null ? (result.get(0).get("TRANSACT_STATUS")) : (result.get(0).get("REVISED_STATUS"));
        if (transactStatus != null && Toolkit.HOST_TRANSACT_STATUS_FAILURE.equals(transactStatus.toString())) { // 若对应状态为失败,返回true;
            return true;
        }
        return false;
    }
    
    
    
    public String query0051Currency(SnfncPositiveResponseMessage message)
    {
      String queryCurrencySql;
      try
      {
        queryCurrencySql = "SELECT CURRENCY FROM HOST_0051_MESSAGE WHERE ID = ?";
        return ((String)this.jdbcTemplate.queryForObject(queryCurrencySql, new Object[] { message.getMessageId() }, String.class));
      } catch (Exception e) {
        this.logger.error("query currency exception : " + message, e);
        throw new RuntimeException(e);
      }
    }

    public String query0051AccountNo(SnfncPositiveResponseMessage message)
    {
      String queryCurrencySql;
      try
      {
        queryCurrencySql = "SELECT ACCOUNT_NO FROM HOST_0051_MESSAGE WHERE ID = ?";
        return ((String)this.jdbcTemplate.queryForObject(queryCurrencySql, new Object[] { message.getMessageId() }, String.class));
      } catch (Exception e) {
        this.logger.error("query currency exception : " + message, e);
        throw new RuntimeException(e); }
    }

    public String query0051QueryDate(SnfncPositiveResponseMessage message) {
      String queryCurrencySql;
      try {
        queryCurrencySql = "SELECT QUERY_DATE FROM HOST_0051_MESSAGE WHERE ID = ?";
        return ((String)this.jdbcTemplate.queryForObject(queryCurrencySql, new Object[] { message.getMessageId() }, String.class));
      } catch (Exception e) {
        this.logger.error("query currency exception : " + message, e);
        throw new RuntimeException(e);
      }
    }

    public String query0052Currency(SnfncPositiveResponseMessage message)
    {
      String queryCurrencySql;
      try
      {
        queryCurrencySql = "SELECT CURRENCY FROM HOST_0052_MESSAGE WHERE ID = ?";
        return ((String)this.jdbcTemplate.queryForObject(queryCurrencySql, new Object[] { message.getMessageId() }, String.class));
      } catch (Exception e) {
        this.logger.error("query currency exception : " + message, e);
        throw new RuntimeException(e);
      }
    }

    public String query0052AccountNo(SnfncPositiveResponseMessage message)
    {
      String queryCurrencySql;
      try
      {
        queryCurrencySql = "SELECT ACCOUNT_NO FROM HOST_0052_MESSAGE WHERE ID = ?";
        return ((String)this.jdbcTemplate.queryForObject(queryCurrencySql, new Object[] { message.getMessageId() }, String.class));
      } catch (Exception e) {
        this.logger.error("query currency exception : " + message, e);
        throw new RuntimeException(e); }
    }

    public String query0052QueryDate(SnfncPositiveResponseMessage message) {
      String queryCurrencySql;
      try {
        queryCurrencySql = "SELECT QUERY_DATE FROM HOST_0052_MESSAGE WHERE ID = ?";
        return ((String)this.jdbcTemplate.queryForObject(queryCurrencySql, new Object[] { message.getMessageId() }, String.class));
      } catch (Exception e) {
        this.logger.error("query currency exception : " + message, e);
        throw new RuntimeException(e);
      }
    }
    /**
	 *
	 * 查询反向客户代码
	 *
	 * @param serialNumber 新消息流水号
	 * @param positiveCode 正向客户代码
	 * @param businessCode 交易代码
	 * @return  反向客户代码
	 */
	public String queryNegativeCode(String serialNumber, String positiveCode, String businessCode) {
		String sql = "SELECT NEGATIVE_CODE FROM POSITIVE_FORMER_MESSAGE WHERE SERIAL_NUMBER=? AND POSITIVE_CODE=? AND BUSINESS_CODE=? ";
		try {
			return this.jdbcTemplate.queryForObject(sql, new Object[] { serialNumber, positiveCode, businessCode }, String.class);
		} catch (Exception e) {
			logger.error("get negative code failed" + sql + " " + serialNumber+" "+positiveCode+" "+businessCode,e);
			throw new RuntimeException(e);
		}
	}
	
    /**
    *
    * 删除保存反向客户代码的临时表数据
    *
    * @param serialNumber 新消息流水号
    * @param positiveCode 正向客户代码
    * @param businessCode 交易代码
    * @return  条数
    */
   public int deletePositiveFormerMessage(String serialNumber, String positiveCode, String businessCode) {
       String sql = "DELETE FROM POSITIVE_FORMER_MESSAGE WHERE SERIAL_NUMBER=? AND POSITIVE_CODE=? AND BUSINESS_CODE=? ";
       try {
           return this.jdbcTemplate.update(sql, new Object[] { serialNumber, positiveCode, businessCode });
       } catch (Exception e) {
           logger.error("delete POSITIVE_FORMER_MESSAGE failed" + sql + " " + serialNumber+" "+positiveCode+" "+businessCode,e);
           throw new RuntimeException(e);
       }
   }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     *
     * 保存消息
     *
     * @param serialNumber 新消息流水号
     * @param positiveCode 正向客户代码
     * @param negativeCode 反向客户代码
     * @param businessCode 交易代码
     */
    public void saveMessage(String serialNumber, String positiveCode, String negativeCode, String businessCode) {
        String sql = "INSERT INTO POSITIVE_FORMER_MESSAGE( ID, SERIAL_NUMBER, POSITIVE_CODE, NEGATIVE_CODE, BUSINESS_CODE) VALUES( ?, ?, ?, ?, ?)";
        try {
            int result = this.jdbcTemplate.update(sql, new Object[] { UUID.randomUUID().toString(), serialNumber, positiveCode, negativeCode, businessCode });
            if (result != 1) throw new RuntimeException("insert positive_former_message failed @result="+result);
        } catch (Exception e) {
            logger.error("insert positive_former_message failed",e);
            throw new RuntimeException(e);
        }
    }
}
