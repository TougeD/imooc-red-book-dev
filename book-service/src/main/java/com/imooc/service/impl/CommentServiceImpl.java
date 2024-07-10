package com.imooc.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.dto.CommentDTO;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.CommentMapper;
import com.imooc.pojo.Comment;
import com.imooc.service.CommentService;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.CommentVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@Service
public class CommentServiceImpl extends BaseInfoProperties implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private Sid sid;

    /**
     * 删除评论
     * @param vlogId
     * @param commentUserId
     * @param commentId
     */
    @Override
    public void deleteComment(String vlogId, String commentUserId, String commentId) {

        Comment pendingDelete = new Comment();
        pendingDelete.setId(commentId);
        pendingDelete.setCommentUserId(commentUserId);

        commentMapper.deleteByComment(pendingDelete);

        //redis操作放在service中 评论总数的累减
        redisOperator.decrement(REDIS_VLOG_COMMENT_COUNTS + ":" + vlogId,1);

    }

    /**
     * 查询评论的列表
     * @param vlogId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PagedGridResult queryVlogComments(String userId,String vlogId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("vlogId",vlogId);

        Page<CommentVO> commentList = commentMapper.getCommentList(map);
        List<CommentVO> result = commentList.getResult();

        for(CommentVO cv : result){
            String commentId = cv.getCommentId();
            //当前短视频的某个评论的点赞总数
            String countsStr = redisOperator.getHashValue(REDIS_VLOG_COMMENT_LIKED_COUNTS, commentId);
            Integer counts = 0;
            if(StringUtils.isNotBlank(countsStr)){
                counts = Integer.valueOf(countsStr);
            }
            cv.setLikeCounts(counts);

            //判断当前用户是否点赞过该评论
            String doILike = redisOperator.hget(REDIS_USER_LIKE_COMMENT, userId + ":" + commentId);
            if(StringUtils.isNotBlank(doILike) && doILike.equalsIgnoreCase("1")){
                cv.setIsLike(YesOrNo.YES.type);
            }
        }

        return setterPagedGrid(result,page);
    }

    /**
     * 发表评论
     * @param commentDTO
     */
    @Override
    public CommentVO createComment(CommentDTO commentDTO) {
        String commentId = sid.nextShort();

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setVlogId(commentDTO.getVlogId());
        comment.setVlogerId(commentDTO.getVlogerId());

        comment.setCommentUserId(commentDTO.getCommentUserId());
        comment.setFatherCommentId(commentDTO.getFatherCommentId());
        comment.setContent(commentDTO.getContent());

        comment.setLikeCounts(0);
        comment.setCreateTime(new Date());

        commentMapper.add(comment);

        //redis操作放在service中 评论总数的累加
        redisOperator.increment(REDIS_VLOG_COMMENT_COUNTS + ":" + comment.getVlogId(),1);

        //留言后的最新评论需要返回给前端进行展示
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment,commentVO);

        return commentVO;
    }
}
