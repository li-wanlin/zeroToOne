package com.jnl.task;

import com.alibaba.fastjson.JSON;
import com.jnl.entity.XSJData;
import com.jnl.mapper.XSJDataMapper;
import com.jnl.sevice.impl.XSJDataServiceImpl;
import com.jnl.utils.ConvertLocalUtils;
import com.jnl.utils.DateLocalUtils;
import com.jnl.utils.HttpLocalUtils;
import com.jnl.vo.xsjVo.XSJInsertVo;
import com.jnl.vo.xsjVo.OverviewXSJ;
import com.jnl.vo.xsjVo.ReceiveXSJ;
import com.jnl.vo.xsjVo.StatisticXSJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

//@Component
public class XSJTask {

    @Resource
    XSJDataMapper xsjDataMapper;

    @Resource
    XSJDataServiceImpl xsjDataService;


    private static final Logger logger = LoggerFactory.getLogger(XSJTask.class);

    private static final String apiPrefix = "http://120.194.40.222:9090/XSJ-1300X/statistics/";
    private static final String apiSuffix = "/0/queryReport";
    private static final String stage = "0601005210";   //水位
    private static final String powerSum = "0601005003";  //总出力
    private static final String powerOne = "0601005051";  //1号机组出力
    private static final String powerTwo = "0601005065";  //2号机组出力
    private static final String powerThree = "0601005080";  //3号机组出力


    @Scheduled(fixedRate = 1000 * 60 * 5)
    @Async("asyncExecutor")
    public void XSJAPI(){
        //http://120.194.40.222:9090/XSJ-1300X/statistics/0601005210,0601005003,0601005051,0601005065,0601005080/2025-03-19/0/queryReport
        //当前日期
        String nowDay = DateLocalUtils.getGiveFormatNow("yyyy-MM-dd");
        //完整HTTP路径
        String apiComplete = apiPrefix + stage + "," + powerSum + "," + powerOne + "," + powerTwo + "," + powerThree + "/" + nowDay + apiSuffix;
        String response = HttpLocalUtils.sendGetRequest(apiComplete);
        ReceiveXSJ receiveXSJ = JSON.parseObject(response, ReceiveXSJ.class);
        if (receiveXSJ == null || receiveXSJ.getStatus() != 200 || receiveXSJ.getData().size() ==0){
            return;
        }
        long nowMilli = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        OverviewXSJ overviewXSJ;
        //原始statisticXSJ列表
        List<StatisticXSJ> statisticXSJList;
        //筛选后stastisticXSJ列表
        List<StatisticXSJ> eligibleList;
        StatisticXSJ lastStatisticXSJ;
        XSJInsertVo xsjInsertVo = new XSJInsertVo();

        for (int i = 0; i < receiveXSJ.getData().size(); i++) {
            overviewXSJ = receiveXSJ.getData().get(i);
            statisticXSJList = overviewXSJ.getStatistics();
            //获取当天当前时间前的统计数据-倒序
            eligibleList = statisticXSJList.stream()
                    .filter(statisticXSJ -> statisticXSJ.getRealTimeValue() != null)
                    .filter(statisticXSJ -> statisticXSJ.getSec() * 100 < nowMilli)
                    .sorted(Comparator.comparing(StatisticXSJ::getSec).reversed())
                    .collect(Collectors.toList());

            if (eligibleList.size() == 0){
                continue;
            }

            //获取当天最新统计数据
            lastStatisticXSJ = eligibleList.get(0);
            xsjInsertVo.setSec(lastStatisticXSJ.getSec());
            xsjInsertVo.setTime(DateLocalUtils.parseLongToDate(lastStatisticXSJ.getSec() * 1000));

            switch(lastStatisticXSJ.getMeasureId()){
                case stage:
                    xsjInsertVo.setStage(lastStatisticXSJ.getRealTimeValue());
                    break;
                case powerSum:
                    xsjInsertVo.setPowerSum(lastStatisticXSJ.getRealTimeValue());
                    break;
                case powerOne:
                    xsjInsertVo.setPowerOne(lastStatisticXSJ.getRealTimeValue());
                    break;
                case powerTwo:
                    xsjInsertVo.setPowerTwo(lastStatisticXSJ.getRealTimeValue());
                    break;
                case powerThree:
                    xsjInsertVo.setPowerThree(lastStatisticXSJ.getRealTimeValue());
                    break;
                default:
                    break;
            }

        }
        if (xsjInsertVo.getTime() == null){
            return;
        }
        XSJData xsjData = ConvertLocalUtils.toXSJData(xsjInsertVo);
        boolean save = xsjDataService.save(xsjData);


    }

}
