package com.zzhl.domain.mapper;

import com.zzhl.domain.pojo.MyRedEnvelope;
import com.zzhl.domain.pojo.RedEnvelope;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedEnvelopeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RedEnvelope record);

    int insertSelective(RedEnvelope record);

    RedEnvelope selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RedEnvelope record);

    int updateByPrimaryKey(RedEnvelope record);

    MyRedEnvelope countByUid(@Param("uid") Integer uid);

    List<MyRedEnvelope> listByUid(@Param("uid") Integer uid, @Param("offset") Integer offset, @Param("limit") Integer limit);
}