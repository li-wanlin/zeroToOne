package com.stg.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stg.entity.GNSSData;
import com.stg.entity.GNSSDay;
import com.stg.entity.GNSSHour;
import com.stg.mapper.GNSSDataMapper;
import com.stg.service.impl.GNSSDataServiceImpl;
import com.stg.service.impl.GNSSDayServiceImpl;
import com.stg.service.impl.GNSSHourServiceImpl;
import com.stg.utils.DateLocalUtils;
import com.stg.utils.HttpLocalUtils;
import com.stg.vo.southVo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@Component
public class SouthTask {


    @Resource
    GNSSDataServiceImpl gnssDataService;


    @Resource
    GNSSDataMapper gnssDataMapper;


    @Resource
    GNSSDayServiceImpl gnssDayService;


    @Resource
    GNSSHourServiceImpl gnssHourService;



    private static final Logger logger = LoggerFactory.getLogger(SouthTask.class);


    private static final String platformId = "20250428";

    private static final Integer moduleId = 1;

    private static final String account = "lcnlsk";

    private static final String password = "South@smos2025";

    private static final String deviceJC01 = "牛岭水库M1";

    private static final String deviceJC02 = "牛岭水库M2";

    private static final String deviceJC03 = "牛岭水库M3";

    private static final String DataUrlPrefix = "https://smos.southgnss.com/api/v2/data/module/getRealTimeData?";

    private static final String loginUrlPrefix = "https://smos.southgnss.com/api/v2/manage/user/auth?account=";

    private static String southToken = "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3NDQ3ODE4MjEsInVzZXJJZCI6MTI1NDU2MDcsImFjY291bnQiOiJ6dW9xdWFua3kifQ.AMsYYxXD96-Qgc0AtWZ1qLvNjrPOwcvRoHg8hcg6DLazP216eDp18ipEhD9lcU5uJnWgqMbP0coX09oftfB7MQ";





