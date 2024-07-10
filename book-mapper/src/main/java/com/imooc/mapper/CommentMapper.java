package com.imooc.mapper;

import com.github.pagehelper.Page;
import com.imooc.annotation.AutoFill;
import com.imooc.my.mapper.MyMapper;
import com.imooc.pojo.Comment;
import com.imooc.vo.CommentVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommentMapper extends MyMapper<Comment> {

    /**
     * 获取评论列表
     * @param map
     * @return
     */
    public Page<CommentVO> getCommentList(Map<String,Object> map);

    /**
     * 添加一条评论
     * @param comment
     */
    @Insert("insert into comment (id, vloger_id, father_comment_id, vlog_id, comment_user_id, content, like_counts, create_time) VALUES" +
            "(#{id},#{vlogerId},#{fatherCommentId},#{vlogId},#{commentUserId},#{content},#{likeCounts},#{createTime}) ")
    void add(Comment comment);

    /**
     * 删除评论
     * @param pendingDelete
     */
    @Delete("delete from comment where id = #{id} and comment_user_id = #{commentUserId}")
    void deleteByComment(Comment pendingDelete);
}