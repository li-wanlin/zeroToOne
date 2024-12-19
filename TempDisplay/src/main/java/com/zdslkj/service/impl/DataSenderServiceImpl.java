package com.zdslkj.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdslkj.config.RabbitMQConfig;
import com.zdslkj.entity.DataStreams;
import com.zdslkj.mapper.DataStreamsMapper;
import com.zdslkj.service.DataSenderService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DataSenderServiceImpl extends ServiceImpl<DataStreamsMapper,DataStreams> implements DataSenderService {
    @Resource
    DataStreamsMapper dataStreamsMapper;
    private static final Logger logger =  LoggerFactory.getLogger(DataSenderServiceImpl.class);
    private final RabbitTemplate rabbitTemplate;


    public DataSenderServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    @Override
    public void sendMessage(Boolean msg) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, msg);
    }

    @Override
    @Transactional
    public List<DataStreams> selectAllByIds(List<String> ids) {
        List<DataStreams> dataStreamsList = dataStreamsMapper.selectBatchIds(ids);

        return dataStreamsList;
    }

    @Override
    public List<DataStreams> selectList() {
        QueryWrapper<DataStreams> wrapper = new QueryWrapper<>();
        wrapper.eq("data_stream_id","1");
        List<DataStreams> dataStreams = dataStreamsMapper.selectList(wrapper);

        logger.info("获取到的数据{}",dataStreams);
        if (dataStreams == null || dataStreams.size() == 0){
            return null;
        }
        return dataStreams;
    }

    @Override
    public List<DataStreams> selectLastListByIds(List<String> ids) {
        LocalDateTime halfMonthAgo = LocalDateTime.now().minusDays(15);
        List<DataStreams> list = new ArrayList<>();
        QueryWrapper<DataStreams> wrapper = new QueryWrapper<>();
        wrapper.in("id",ids);
        wrapper.gt("update_at",halfMonthAgo);
        wrapper.orderByDesc("update_at");
        wrapper.orderByAsc("data_stream_id");
        wrapper.last("limit 10");
        list = dataStreamsMapper.selectList(wrapper);
        return list;
    }
}
