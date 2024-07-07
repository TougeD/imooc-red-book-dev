package com.imooc.service;

import com.imooc.dto.UpdatedUserDTO;
import com.imooc.pojo.Users;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */
public interface UserService {

    /**
     * 根据DTO修改用户信息并返回修改后的用户
     * @param updatedUserDTO
     * @return
     */
    public Users updateUserInfo(UpdatedUserDTO updatedUserDTO,Integer type);

    /**
     * 根据DTO修改用户信息并返回修改后的用户
     * @param updatedUserDTO
     * @return
     */
    public Users updateUserInfo(UpdatedUserDTO updatedUserDTO);

    /**
     * 根据用户主键查询用户信息
     * @param userId
     * @return
     */
    public Users getUserById(String userId);

    /**
     * 查询手机号是否存在
     * @param mobile
     * @return
     */
    public Users queryMobileIsExist(String mobile);

    /**
     * 创建用户 并返回用户对象
     * @param mobile
     * @return
     */
    public Users createUser(String mobile);
}
