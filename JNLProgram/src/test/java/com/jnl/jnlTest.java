package com.jnl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jnl.config.IoTConfig;
import com.jnl.entity.XSJData;
import com.jnl.mapper.XSJDataMapper;
import com.jnl.sevice.impl.XSJDataServiceImpl;
import com.jnl.task.OnenetTask;
import com.jnl.utils.AESBase64Utils;
import com.jnl.utils.DateLocalUtils;
import com.jnl.utils.HttpLocalUtils;
import com.jnl.vo.onenetVo.IoTConsumer;
import com.jnl.vo.onenetVo.IoTMessage;
import com.jnl.vo.xsjVo.XSJInsertVo;
import com.jnl.vo.xsjVo.OverviewXSJ;
import com.jnl.vo.xsjVo.ReceiveXSJ;
import com.jnl.vo.xsjVo.StatisticXSJ;
import io.netty.util.internal.StringUtil;
import org.apache.pulsar.client.api.MessageId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAsync
@EnableScheduling
public class jnlTest {

    private static final Logger logger = LoggerFactory.getLogger(jnlTest.class);

    // 消息队列
    private final LinkedBlockingDeque<String> messageQueue = new LinkedBlockingDeque<>();


    private final LinkedBlockingDeque<String> messageDeque = new LinkedBlockingDeque<>();


    // 用于控制消费者是否接收消息
    private CountDownLatch latch = new CountDownLatch(0);

    private static final AtomicBoolean atomic = new AtomicBoolean(true);

    private static List messageList = null;


    // 等待新消息的最大时间（毫秒）
    private static final long MAX_WAIT_TIME_FOR_NEW_MESSAGE = 50;
    // 消息处理的最大时间（毫秒）
    private static final long MAX_PROCESSING_TIME = 2000;
    // 消息队列的最大容量
    private static final int MAX_QUEUE_SIZE = 1000;
    // 线程休眠时间（毫秒）
    private static final long SLEEP_TIME = 10;

    @Resource
    XSJDataServiceImpl xsjDataService;

    @Resource
    XSJDataMapper xsjDataMapper;

    @Resource
    OnenetTask onenetTask;


    @Test
    public void SimTest(){
        System.out.println("123456789");
    }

    @Test
    public void XSJTest() throws Exception{
        String url1 = "http://120.194.40.222:9090/XSJ-1300X/statistics/0601005210,0601005003,0601005051,0601005065,0601005080/2025-03-21/0/queryReport";
        String response = HttpLocalUtils.sendGetRequest(url1);
        ReceiveXSJ receiveXSJ = JSON.parseObject(response.toString(), ReceiveXSJ.class);
        if (receiveXSJ.getStatus() != 200 || receiveXSJ.getData().size() == 0){
            return;
        }
        long milli = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        OverviewXSJ overviewXSJ;
        //原始statisticXSJ列表
        List<StatisticXSJ> statisticXSJList;
        //筛选后的statisticXSJ列表
        List<StatisticXSJ> eligibleList;
        StatisticXSJ lastStatisticXSJ;
        XSJInsertVo xsjInsertVo = new XSJInsertVo();


        for (int i = 0; i < receiveXSJ.getData().size(); i++) {
            overviewXSJ = receiveXSJ.getData().get(i);
            statisticXSJList = overviewXSJ.getStatistics();
            eligibleList = statisticXSJList.stream()
                    .filter(statisticXSJ -> statisticXSJ.getRealTimeValue() != null)
                    .filter(statisticXSJ -> statisticXSJ.getSec() * 1000 < milli)
                    .sorted(Comparator.comparing(StatisticXSJ::getSec).reversed())
                    .collect(Collectors.toList());


            if (eligibleList.size() == 0){
                continue;
            }


            lastStatisticXSJ = eligibleList.get(0);
            xsjInsertVo.setSec(lastStatisticXSJ.getSec());
            xsjInsertVo.setTime(DateLocalUtils.parseLongToDate(lastStatisticXSJ.getSec()*1000));
            switch (lastStatisticXSJ.getMeasureId()){
                case "0601005210":
                    xsjInsertVo.setStage(lastStatisticXSJ.getRealTimeValue());
                    break;
                case "0601005003":
                    xsjInsertVo.setPowerSum(lastStatisticXSJ.getRealTimeValue());
                    break;
                case "0601005051":
                    xsjInsertVo.setPowerOne(lastStatisticXSJ.getRealTimeValue());
                    break;
                case "0601005065":
                    xsjInsertVo.setPowerTwo(lastStatisticXSJ.getRealTimeValue());
                    break;
                case "0601005080":
                    xsjInsertVo.setPowerThree(lastStatisticXSJ.getRealTimeValue());
                    break;
                default:
                    break;
            }
        }
        System.out.println(JSON.toJSONString(xsjInsertVo));
        System.out.println(DateLocalUtils.parseDateToStr(xsjInsertVo.getTime()));
        System.out.println(response);

    }


