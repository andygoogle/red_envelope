package com.zzhl.domain.mapper;

import com.zzhl.domain.pojo.UserExtra;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExtraMapper {
    int deleteByPrimaryKey(Integer uid);

    int insert(UserExtra record);

    int insertSelective(UserExtra record);

    UserExtra selectByPrimaryKey(Integer uid);

    int updateByPrimaryKeySelective(UserExtra record);

    int updateByPrimaryKey(UserExtra record);

    void updateBalance(@Param("uid") Integer uid, @Param("amount") Integer amount, @Param("modifyTime") long modifyTime);
}