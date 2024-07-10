package com.imooc.mapper;

import com.imooc.my.mapper.MyMapper;
import com.imooc.pojo.MyLikedVlog;
import org.apache.ibatis.annotations.Delete;
import org.springframework.stereotype.Repository;

@Repository
public interface MyLikedVlogMapper extends MyMapper<MyLikedVlog> {


    /**
     * 新增点赞/喜欢视频
     * @param myLikedVlog
     */
    void add(MyLikedVlog myLikedVlog);

    /**
     * 删除对应的视频
     * @param myLikedVlog
     */
    @Delete("delete from my_liked_vlog where user_id = #{userId} and vlog_id = #{vlogId}")
    void deleteByMyLikedVlog(MyLikedVlog myLikedVlog);
}