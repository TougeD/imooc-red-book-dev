package com.imooc.mapper;


import com.imooc.annotation.AutoFill;
import com.imooc.enums.OperationType;
import com.imooc.my.mapper.MyMapper;
import com.imooc.pojo.Users;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UsersMapper extends MyMapper<Users> {

    /**
     * 通过手机号查询用户
     * @param mobile
     * @return
     */
    @Select("select * from users where mobile = #{mobile}")
    Users queryUsersBymobile(String mobile);

    /**
     * 根据已知的用户信息获取用户
     * @param user
     * @return
     */
    Users queryUserByUser(Users user);

    /**
     * 根据user修改用户信息
     * @param user
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateByUsers(Users user);

    /**
     * 插入用户
     * @param users
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into users (id, mobile, nickname, imooc_num, face, sex, birthday, country, province, city, district, description, bg_img, can_imooc_num_be_updated, created_time, updated_time) VALUES " +
            "(#{id},#{mobile},#{nickname},#{imoocNum},#{face},#{sex},#{birthday},#{country},#{province},#{city},#{district},#{description},#{bgImg},#{canImoocNumBeUpdated},#{createdTime},#{updatedTime})")
    void add(Users users);
}