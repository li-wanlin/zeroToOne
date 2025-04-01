package com.jnl.task;

import com.alibaba.fastjson2.JSONObject;
import com.jnl.config.IoTConfig;
import com.jnl.utils.AESBase64Utils;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.onenetVo.IoTConsumer;
import com.jnl.vo.onenetVo.IoTMessage;
import io.netty.util.internal.StringUtil;
import org.apache.pulsar.client.api.MessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

//@Component
public class OnenetTask {

    private final Executor asyncExecutor;

    private static final Logger logger = LoggerFactory.getLogger(OnenetTask.class);




    //TODO need to set iotAccessId 消费组ID
    private static  String iotAccessId="VF4ZMDRetitkUoYW5551"; //VF4ZMDRetitkUoYW5551
    //TODO need to set iotSecretKey 消费组KEY
    private static  String iotSecretKey="0e03e44c7d1840009aef3cbfe69638ad"; //0e03e44c7d1840009aef3cbfe69638ad

    //TODO 订阅名称
    private static  String iotSubscriptionName="VF4ZMDRetitkUoYW5551-sub2"; //VF4ZMDRetitkUoYW5551-productJava,VF4ZMDRetitkUoYW5551-sub2

    //用于控制是否接收消息
    private  CountDownLatch latch = new CountDownLatch(0);

    //用于存放数据的阻塞队列
    private final LinkedBlockingDeque<String> messageDeque = new LinkedBlockingDeque<>();


    //用于存放将要处理的数据
    private static  List<String> messageList;


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


    public OnenetTask(@Qualifier("asyncExecutor") Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }


    //@PostConstruct
    //@Async("asyncExecutor")
    public void OnenetAPI(String iotAccessId, String iotSecretKey,String iotSubscriptionName){
        try{
            if (StringUtil.isNullOrEmpty(iotAccessId)) {
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

            /**
             * 最终版2025.03.31
             * 消息过来后，记录当前时间startTime，将携带数据的消息放入list中，记录当前list容量startSize，并进入下列循环判断：
             * 步骤一：休眠10毫秒，防止循环空转，进行步骤二；
             * 步骤二：记录当前进入循环的时间endTime，记录diffTime=endTime-startTime的值，记录当前list容量endSize，进行步骤三；
             * 步骤三：若diffTime的值大于2000毫秒或list的容量大于1000，则阻塞消费者线程，停止接收消息，并将其他正在进行中的循环全部停止，随后跳出循环，处理当前list中的数据，处理完数据后，清空list，随后开启消费者线程继续接收消息，若不满足，进行步骤四；
             * 步骤四：若endSize与startSize的值相等，说明10毫秒内没有新消息过来，认定为没有消息堆积，阻塞消费者线程，停止接收消息，并将其他正在进行中的循环全部停止，并将其他正在进行中的循环全部停止，随后跳出循环，处理当前list中的数据，处理完数据后，清空list，随后开启消费者线程继续接收消息，若不满足，进行步骤五；
             * 步骤五：若步骤三和步骤四都不满足，说明发生了消息堆积，只是容量或时间还未达到所设标准，将endSize的值赋值给startSize，随后结束开始下一个循环；
             *
             *
             * 附：
             * 用目前框架mybatis-plus插入一条数据所需约40毫秒，循环插入一千条数据所需约40000毫秒
             * 用目前框架mybatis-plus框架直接插入一个容量为1000的list，所需约600毫秒
             */

            AtomicReference<String> dataMsg = null;


            //TODO 建议收到消息后将消息转到中间件后立即ACK。避免消息量过大导致消息过期
            IoTConsumer iotConsumer = IoTConsumer.IOTConsumerBuilder.anIOTConsumer()
                    .brokerServerUrl(IoTConfig.brokerSSLServerUrl)
                    .iotAccessId(iotAccessId)
                    .iotSecretKey(iotSecretKey)
                    .subscriptionName(iotSubscriptionName)
                    .iotMessageListener(message -> {
                        asyncExecutor.execute(() -> {
                            try {
                                latch.await();
                                MessageId msgId = message.getMessageId();
                                long publishTime = message.getPublishTime();
                                String payload = new String(message.getData());
                                IoTMessage iotMessage = JSONObject.parseObject(payload, IoTMessage.class);
                                String originalMsg = AESBase64Utils.decrypt(iotMessage.getData(), iotSecretKey.substring(8, 24));
                                logger.info("IOT consume message======>>>>>>> messageId={}, publishTime={},  payload={}",
                                        msgId, publishTime, payload);
                                logger.info("^^^^^^^^^^^^^^^^");
                                logger.info("IOT originalMsg:{}", originalMsg);
                                parseMessage(originalMsg);
                            } catch (Exception e) {
                                logger.error("Error processing message", e);
                            }
                        });
                    }).build();
            iotConsumer.run();
        }catch (Exception e){
            logger.error("onenet已停止获取数据,时间为:{}", DateLocalUtils.getGiveFormatNow("yyyy-MM-dd HH:mm:ss"));
        }
    }




    public void parseMessage(String msg){
        try{

            //用于记录进入消息解析的起始时间
            Long startTime = DateLocalUtils.getNowLong();
            messageDeque.put(msg);
            //用于记录双向队列的容量
            int startSize = messageDeque.size();


            while(atomic.get()){

                //循环休眠10毫秒，防止while循环空转
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIME);


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

                //当循环过程中时间超过50毫秒内无新消息过来时，停止接收消息，并停止其余循环后，终止循环
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
                //parseToObj(messageList);

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



    public void parseToObj(List<String> messages){
        //开始进行解析存储,以设备为维度，时间从远到近


    }


}
