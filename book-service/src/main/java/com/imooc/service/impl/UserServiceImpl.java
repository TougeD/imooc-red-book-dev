package com.imooc.service.impl;

import com.imooc.dto.UpdatedUserDTO;
import com.imooc.enums.Sex;
import com.imooc.enums.UserInfoModifyType;
import com.imooc.enums.YesOrNo;
import com.imooc.exceptions.UpdateUserErrorException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.service.UserService;
import com.imooc.utils.DateUtil;
import com.imooc.utils.DesensitizationUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    private final static String USER_FACE1 = "https://zw-sky-take-out.oss-cn-hangzhou.aliyuncs.com/0b3b6a58-9dfd-4cfd-a5f0-e8ea67093a76.jpg";

    /**
     * 根据DTO修改用户信息并返回修改后的用户
     *
     * @param updatedUserDTO
     * @return
     */
    @Override
    public Users updateUserInfo(UpdatedUserDTO updatedUserDTO,Integer type) {
        Users user = new Users();
        if(type == UserInfoModifyType.NICKNAME.type){
            //此时要修改的是昵称
            user.setNickname(updatedUserDTO.getNickname());
            Users users = usersMapper.queryUserByUser(user);
            if(users != null){
                //说明有人的昵称和要修改的昵称重合了
                throw new UpdateUserErrorException(ResponseStatusEnum.USER_INFO_UPDATED_NICKNAME_EXIST_ERROR);
            }
        }
        if(type == UserInfoModifyType.IMOOCNUM.type){
            //此时要修改的是慕课号
            user.setImoocNum(updatedUserDTO.getImoocNum());
            Users users = usersMapper.queryUserByUser(user);
            if(users != null){
                //说明有人的慕课号和要修改的慕课号重合了
                throw new UpdateUserErrorException(ResponseStatusEnum.USER_INFO_CANT_UPDATED_IMOOCNUM_ERROR);
            }
            users = getUserById(updatedUserDTO.getId());
            if(users.getCanImoocNumBeUpdated() == YesOrNo.NO.type){
                //说明就算没重合 但是由于此用户不被允许修改
                throw new UpdateUserErrorException(ResponseStatusEnum.USER_INFO_CANT_UPDATED_IMOOCNUM_ERROR);
            }
            //说明允许修改  设置下次慕课号为无法修改
            updatedUserDTO.setCanImoocNumBeUpdated(YesOrNo.NO.type);
        }
        return updateUserInfo(updatedUserDTO);
    }

    /**
     * 根据DTO修改用户信息并返回修改后的用户
     *
     * @param updatedUserDTO
     * @return
     */
    @Override
    public Users updateUserInfo(UpdatedUserDTO updatedUserDTO) {
        Users user = new Users();
        BeanUtils.copyProperties(updatedUserDTO, user);
        user.setUpdatedTime(new Date());
//        int result = usersMapper.updateByPrimaryKeySelective(user);
//        if (result != 1) {
//            throw new UpdateUserErrorException(ResponseStatusEnum.USER_INFO_UPDATE_FAILED);
//        }
        usersMapper.updateByUsers(user);
        return getUserById(updatedUserDTO.getId());
    }

    /**
     * 根据用户主键查询信息
     *
     * @param userId
     * @return
     */
    @Override
    public Users getUserById(String userId) {
//        Users user1 = usersMapper.selectByPrimaryKey(userId);
        Users user = new Users();
        user.setId(userId);
        return usersMapper.queryUserByUser(user);
    }

    /**
     * 创建用户并返回
     *
     * @param mobile
     * @return
     */
    @Transactional
    @Override
    public Users createUser(String mobile) {
        //获得全局唯一主键
        String userId = sid.nextShort();

        Users users = Users.builder()
                .id(userId)
                .mobile(mobile)
                .nickname("用户：" + DesensitizationUtil.commonDisplay(mobile))
                .imoocNum("用户：" + DesensitizationUtil.commonDisplay(mobile))
                .face(USER_FACE1)
                .birthday(DateUtil.stringToDate("1900-01-01"))
                .sex(Sex.secret.type)
                .country("中国")
                .province("")
                .city("")
                .district("")
                .description("这家伙真懒，啥都不写")
                .canImoocNumBeUpdated(YesOrNo.YES.type)
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();

        usersMapper.insert(users);

        return users;
    }

    /**
     * 查询手机号是否存在
     *
     * @param mobile
     * @return
     */
    @Override
    public Users queryMobileIsExist(String mobile) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mobile", mobile);
        Users users = usersMapper.selectOneByExample(example);
        //return usersMapper.queryUsersBymobile(mobile);
        return users;
    }
}