    @Test
    public void mapperTest(){
        int id = 3;
        XSJData byId = xsjDataService.getById(1);
        System.out.println(JSON.toJSONString(byId));
    }


    @Test
    public void apiConcat(){
        //http://120.194.40.222:9090/XSJ-1300X/statistics/0601005210,0601005003,0601005051,0601005065,0601005080/2025-03-19/0/queryReport
        String apiPrefix = "http://120.194.40.222:9090/XSJ-1300X/statistics/";
        String apiSuffix = "/0/queryReport";
        //水位
        String stage = "0601005210";
        //总出力
        String powerSum = "0601005003";
        //1号机组出力
        String powerOne = "0601005051";
        //2号机组出力
        String powerTwo = "0601005065";
        //3号机组出力
        String powerThree = "0601005080";

        String nowDay = DateLocalUtils.getGiveFormatNow("yyyy-MM-dd");

        String apiComplete = apiPrefix + stage + "," + powerSum + "," + powerOne + "," + powerTwo + "," + powerThree + "/" + nowDay + apiSuffix;
        System.out.println(apiComplete);
    }

    @Test
    public void StringTest(){
        String s = "VF4ZMDRetitkUoYW5551-productJava";
        System.out.println(s.substring(8,24));
    }

    @Test
    public void DateTest(){
        String timeStr = "1743037841721";
        long timestamp = Long.parseLong(timeStr);
        String toStr = DateLocalUtils.parseLongToMilli(timestamp);
        ArrayList<String> strings = new ArrayList<>();
        System.out.println(toStr);
    }

    @Test
    public void listTest() throws Exception {
        messageQueue.put("0");
        messageQueue.put("1");
        messageQueue.put("2");
        messageQueue.put("4");
        messageQueue.put("3");

        messageList = new ArrayList<String>(messageQueue);
        messageQueue.clear();
        System.out.println(JSON.toJSONString(messageList));
        System.out.println(messageList.get(3));
        System.out.println(JSON.toJSONString(messageQueue));
    }

