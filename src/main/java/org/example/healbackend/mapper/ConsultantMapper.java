package org.example.healbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.healbackend.bean.Consultant;

import java.util.List;

@Mapper
public interface ConsultantMapper {

    // 查询所有咨询师
    @Select("SELECT * FROM consultants")
    List<Consultant> getAllConsultants();

    // 根据ID查询一个咨询师
    @Select("SELECT * FROM consultants WHERE consultant_id = #{id}")
    Consultant getConsultantById(int id);
    @Update("UPDATE consultants SET is_online = #{status} WHERE user_id = #{userId}")
    void updateOnlineStatus(@Param("userId") int userId, @Param("status") boolean status);
}
