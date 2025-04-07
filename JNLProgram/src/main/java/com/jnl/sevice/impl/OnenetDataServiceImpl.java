package com.jnl.sevice.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnl.entity.OnenetData;
import com.jnl.mapper.OnenetDataMapper;
import com.jnl.sevice.OnenetDataService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.onenetVo.OnenetBase;
import com.jnl.vo.onenetVo.OnenetMsg;
import com.jnl.vo.onenetVo.OnenetParams;
import com.jnl.vo.onenetVo.OnenetSubData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OnenetDataServiceImpl extends ServiceImpl<OnenetDataMapper, OnenetData> implements OnenetDataService {


    private static final Logger logger = LoggerFactory.getLogger(OnenetMessageParserService.class);

    //private final Executor asyncExecutor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //用来控制条件循环
    private static final AtomicBoolean atomic = new AtomicBoolean(true);

    //用于记录程序启动后，只接收消息但不处理的放空时间
    private static final long HANDLE_MSG_TIME = 13500;

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



    @Override
    @Async("asyncExecutor")
    public void parseMessage(String msg) {
        try {
            logger.info("进入消息判断：{}", msg);

            //用于记录进入消息解析的起始时间
            Long startTime = DateLocalUtils.getNowLong();
            List<OnenetMsg> messageList = new ArrayList<>();

            OnenetMsg onenetMsg = objectMapper.readValue(msg, OnenetMsg.class);

            messageDeque.put(onenetMsg);
            //用于记录双向队列的容量
            int startSize = messageDeque.size();

            while (atomic.get()) {
                //循环休眠10毫秒，防止while循环空转
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIME);

                //用于记录每次进入循环的时间
                Long endTime = DateLocalUtils.getNowLong();
                //用于进行条件判断
                long diffTime = endTime - startTime;

                //当循环时间经过2秒或双向队列的容量达到1000以上时，停止接收消息，并停止其余循环后，终止循环
                if (diffTime >= MAX_PROCESSING_TIME || messageDeque.size() >= MAX_QUEUE_SIZE) {
                    latch = new CountDownLatch(1);
                    atomic.set(false);

                    messageList = new ArrayList<>(messageDeque);

                    //清空双向队列，为下次接收消息做准备
                    messageDeque.clear();
                    //TimeUnit.MILLISECONDS.sleep(10000);
                    break;
                }

                //当循环过程中时间超过50毫秒内无新消息过来时，停止接收消息，并停止其余循环后，终止循环
                if (endTime > 50 && messageDeque.size() == startSize) {
                    latch = new CountDownLatch(1);
                    atomic.set(false);

                    messageList = new ArrayList<>(messageDeque);

                    //清空双向队列，为下次接收消息做准备
                    messageDeque.clear();
                    //TimeUnit.MILLISECONDS.sleep(10000);
                    break;
                }

                //上述条件均不满足，说明10毫秒内有新消息过来，更新双向队列容量，为下次循环判断做准备
                startSize = messageDeque.size();
            }

            //将满足上述条件的双向队列，取离当前时间最近的两条数据（因为一个设备一条数据，一个完整的消息由两条分开的消息组成）
            if (messageList.size() > 0) {
                //parseToObj(messageList);

                logger.info("当前线程：{}", Thread.currentThread().getName());

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

        logger.info("进行消息处理:{}", JSON.toJSONString(messages));

        List<OnenetBase> pendingList = messages.stream()
                .map(OnenetMsg::getSubData)
                .filter(onenetSubData -> "device_1".equals(onenetSubData.getDeviceName()))
                .map(OnenetSubData::getParams)
                .map(OnenetParams::getCsq)
                .collect(Collectors.toList());

        List<OnenetData> onenetList = new ArrayList<>(pendingList.size());

        IntStream.range(0, pendingList.size())
                .parallel()
                .forEachOrdered(index -> {
                    OnenetBase onenetBase = pendingList.get(index);
                    OnenetData onenetData = new OnenetData();
                    onenetData.setTime(onenetBase.getTime());
                    onenetList.add(onenetData);
                });

        //logger.info("onenetDataService is null: {}", onenetDataService == null);

        //atomic.set(true);
        //latch.countDown();

        if (onenetList.size() == 0){
            return;
        }

        boolean saveBatch = saveBatch(onenetList);

        if (saveBatch) {
            logger.info("存储onenet数据成功：{}", DateLocalUtils.getGiveFormatNow("yyyy-MM-dd HH:mm:ss.SSS"));
        } else {
            logger.info("存储onenet数据失败：{}", DateLocalUtils.getGiveFormatNow("yyyy-MM-dd HH:mm:ss.SSS"));
        }
    }



}