    @Test
    public void onenetTest() throws Exception{

        //System.out.println("这是程序启动时间："+DateLocalUtils.getGiveFormatNow("yyyy-MM-dd HH:mm:ss.SSS"));

        //TODO need to set iotAccessId 消费组ID
       String iotAccessId="VF4ZMDRetitkUoYW5551"; //VF4ZMDRetitkUoYW5551
        //TODO need to set iotSecretKey 消费组KEY
        String iotSecretKey="0e03e44c7d1840009aef3cbfe69638ad"; //0e03e44c7d1840009aef3cbfe69638ad

        //TODO 订阅名称
        String iotSubscriptionName="VF4ZMDRetitkUoYW5551-sub"; //VF4ZMDRetitkUoYW5551-productJava

        onenetTask.OnenetAPI(iotAccessId,iotSecretKey,iotSubscriptionName);


        /*if (StringUtil.isNullOrEmpty(iotAccessId)) {
            logger.error("iotAccessId is null,please input iotAccessId");
            System.exit(1);
        }
        if (StringUtil.isNullOrEmpty(iotSecretKey)) {
            logger.error("iotSecretKey is null,please input iotSecretKey");
            System.exit(1);
        }
        if (StringUtil.isNullOrEmpty(iotSubscriptionName)) {
            logger.error("iotSubscriptionName is null,please input iotSubscriptionName");
            System.exit(1);
        }


        //TODO 建议收到消息后将消息转到中间件后立即ACK。避免消息量过大导致消息过期
        IoTConsumer iotConsumer = IoTConsumer.IOTConsumerBuilder.anIOTConsumer().brokerServerUrl(IoTConfig.brokerSSLServerUrl)
                .iotAccessId(iotAccessId)
                .iotSecretKey(iotSecretKey)
                .subscriptionName(iotSubscriptionName)
                .iotMessageListener(message -> {
                    try{
                        //latch.await();
                        //n.set(n.get() + 1);
                        MessageId msgId = message.getMessageId();
                        long publishTime = message.getPublishTime();
                        String payload = new String(message.getData());
                        IoTMessage iotMessage= JSONObject.parseObject(payload, IoTMessage.class);
                        String originalMsg= AESBase64Utils.decrypt(iotMessage.getData(),iotSecretKey.substring(8,24));
                        logger.info("IOT consume message======>>>>>>> messageId={}, publishTime={},  payload={}",
                                msgId, publishTime, payload);
                        logger.info("IOT originalMsg:{}",originalMsg);
                        //System.out.println("这是消息接收时间："+DateLocalUtils.getGiveFormatNow("yyyy-MM-dd HH:mm:ss.SSS"));


                        parseMessage(originalMsg);
                    }catch (Exception e){
                        Thread.currentThread().interrupt();
                        logger.error("Message listener interrupted", e);
                    }
                }).build();
        iotConsumer.run();*/
    }


    @Async("asyncExecutor")
    public void parseMessage(String msg) throws Exception{

        if (msg == null){
            System.out.println("可以停止了");
            System.out.println("#################################");
            System.out.println("#################################");
            System.out.println("#################################");
            System.out.println("#################################");
            System.out.println("#################################");
            System.out.println("#################################");

            TimeUnit.MILLISECONDS.sleep(100000);
        }


        try{

            //用于记录进入消息解析的起始时间
            Long startTime = DateLocalUtils.getNowLong();
            messageDeque.put(msg);
            //用于记录双向队列的容量
            int startSize = messageDeque.size();



            while(atomic.get()){

                //循环休眠10毫秒，防止while循环空转
                TimeUnit.MILLISECONDS.sleep(10);

                //用于记录每次进入循环的时间
                Long endTime = DateLocalUtils.getNowLong();
                //用于进行条件判断
                long diffTime = endTime - startTime;

                //当循环时间经过2秒或双向队列的容量达到1000以上时，停止接收消息，并停止其余循环后，终止循环
                if (diffTime >= MAX_PROCESSING_TIME || messageDeque.size() >= MAX_QUEUE_SIZE){
                    latch = new CountDownLatch(1);
                    atomic.set(false);

                    messageList = new ArrayList<String>(messageDeque);

                    //清空双向队列，为下次接收消息做准备
                    messageDeque.clear();
                    break;
                }

                //当循环过程中时间超过50毫秒，且10毫秒内无新消息过来时，停止接收消息，并停止其余循环后，终止循环
                if (messageDeque.size() == startSize){
                    latch = new CountDownLatch(1);
                    atomic.set(false);

                    messageList = new ArrayList<String>(messageDeque);


                    //清空双向队列，为下次接收消息做准备
                    messageDeque.clear();
                    break;
                }

                //上述条件均不满足，说明10毫秒内有新消息过来，更新双向队列容量，为下次循环判断做准备
                startSize = messageDeque.size();


            }


            //将满足上述条件的双向队列，取离当前时间最近的两条数据（因为一个设备一条数据，一个完整的消息由两条分开的消息组成）
            if (messageList.size() > 0){
                parseToObj(messageList);

                System.out.println("可以停止了");
                System.out.println("*******************************");
                System.out.println("*******************************");
                System.out.println("*******************************");
                System.out.println("*******************************");
                System.out.println("*******************************");
                System.out.println("*******************************");
                System.out.println("*******************************");
                System.out.println("*******************************");


                TimeUnit.MILLISECONDS.sleep(100000);

                //数据转换结束，将条件恢复
                messageList.clear();
                atomic.set(true);
                //开启阻塞的消费者线程，开始接收消息
                latch.countDown();
            }
        }catch (Exception e){
            logger.error("在条件循环过程中发生错误，无法解决的问题，请忽略",e);
            latch.countDown();
            atomic.set(true);
        }

    }



