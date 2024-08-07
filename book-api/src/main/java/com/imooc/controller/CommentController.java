package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.base.RabbitMQConfig;
import com.imooc.dto.CommentDTO;
import com.imooc.enums.MessageEnum;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.mapper.CommentMapper;
import com.imooc.mapper.VlogMapper;
import com.imooc.mo.MessageMO;
import com.imooc.pojo.Comment;
import com.imooc.pojo.Vlog;
import com.imooc.service.CommentService;
import com.imooc.service.MsgService;
import com.imooc.utils.JsonUtils;
import com.imooc.vo.CommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@Slf4j
@Api(tags = "评论业务接口")
@RestController
@RequestMapping("comment")
public class CommentController extends BaseInfoProperties {

    @Autowired
    private CommentService commentService;

    @Autowired
    private MsgService msgService;

    @Autowired
    private VlogMapper vlogMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("like")
    public GraceJSONResult like( @RequestParam String commentId,
                                 @RequestParam String userId){
        //故意犯错 bigKey
        redisOperator.incrementHash(REDIS_VLOG_COMMENT_LIKED_COUNTS,commentId,1);
        redisOperator.setHashValue(REDIS_USER_LIKE_COMMENT,userId + ":" + commentId,"1");

        // 系统消息：点赞评论
        Comment comment = commentService.getComment(commentId);
        Vlog vlog = vlogMapper.selectByPrimaryKey(comment.getVlogId());

        Map msgContent = new HashMap<>();
        msgContent.put("commentId",commentId);
        msgContent.put("vlogId",vlog.getId());
        msgContent.put("vlogCover",vlog.getCover());

        MessageMO messageMO = new MessageMO();
        messageMO.setFromUserId(userId);
        messageMO.setToUserId(comment.getCommentUserId());
        messageMO.setMsgContent(msgContent);
        //优化使用MQ异步解耦

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG,"sys.msg."+MessageEnum.LIKE_COMMENT.enValue, JsonUtils.objectToJson(messageMO));
//        msgService.createMsg(userId,
//                comment.getCommentUserId(),
//                MessageEnum.LIKE_COMMENT.type, msgContent);
        return GraceJSONResult.ok();
    }

    @PostMapping("unlike")
    public GraceJSONResult unlike( @RequestParam String commentId,
                                 @RequestParam String userId){
        redisOperator.decrementHash(REDIS_VLOG_COMMENT_LIKED_COUNTS,commentId,1);
        redisOperator.hdel(REDIS_USER_LIKE_COMMENT,userId);


        return GraceJSONResult.ok();
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "删除评论")
    public GraceJSONResult delete(@RequestParam String commentUserId,
                                  @RequestParam String commentId,
                                  @RequestParam String vlogId){
//        commentService.deleteComment(commentUserId,commentId,vlogId);

        Comment pendingDelete = new Comment();
        pendingDelete.setId(commentId);
        pendingDelete.setCommentUserId(commentUserId);

        commentMapper.deleteByComment(pendingDelete);

        //redis操作放在service中 评论总数的累减
        redisOperator.decrement(REDIS_VLOG_COMMENT_COUNTS + ":" + vlogId,1);

        return GraceJSONResult.ok();
    }

    @GetMapping("list")
    @ApiOperation(value = "查询评论列表")
    public GraceJSONResult list(@RequestParam String vlogId,
                                @RequestParam(defaultValue = "") String userId,
                                @RequestParam(required = false,defaultValue = "1") Integer page,
                                @RequestParam(required = false,defaultValue = "10") Integer pageSize){

        return GraceJSONResult.ok(commentService.queryVlogComments(userId,vlogId,page,pageSize));
    }

    @PostMapping("create")
    @ApiOperation(value = "发表评论")
    public GraceJSONResult create(@RequestBody @Valid CommentDTO commentDTO){

        CommentVO commentVO = commentService.createComment(commentDTO);
        return GraceJSONResult.ok(commentVO);
    }

    @GetMapping("counts")
    @ApiOperation(value = "评论计数")
    public GraceJSONResult counts(@RequestParam String vlogId){

        String countsStr = redisOperator.get(REDIS_VLOG_COMMENT_COUNTS + ":" + vlogId);
        if(StringUtils.isBlank(countsStr)){
            countsStr = "0";
        }
        return GraceJSONResult.ok(Integer.valueOf(countsStr));
    }
}
