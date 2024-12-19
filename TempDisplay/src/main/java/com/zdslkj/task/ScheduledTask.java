package com.zdslkj.task;

import cmcc.iot.onenet.javasdk.api.datastreams.FindDatastreamListApi;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.datastreams.DatastreamsResponse;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zdslkj.entity.DataStreams;
import com.zdslkj.mapper.ConvertMapper;
import com.zdslkj.mapper.DataStreamsMapper;
import com.zdslkj.service.DataSenderService;
import com.zdslkj.service.impl.DataSenderServiceImpl;
import com.zdslkj.vo.DataReturnVo;
import com.zdslkj.vo.DataStreamsVo;
import jdk.nashorn.internal.runtime.regexp.JoniRegExp;
import org.apache.ibatis.executor.BatchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledTask {
    @Resource
    DataSenderService dataSenderService;

    @Resource
    TransactionTemplate transactionTemplate;

    @Resource
    ConvertMapper convertMapper;

    @Resource
    DataStreamsMapper dataStreamsMapper;

    private static final Logger logger =  LoggerFactory.getLogger(ScheduledTask.class);


    @PostConstruct
    public void init() {
        sendData();
    }

    @Scheduled(fixedRate = 10000)
    public void sendData() {
        String datastreamids = "Temp1,Temp2,Temp3,Temp4,Temp5,Temp6,Temp7,Temp8,Temp9,Temp10";
        String devid = "1143712805";
        String key = "kENHXFJJ0G2L7uI7jF13yVwSuu4=";
        //logger.info("温度接口开始请求数据：");

        /**
         * 查询多个数据流
         * @param datastreamids:数据流名称 ,String
         * @param devid:设备ID,String
         * @param key:masterkey 或者 设备apikey
         */

        logger.info("开始请求数据");
        FindDatastreamListApi api = new FindDatastreamListApi(datastreamids, devid, key);
        BasicResponse<List<DatastreamsResponse>> response = api.executeApi();

        //将获取到的数据转为可在MySQL中存储的对象格式
        DataReturnVo dataReturnVo = JSON.parseObject(response.getJson(), DataReturnVo.class);
        List<DataStreamsVo> streamsVoList = dataReturnVo.getData();
        List<DataStreams> dataStreamsList = convertMapper.INSTANCE.mapList(streamsVoList);
        logger.info("数据插入前打印数据：{}",JSON.toJSONString(streamsVoList));
        logger.info("数据插入前打印数据：{}",JSON.toJSONString(dataStreamsList));

        //进行数据批量插入操作
        boolean saveBatch = dataSenderService.saveBatch(dataStreamsList);
        dataSenderService.sendMessage(saveBatch);
    }
}
