package com.zzhl.domain.mapper;

import com.zzhl.domain.pojo.Withdrawals;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Withdrawals record);

    int insertSelective(Withdrawals record);

    Withdrawals selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Withdrawals record);

    int updateByPrimaryKey(Withdrawals record);
}