    public void parseToObj(List<String> messages) throws Exception{
        //开始进行解析存储,以设备为维度，时间从远到近
        TimeUnit.MILLISECONDS.sleep(40);

    }



    @Test
    public void threadTest() throws Exception {
        // 创建一个固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        for (int i = 0; i < 700; i++) {
            final int num = i;
            // 提交任务到线程池
            executorService.submit(() -> {
                try {
                    latch.await();
                    threadFuction(num);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            if (i <= 500) {
                TimeUnit.MILLISECONDS.sleep(1);
            } else  if (i > 500 && i <= 690){
                TimeUnit.MILLISECONDS.sleep(1000);
            }else {
                TimeUnit.MILLISECONDS.sleep(10000);
            }
        }

        // 关闭线程池
        executorService.shutdown();
        // 等待所有任务执行完毕
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }



    public void threadFuction(Integer num) throws Exception {



        Long startLong = DateLocalUtils.getNowLong();
        messageQueue.add(String.valueOf(num));
        int startSize = messageQueue.size();

        String first = null;


        while (atomic.get()) {
            Long whileLong = DateLocalUtils.getNowLong();
            Long endLong = whileLong - startLong;

            if (endLong > 2000 || messageQueue.size() > 200){
                latch = new CountDownLatch(1);
                atomic.set(false);
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                System.out.println("这是第" + num + "的循环中whileLong时间：" + whileLong);
                System.out.println("这是第" + num + "的循环中endLong时间：" + endLong);
                System.out.println("这是第" + num + "的循环while中nowLong时间：" + startLong);
                System.out.println("这是第" + num + "的循环while中队列容量：" + messageQueue.size());

                if (messageQueue.size() > 0){
                    first = messageQueue.getFirst();
                }

                messageQueue.clear();
                break;
            }

            if (endLong > 50 && messageQueue.size() == startSize) {
                latch = new CountDownLatch(1);
                atomic.set(false);
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                System.out.println("这是第" + num + "的循环中whileLong时间：" + whileLong);
                System.out.println("这是第" + num + "的循环中endLong时间：" + endLong);
                System.out.println("这是第" + num + "的循环while中nowLong时间：" + startLong);
                System.out.println("这是第" + num + "的循环while中队列容量：" + messageQueue.size());
                if (messageQueue.size() > 0){
                    first = messageQueue.getFirst();
                }
                messageQueue.clear();
                break;
            }
            startSize = messageQueue.size();
            TimeUnit.MILLISECONDS.sleep(10);
        }

        System.out.println("这是第" + num + "的结束循环的时间：" + DateLocalUtils.getNowLong());
        System.out.println("这是第" + num + "的循环"+JSON.toJSONString(messageQueue));


        if (first != null){
            try {

                System.out.println("这是第" + num + "的循环中first的值："+first);
                TimeUnit.MILLISECONDS.sleep(1000);

                System.out.println("符合条件的循环"+first);

                atomic.set(true);
                latch.countDown();

                System.out.println("这是第" + num + "的循环中的结束时间：" + DateLocalUtils.getNowLong());
            }catch (Exception e){
                logger.error("循环解决不了的报错，忽略");
                atomic.set(true);
                latch.countDown();
            }
        }
    }


}
