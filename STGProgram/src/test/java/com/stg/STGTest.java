package com.stg;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stg.entity.DisplayText;
import com.stg.entity.OnenetDev24;
import com.stg.service.impl.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAsync
@EnableScheduling
public class STGTest {


    @Resource
    RainDayServiceImpl rainDayService;


    @Resource
    GNSSHourServiceImpl gnssHourService;

    @Resource
    GNSSDayServiceImpl gnssDayService;

    @Resource
    StageHourServiceImpl stageHourService;

    @Resource
    StageDayServiceImpl stageDayService;


    @Resource
    WaterHourServiceImpl waterHourService;

    @Resource
    WaterDayServiceImpl waterDayService;

    @Resource
    EduTrainImgServiceImpl eduTrainImgService;

    @Resource
    ProjectImgServiceImpl projectImgService;

    @Resource
    FpImgServiceImpl fpImgService;


    @Resource
    DisplayTextServiceImpl displayTextService;


    @Resource
    DisplayImgServiceImpl displayImgService;

    @Resource
    FileStoreServiceImpl fileStoreService;




    private static final Logger logger = LoggerFactory.getLogger(STGTest.class);



    @Test
    public void simTest(){
        OnenetDev24 dev24 = new OnenetDev24();
        dev24.setEC(123f);
        dev24.setDO(456f);
        dev24.setNHN(789f);

        System.out.println(dev24.getFieldValue("PH"));


    }


    @Test
    public void mapperTest(){

        System.out.println(JSON.toJSONString(fileStoreService.getById(1)));


    }



    @Test
    public void numTest(){
        Float num = 3.1415926f;
        double v = BigDecimal.valueOf(num)
                .setScale(2, RoundingMode.DOWN)
                .doubleValue();
        System.out.println(v);


    }




}