    //@Scheduled(fixedRate = 1000 * 60 * 60)
    @Scheduled(cron = "0 10 * * * ?")
    @Async("asyncExecutor")
    public void GNSSAPI(){
        try{

            //从HTTP请求中获取数据源
            String urlJC01 = DataUrlPrefix + "platformId=" + platformId + "&moduleId=" + moduleId + "&deviceName=" + deviceJC01;
            String urlJC02 = DataUrlPrefix + "platformId=" + platformId + "&moduleId=" + moduleId + "&deviceName=" + deviceJC02;
            String urlJC03 = DataUrlPrefix + "platformId=" + platformId + "&moduleId=" + moduleId + "&deviceName=" + deviceJC03;
            String responseJC01 = HttpLocalUtils.sendGetRequest(urlJC01, southToken);
            String responseJC02 = HttpLocalUtils.sendGetRequest(urlJC02, southToken);
            String responseJC03 = HttpLocalUtils.sendGetRequest(urlJC03, southToken);



            ObjectMapper objectMapper = new ObjectMapper();
            GNSSReceive gnssReceiveJC01 = objectMapper.readValue(responseJC01, GNSSReceive.class);
            GNSSReceive gnssReceiveJC02 = objectMapper.readValue(responseJC02, GNSSReceive.class);
            GNSSReceive gnssReceiveJC03 = objectMapper.readValue(responseJC03, GNSSReceive.class);


            /**
             * 1000	用户未登录
             * 1002	登录信息过期
             * 1003	无权限
             * 500	服务器异常
             * 0	请求成功
             */
            if (gnssReceiveJC01.getStatus() == 500 || gnssReceiveJC02.getStatus() == 500 || gnssReceiveJC03.getStatus() == 500){
                logger.error("南方测绘数据源服务器发生异常，请联系相关人员！！！！！！！！！！！");
                return;
            }

            if(gnssReceiveJC01.getStatus() == 1003 || gnssReceiveJC02.getStatus() == 1003 || gnssReceiveJC03.getStatus() == 1003){
                logger.error("所选设备没有权限，请检查！！！！！！！！！！！！！！！");
                return;
            }
            
            
            //执行完登录操作后，用新token去请求平台数据
            if (gnssReceiveJC01.getStatus() == 1000 || gnssReceiveJC01.getStatus() == 1002
                    || gnssReceiveJC02.getStatus() == 1000 || gnssReceiveJC02.getStatus() == 1002
                    || gnssReceiveJC03.getStatus() == 1000 || gnssReceiveJC03.getStatus() == 1002){
                logger.info("南方测绘访问token过期，执行登录操作");
                //TODO
                Boolean loginSouth = loginSouth();

                //若没有登录成功，则返回，不再执行后续操作，等待下次任务执行
                if (!loginSouth){
                    return;
                }


                responseJC01 = HttpLocalUtils.sendGetRequest(urlJC01, southToken);
                responseJC02 = HttpLocalUtils.sendGetRequest(urlJC02, southToken);
                responseJC03 = HttpLocalUtils.sendGetRequest(urlJC03, southToken);

                gnssReceiveJC01 = objectMapper.readValue(responseJC01, GNSSReceive.class);
                gnssReceiveJC02 = objectMapper.readValue(responseJC02, GNSSReceive.class);
                gnssReceiveJC03 = objectMapper.readValue(responseJC03, GNSSReceive.class);
            }


            boolean presentJC01 = Optional.ofNullable(gnssReceiveJC01)
                    .map(GNSSReceive::getData)
                    .map(DataStatistic::getLatest)
                    .map(DataLatest::getStaticField)
                    .isPresent();

            boolean presentJC02 = Optional.ofNullable(gnssReceiveJC02)
                    .map(GNSSReceive::getData)
                    .map(DataStatistic::getLatest)
                    .map(DataLatest::getStaticField)
                    .isPresent();

            boolean presentJC03 = Optional.ofNullable(gnssReceiveJC03)
                    .map(GNSSReceive::getData)
                    .map(DataStatistic::getLatest)
                    .map(DataLatest::getStaticField)
                    .isPresent();

            if (presentJC01){
                gnssDataService.LocalSaveOrUpdata(gnssReceiveJC01.getData().getLatest().getStaticField(),platformId,moduleId,deviceJC01);

            }

            if (presentJC02){
                gnssDataService.LocalSaveOrUpdata(gnssReceiveJC02.getData().getLatest().getStaticField(),platformId,moduleId,deviceJC02);

            }

            if (presentJC03){
                gnssDataService.LocalSaveOrUpdata(gnssReceiveJC03.getData().getLatest().getStaticField(),platformId,moduleId,deviceJC03);

            }

        }catch (Exception e){
            logger.error("在获取南方测绘平台数据时发生异常！！！！！",e);
        }
    }







    //@Scheduled(fixedRate = 1000 * 60 * 60)
    @Scheduled(cron = "0 15 * * * ?")
    @Async("asyncExecutor")
    public void tranGNSSh(){
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);

            QueryWrapper<GNSSData> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("time",nowHour);

            List<GNSSData> gnssDatas = gnssDataMapper.selectList(queryWrapper);
            if (gnssDatas == null || gnssDatas.size() == 0){
                return;
            }

            List<GNSSData> m1List = gnssDatas.stream().filter(gnssData -> gnssData.getDeviceName() != null)
                    .filter(gnssData -> "牛岭水库M1".equals(gnssData.getDeviceName()))
                    .collect(Collectors.toList());

            List<GNSSData> m2List = gnssDatas.stream().filter(gnssData -> gnssData.getDeviceName() != null)
                    .filter(gnssData -> "牛岭水库M2".equals(gnssData.getDeviceName()))
                    .collect(Collectors.toList());

            List<GNSSData> m3List = gnssDatas.stream().filter(gnssData -> gnssData.getDeviceName() != null)
                    .filter(gnssData -> "牛岭水库M3".equals(gnssData.getDeviceName()))
                    .collect(Collectors.toList());


