package com.imooc.service;

import com.imooc.dto.CommentDTO;
import com.imooc.pojo.Comment;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.CommentVO;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */
public interface CommentService {


    /**
     * 根据主键查询comment
     * @param id
     * @return
     */
    public Comment getComment(String id);

    /**
     * 删除评论
     * @param vlogId
     * @param commentUserId
     * @param commentId
     */
    public void deleteComment(String vlogId,String commentUserId,String commentId);

    /**
     * 发表评论
     * @param commentDTO
     */
    public CommentVO createComment(CommentDTO commentDTO);

    /**
     * 查询评论的列表
     * @param vlogId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryVlogComments(String userId,String vlogId,Integer page,Integer pageSize);
}
