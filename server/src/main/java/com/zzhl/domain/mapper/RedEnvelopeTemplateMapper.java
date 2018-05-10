package com.zzhl.domain.mapper;

import com.zzhl.domain.pojo.RedEnvelopeTemplate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedEnvelopeTemplateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RedEnvelopeTemplate record);

    int insertSelective(RedEnvelopeTemplate record);

    RedEnvelopeTemplate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RedEnvelopeTemplate record);

    int updateByPrimaryKey(RedEnvelopeTemplate record);

    List<RedEnvelopeTemplate> listByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);
}