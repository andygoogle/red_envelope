package com.zzhl.domain.mapper;

import com.zzhl.domain.pojo.PayLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PayLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PayLog record);

    int insertSelective(PayLog record);

    PayLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PayLog record);

    int updateByPrimaryKey(PayLog record);

    PayLog selectByTradeNo(@Param("tradeNo") String tradeNo);
}