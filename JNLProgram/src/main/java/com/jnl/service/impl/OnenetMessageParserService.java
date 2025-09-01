package com.jnl.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.onenetVo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
public class OnenetMessageParserService {

    private static final Logger logger = LoggerFactory.getLogger(OnenetMessageParserService.class);


    @Resource
    OnenetDev1ServiceImpl onenetDev1Service;

    @Resource
    OnenetDev2ServiceImpl onenetDev2Service;

    @Resource
    OnenetDev3ServiceImpl onenetDev3Service;

    @Resource
    OnenetDev4ServiceImpl onenetDev4Service;

    @Resource
    OnenetDev5ServiceImpl onenetDev5Service;

    @Resource
    OnenetDev6ServiceImpl onenetDev6Service;

    @Resource
    OnenetDev7ServiceImpl onenetDev7Service;



    //用来控制条件循环
    private static final AtomicBoolean atomic = new AtomicBoolean(true);

    //消息处理的最大时间（毫秒）
    private static final long MAX_PROCESSING_TIME = 2000;

    //阻塞队列的最大容量
    private static final int MAX_QUEUE_SIZE = 500;

    //线程休眠时间（毫秒）
    private static final long SLEEP_TIME = 10;

    //用于控制是否接收消息
    private CountDownLatch latch = new CountDownLatch(0);

    //用于存放数据的阻塞队列
    private final java.util.concurrent.LinkedBlockingDeque<OnenetMsg> messageDeque = new java.util.concurrent.LinkedBlockingDeque<>();

    private final ObjectMapper objectMapper = new ObjectMapper();





