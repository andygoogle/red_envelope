package com.zzhl.domain.mapper;

import com.zzhl.domain.pojo.MyRedEnvelope;
import com.zzhl.domain.pojo.RedEnvelopeReceive;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedEnvelopeReceiveMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RedEnvelopeReceive record);

    int insertSelective(RedEnvelopeReceive record);

    RedEnvelopeReceive selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RedEnvelopeReceive record);

    int updateByPrimaryKey(RedEnvelopeReceive record);

    MyRedEnvelope countByUid(@Param("uid") Integer uid);

    List<MyRedEnvelope> listByUid(@Param("uid") Integer uid, @Param("offset") Integer offset, @Param("limit") Integer limit);

    List<RedEnvelopeReceive> selectByUidOrReid(@Param("uid") Integer uid, @Param("redEnvelopeId") Integer redEnvelopeId, @Param("offset") Integer offset, @Param("limit") Integer limit);
}