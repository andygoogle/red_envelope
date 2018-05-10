package com.zzhl.domain.mapper;

import com.zzhl.domain.pojo.RedEnvelopeRefund;

public interface RedEnvelopeRefundMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RedEnvelopeRefund record);

    int insertSelective(RedEnvelopeRefund record);

    RedEnvelopeRefund selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RedEnvelopeRefund record);

    int updateByPrimaryKey(RedEnvelopeRefund record);
}