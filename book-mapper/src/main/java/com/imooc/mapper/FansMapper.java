package com.imooc.mapper;

import com.github.pagehelper.Page;
import com.imooc.my.mapper.MyMapper;
import com.imooc.pojo.Fans;
import com.imooc.vo.FansVO;
import com.imooc.vo.VlogerVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FansMapper extends MyMapper<Fans> {

    /**
     * 查询我的粉丝
     * @return
     */
    Page<FansVO> queryMyFans(Map<String,Object> map);

    /**
     * 查询我的关注
     * @return
     */
    Page<VlogerVO> queryMyFollows(Map<String,Object> map);

    /**
     * 查询粉丝
     * @param fans
     * @return
     */
    Fans queryFansByFans(Fans fans);

    /**
     * 更新
     * @param vloger
     */
    void updateById(Fans vloger);

    /**
     * 新增数据
     * @param fans
     */
    @Insert("insert into fans (id, vloger_id, fan_id, is_fan_friend_of_mine) " +
            "values (#{id},#{vlogerId},#{fanId},#{isFanFriendOfMine})")
    void add(Fans fans);

    /**
     * 通过Id删除数据
     * @param id
     */
    @Delete("delete from fans where id = #{id}")
    void deleteById(String id);
}