package com.zdslkj.listener;


import com.zdslkj.config.RabbitMQConfig;
import com.zdslkj.controller.DisplayController;
import com.zdslkj.entity.DataStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class DataQueueListener {

    private static final Logger logger =  LoggerFactory.getLogger(DataQueueListener.class);
    @Resource
    private DisplayController displayController;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveDataFromQueue(Boolean msg) {
        logger.info("接收到消息队列中的消息：{}", msg);
        displayController.receiveData(msg);
    }
}