    @Async("backgroundTaskExecutor")
    public void parseMessage(String msg) {
        try {
            //logger.info("进入消息判断：{}", msg);

            //用于记录进入消息解析的起始时间
            Long startTime = DateLocalUtils.getNowLong();
            List<OnenetMsg> messageList = new ArrayList<>();

            OnenetMsg onenetMsg = objectMapper.readValue(msg, OnenetMsg.class);

            messageDeque.put(onenetMsg);
            //用于记录双向队列的容量
            int startSize = messageDeque.size();

            //logger.info("用于记录：atomic.get()={}、、latch={}", atomic.get(),latch);

            while (atomic.get()) {
                //循环休眠10毫秒，防止while循环空转
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIME);

                //用于记录每次进入循环的时间
                Long endTime = DateLocalUtils.getNowLong();
                //用于进行条件判断
                long diffTime = endTime - startTime;


                //logger.info("这是第一个判断：diffTime={}、、messageDeque.size()={}", diffTime,messageDeque.size());
                //当循环时间经过2秒或双向队列的容量达到1000以上时，停止接收消息，并停止其余循环后，终止循环
                if (diffTime >= MAX_PROCESSING_TIME || messageDeque.size() >= MAX_QUEUE_SIZE) {

                    latch = new CountDownLatch(1);
                    atomic.set(false);


                    TimeUnit.MILLISECONDS.sleep(50);

                    messageList = new ArrayList<>(messageDeque);

                    //清空双向队列，为下次接收消息做准备
                    messageDeque.clear();
                    //TimeUnit.MILLISECONDS.sleep(10000);
                    break;
                }


                //logger.info("这是第二个判断：diffTime={}、、messageDeque.size()={}、、startSize={}", diffTime,messageDeque.size(),startSize);
                //当循环过程中时间超过50毫秒内无新消息过来时，停止接收消息，并停止其余循环后，终止循环
                if (diffTime > 50 && messageDeque.size() == startSize) {
                    latch = new CountDownLatch(1);
                    atomic.set(false);

                    TimeUnit.MILLISECONDS.sleep(50);

                    messageList = new ArrayList<>(messageDeque);

                    //清空双向队列，为下次接收消息做准备
                    messageDeque.clear();
                    //TimeUnit.MILLISECONDS.sleep(10000);
                    break;
                }


                //logger.info("这是循环的消息信息：{}、、{}", onenetMsg.getSubData().getDeviceName(),onenetMsg.getSubData().getParams().getCsq().getTime());

                //上述条件均不满足，说明10毫秒内有新消息过来，更新双向队列容量，为下次循环判断做准备
                startSize = messageDeque.size();
            }




            //将满足上述条件的双向队列，取离当前时间最近的两条数据（因为一个设备一条数据，一个完整的消息由两条分开的消息组成）
            if (messageList.size() > 0) {
                //parseToObj(messageList);

                //logger.info("当前线程：{}", Thread.currentThread().getName());

                parseToObj(messageList);

                //数据转换结束，将条件恢复
                messageList.clear();
                atomic.set(true);
                //开启阻塞的消费者线程，开始接收消息
                latch.countDown();
            }


        } catch (Exception e) {
            logger.error("在条件循环过程中发生错误，无法解决的问题，请忽略:{}", msg, e);
            latch.countDown();
            atomic.set(true);
        }
    }


    public void parseToObj(List<OnenetMsg> messages) {
        //开始进行解析存储,以设备为维度，时间从远到近

        //logger.info("进行消息处理:{}", JSON.toJSONString(messages));

        try{
            //从接收到的消息列表中分出device_1的消息
            List<OnenetParams> pendingDev1List = messages.stream()
                    .map(OnenetMsg::getSubData)
                    .filter(onenetSubData -> "device_1".equals(onenetSubData.getDeviceName()))
                    .map(OnenetSubData::getParams)
                    .collect(Collectors.toList());

            //从接收到的消息列表中分出device_2的消息
            List<OnenetParams> pendingDev2List = messages.stream()
                    .map(OnenetMsg::getSubData)
                    .filter(onenetSubData -> "device_2".equals(onenetSubData.getDeviceName()))
                    .map(OnenetSubData::getParams)
                    .collect(Collectors.toList());

            //从接收到的消息列表中分出device_3的消息
            List<OnenetParams> pendingDev3List = messages.stream()
                    .map(OnenetMsg::getSubData)
                    .filter(onenetSubData -> "device_3".equals(onenetSubData.getDeviceName()))
                    .map(OnenetSubData::getParams)
                    .collect(Collectors.toList());

            //从接收到的消息列表中分出device_4的消息
            List<OnenetParams> pendingDev4List = messages.stream()
                    .map(OnenetMsg::getSubData)
                    .filter(onenetSubData -> "device_4".equals(onenetSubData.getDeviceName()))
                    .map(OnenetSubData::getParams)
                    .collect(Collectors.toList());

            //从接收到的消息列表中分出device_5的消息
            List<OnenetParams> pendingDev5List = messages.stream()
                    .map(OnenetMsg::getSubData)
                    .filter(onenetSubData -> "device_5".equals(onenetSubData.getDeviceName()))
                    .map(OnenetSubData::getParams)
                    .collect(Collectors.toList());

            //从接收到的消息列表中分出device_6的消息
            List<OnenetParams> pendingDev6List = messages.stream()
                    .map(OnenetMsg::getSubData)
                    .filter(onenetSubData -> "device_6".equals(onenetSubData.getDeviceName()))
                    .map(OnenetSubData::getParams)
                    .collect(Collectors.toList());

            //从接收到的消息列表中分出device_7的消息
            List<OnenetParams> pendingDev7List = messages.stream()
                    .map(OnenetMsg::getSubData)
                    .filter(onenetSubData -> "device_7".equals(onenetSubData.getDeviceName()))
                    .map(OnenetSubData::getParams)
                    .collect(Collectors.toList());


            onenetDev1Service.parseDev1MsgList(pendingDev1List);
            onenetDev2Service.parseDev2MsgList(pendingDev2List);
            onenetDev3Service.parseDev3MsgList(pendingDev3List);
            onenetDev4Service.parseDev4MsgList(pendingDev4List);
            onenetDev5Service.parseDev5MsgList(pendingDev5List);
            onenetDev6Service.parseDev6MsgList(pendingDev6List);
            onenetDev7Service.parseDev7MsgList(pendingDev7List);
        }catch (Exception e){
            logger.error("将解析后的数据存储时发生问题",e);
        }



    }






}
