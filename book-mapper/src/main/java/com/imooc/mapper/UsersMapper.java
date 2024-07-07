package com.imooc.mapper;

import com.imooc.dto.UpdatedUserDTO;
import com.imooc.my.mapper.MyMapper;
import com.imooc.pojo.Users;
import org.apache.catalina.User;
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
    void updateByUsers(Users user);
}