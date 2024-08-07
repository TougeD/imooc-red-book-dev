package com.imooc;

import com.imooc.base.RabbitMQConfig;
import com.imooc.enums.MessageEnum;
import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.mo.MessageMO;
import com.imooc.service.MsgService;
import com.imooc.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api 消费者监听消息 并打印
 * @since 2024年 月 日
 */

@Component
@Slf4j
public class RabbitMQConsumer {

    @Autowired
    private MsgService msgService;

    /**
     * 监听此队列
     */
    @RabbitListener(queues = {RabbitMQConfig.QUEUE_SYS_MSG})
    public void watchQueue(String payload, Message message) {
        log.info(payload);
        MessageMO messageMO = JsonUtils.jsonToPojo(payload, MessageMO.class);

        String receivedRoutingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info(receivedRoutingKey);

        if (receivedRoutingKey.equalsIgnoreCase("sys.msg." + MessageEnum.FOLLOW_YOU.enValue)) {
            msgService.createMsg(messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.FOLLOW_YOU.type,
                    null);
        } else if (receivedRoutingKey.equalsIgnoreCase("sys.msg." + MessageEnum.LIKE_VLOG.enValue)) {
            msgService.createMsg(messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.LIKE_VLOG.type,
                    messageMO.getMsgContent());
        } else if (receivedRoutingKey.equalsIgnoreCase("sys.msg." + MessageEnum.COMMENT_VLOG.enValue)) {
            msgService.createMsg(messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.COMMENT_VLOG.type,
                    messageMO.getMsgContent());
        } else if (receivedRoutingKey.equalsIgnoreCase("sys.msg." + MessageEnum.LIKE_COMMENT.enValue)) {
            msgService.createMsg(messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.LIKE_COMMENT.type,
                    messageMO.getMsgContent());
        } else {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }
}