            GNSSHour gnssHour = new GNSSHour();
            gnssHour.setUpdateTime(DateLocalUtils.parseTimeToDate(nowHour));

            if (m1List.size() > 0){
                gnssHour.setM1X(m1List.get(0).getTargetVariationPlaneX());
                gnssHour.setM1Y(m1List.get(0).getTargetVariationPlaneY());
                gnssHour.setM1H(m1List.get(0).getTargetVariationPlaneH());
            }

            if (m2List.size() > 0){
                gnssHour.setM2X(m2List.get(0).getTargetVariationPlaneX());
                gnssHour.setM2Y(m2List.get(0).getTargetVariationPlaneY());
                gnssHour.setM2H(m2List.get(0).getTargetVariationPlaneH());
            }

            if (m3List.size() > 0){
                gnssHour.setM3X(m3List.get(0).getTargetVariationPlaneX());
                gnssHour.setM3Y(m3List.get(0).getTargetVariationPlaneY());
                gnssHour.setM3H(m3List.get(0).getTargetVariationPlaneH());
            }


            gnssHourService.saveOrUpdate(gnssHour,new LambdaUpdateWrapper<GNSSHour>()
                    .eq(GNSSHour::getUpdateTime,gnssHour.getUpdateTime()));


            logger.info("GNSS小时级数据更新成功");

        }catch (Exception e){
            logger.error("GNSS小时级数据更新发生异常",e);
        }
    }





    //@Scheduled(fixedRate = 1000 * 60 * 60)
    @Scheduled(cron = "0 20 * * * ?")
    @Async("asyncExecutor")
    public void tranGNSSd(){
        try {
            LocalDate nowDate = LocalDate.now();
            Date start = DateLocalUtils.parseLocalDateToDateStart(nowDate);
            Date end = DateLocalUtils.parseLocalDateToDateEnd(nowDate);

            QueryWrapper<GNSSData> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("time",start,end);
            queryWrapper.orderByAsc("time");

            List<GNSSData> gnssDatas = gnssDataMapper.selectList(queryWrapper);
            if (gnssDatas == null || gnssDatas.size() == 0){
                return;
            }

            List<GNSSData> m1List = gnssDatas.stream().filter(gnssData -> gnssData.getDeviceName() != null)
                    .filter(gnssData -> "牛岭水库M1".equals(gnssData.getDeviceName()))
                    .collect(Collectors.toList());

            List<GNSSData> m2List = gnssDatas.stream().filter(gnssData -> gnssData.getDeviceName() != null)
                    .filter(gnssData -> "牛岭水库M2".equals(gnssData.getDeviceName()))
                    .collect(Collectors.toList());

            List<GNSSData> m3List = gnssDatas.stream().filter(gnssData -> gnssData.getDeviceName() != null)
                    .filter(gnssData -> "牛岭水库M3".equals(gnssData.getDeviceName()))
                    .collect(Collectors.toList());


            GNSSDay gnssDay = new GNSSDay();
            gnssDay.setUpdateTime(start);
            if (m1List.size() > 0){
                gnssDay.setM1X(m1List.get(m1List.size() - 1).getTargetVariationPlaneX());
                gnssDay.setM1Y(m1List.get(m1List.size() - 1).getTargetVariationPlaneY());
                gnssDay.setM1H(m1List.get(m1List.size() - 1).getTargetVariationPlaneH());
            }

            if (m2List.size() > 0){
                gnssDay.setM2X(m2List.get(m2List.size() - 1).getTargetVariationPlaneX());
                gnssDay.setM2Y(m2List.get(m2List.size() - 1).getTargetVariationPlaneY());
                gnssDay.setM2H(m2List.get(m2List.size() - 1).getTargetVariationPlaneH());
            }

            if (m3List.size() > 0){
                gnssDay.setM3X(m3List.get(m3List.size() - 1).getTargetVariationPlaneX());
                gnssDay.setM3Y(m3List.get(m3List.size() - 1).getTargetVariationPlaneY());
                gnssDay.setM3H(m3List.get(m3List.size() - 1).getTargetVariationPlaneH());
            }


            gnssDayService.saveOrUpdate(gnssDay,new LambdaUpdateWrapper<GNSSDay>()
                    .eq(GNSSDay::getUpdateTime,gnssDay.getUpdateTime()));


            //logger.info("GNSS天级数据更新成功");

        }catch (Exception e){
            logger.error("GNSS天级数据更新发生异常",e);
        }
    }
















    /**
     * 明文密码加密步骤
     * (1) 将明文密码“ceshi”用sha1算法加密一次，加密后得到：0ff58845367298b9346506748c588e1969c44360
     *
     * (2) 将第一步加密后的密文用MD5算法再加密一次得到：cbf7042edc5c03ee950fbc2e08a5aad3
     *
     * (3) 获取当前时间戳，如：1623894968552，将第二步得到的密文作为密钥，时间戳1623894968552作为被加密内容，用HmacSHA256算法进行加密，得到最终后台需要的密码：ee2a89da23062334689df2cba2cb98ed91ebdc0207a738a0018cded922ffde39
     */
    public Boolean loginSouth(){
        try{
            // 第一步：SHA-1 加密
            String sha1Encrypted = sha1Encrypt(password);

            // 第二步：MD5 加密
            String md5Encrypted = md5Encrypt(sha1Encrypted);

            // 第三步：获取当前时间戳
            long timestamp = System.currentTimeMillis();
            String timestampStr = String.valueOf(timestamp);

            // 第四步：HmacSHA256 加密
            String hmacSha256Encrypted = hmacSha256Encrypt(timestampStr, md5Encrypted);

            // 第五步：登录到南方测绘平台
            ////https://smos.southgnss.com/api/v2/manage/user/auth?account=zuoquanky&password=b75760bbd8c8123ef481f08a510c2ef6f068bd4aed4371df6004626645be8fae&time=1744695398873
            String url = loginUrlPrefix + account + "&password=" + hmacSha256Encrypted + "&time=" + timestampStr;

            //连续请求三次，每次间隔1000毫秒，有次测试发现第一次token返回null
            HttpLocalUtils.sendUnverifiedPost(url);
            Thread.sleep(1000);

            HttpLocalUtils.sendUnverifiedPost(url);
            Thread.sleep(1000);

            String loginResponse = HttpLocalUtils.sendUnverifiedPost(url);


            ObjectMapper objectMapper = new ObjectMapper();
            SouthLoginVo southLoginVo = objectMapper.readValue(loginResponse, SouthLoginVo.class);
            if (southLoginVo.getStatus() == 0 && !StringUtils.isEmpty(southLoginVo.getData().getToken())){
                southToken = southLoginVo.getData().getToken();
                return true;
            }
            return false;

        }catch (Exception e){
            logger.error("登录南方测绘接口发生异常！！！！！",e);
        }
        return false;
    }


    // SHA-1 加密方法
    public  String sha1Encrypt(String input) throws Exception {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] sha1Bytes = sha1.digest(inputBytes);
        StringBuilder sha1Result = new StringBuilder();
        for (byte b : sha1Bytes) {
            sha1Result.append(String.format("%02x", b));
        }
        return sha1Result.toString();
    }

    // MD5 加密方法
    public  String md5Encrypt(String input) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] md5Bytes = md5.digest(inputBytes);
        StringBuilder md5Result = new StringBuilder();
        for (byte b : md5Bytes) {
            md5Result.append(String.format("%02x", b));
        }
        return md5Result.toString();
    }

    // HmacSHA256 加密方法
    public static String hmacSha256Encrypt(String input, String key) throws Exception {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(secretKeySpec);
        byte[] hmacBytes = hmacSha256.doFinal(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * hmacBytes.length);
        for (byte b : hmacBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


}
