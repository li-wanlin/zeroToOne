package com.jnl.task;


import com.alibaba.fastjson2.JSONObject;
import com.jnl.config.IoTConfig;
import com.jnl.service.impl.OnenetMessageParserService;
import com.jnl.utils.AESBase64Utils;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.onenetVo.*;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class OnenetTask {

    @Resource
    OnenetMessageParserService onenetMessageParserService;

    private static final Logger logger = LoggerFactory.getLogger(OnenetTask.class);


    //TODO need to set iotAccessId 消费组ID
    private static  String iotAccessId="VF4ZMDRetitkUoYW5551"; //VF4ZMDRetitkUoYW5551
    //TODO need to set iotSecretKey 消费组KEY
    private static  String iotSecretKey="0e03e44c7d1840009aef3cbfe69638ad"; //0e03e44c7d1840009aef3cbfe69638ad

    //TODO 订阅名称
    private static  String iotSubscriptionName="VF4ZMDRetitkUoYW5551-productJava"; //VF4ZMDRetitkUoYW5551-productJava,VF4ZMDRetitkUoYW5551-sub2


    //用于控制是否接收消息
    private CountDownLatch latch = new CountDownLatch(0);

    private final AtomicBoolean running = new AtomicBoolean(true);






    @Async("backgroundTaskExecutor")
    public void OnenetAPI(){
        try{
            if (StringUtil.isNullOrEmpty(iotAccessId)) {
                logger.error("iotAccessId is null,please input iotAccessId");
            }
            if (StringUtil.isNullOrEmpty(iotSecretKey)) {
                logger.error("iotSecretKey is null,please input iotSecretKey");
            }
            if (StringUtil.isNullOrEmpty(iotSubscriptionName)) {
                logger.error("iotSubscriptionName is null,please input iotSubscriptionName");
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

                //TODO 建议收到消息后将消息转到中间件后立即ACK。避免消息量过大导致消息过期
                IoTConsumer iotConsumer = IoTConsumer.IOTConsumerBuilder.anIOTConsumer()
                        .brokerServerUrl(IoTConfig.brokerSSLServerUrl)
                        .iotAccessId(iotAccessId)
                        .iotSecretKey(iotSecretKey)
                        .subscriptionName(iotSubscriptionName)
                        .iotMessageListener(message -> {

                            try {
                                latch.await();
                                //MessageId msgId = message.getMessageId();
                                //long publishTime = message.getPublishTime();
                                String payload = new String(message.getData());
                                IoTMessage iotMessage = JSONObject.parseObject(payload, IoTMessage.class);
                                String originalMsg = AESBase64Utils.decrypt(iotMessage.getData(), iotSecretKey.substring(8, 24));
                                //logger.info("IOT originalMsg^^^^^^^:{}", originalMsg);

                                onenetMessageParserService.parseMessage(originalMsg);
                            } catch (Exception e) {
                                logger.error("Error processing message", e);
                            }

                        }).build();


                while (running.get()){
                    iotConsumer.run();
                }


        }catch (Exception e){
            logger.error("onenet已停止获取数据,时间为:{}", DateLocalUtils.getGiveFormatNow("yyyy-MM-dd HH:mm:ss"));
        }
    }


    @PreDestroy
    public void stopTask(){
        logger.info("准备关闭onenetTask");
        running.set(false);
    }

}
