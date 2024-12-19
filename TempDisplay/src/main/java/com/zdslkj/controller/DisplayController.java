package com.zdslkj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zdslkj.entity.DataStreams;
import com.zdslkj.service.DataSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@RestController
public class DisplayController {
    private static final Logger logger =  LoggerFactory.getLogger(DisplayController.class);

    private HashMap<String, List<DataStreams>> mapPre = new HashMap<>();  //展示给前端

    private  HashMap<String, List<DataStreams>> mapAfter = new HashMap<>();  //后端处理用

    private List<DataStreams> list = new ArrayList<>();  //后端处理用

    private BlockingQueue<List<DataStreams>> dataQueue = new LinkedBlockingQueue<>();

    private BlockingQueue<List<DataStreams>> dataQueue1 = new ArrayBlockingQueue<>(10);


    private static final Integer Initial = 0;  //表示初始状态
    private static final Integer Refresh = 1;   //表示未在周期内的请求，显示还是上周期的内容
    private static final Integer First = 2;  //表示程序的第一次启动请求
    private static final Integer Change = 3;   //表示在周期内的请求，显示是本周期的内容
    @Resource
    private DataSenderService dataSenderService;

/*    @GetMapping(path={"/index"},produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getProducts(HttpServletResponse response) throws Exception {
        HashMap<String, Product> map = new HashMap<>();
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                while (true) {
                    logger.info("开始弹出");
                    Product take = dataQueue.take();
                    logger.info("弹出的数据：{}", take.getName());
                    emitter.send(take);
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }*/

    @GetMapping(path={"/index","/"})
    public ModelAndView getIndex() throws Exception{
        while (mapPre.size() == 0 && mapAfter.size() == 0){
            Thread.sleep(50);
        }
        while(dataQueue.size() > 1){
            dataQueue.take();
        }
        ModelAndView view = new ModelAndView();
        ObjectMapper objectMapper = new ObjectMapper();
        // 配置 ObjectMapper 以美化输出 JSON
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        Integer flag = Initial;  //flag>0，表示请求是周期请求，否则是刷新
        if (mapPre.size() == 0){
            List<DataStreams>  take = dataQueue.take();
            mapPre.put("pre",take);
            flag = First;
        }
        List<DataStreams> pre = mapPre.get("pre");
        List<DataStreams> after = mapAfter.get("after");
        if (!pre.equals(after)){
            mapPre.put("pre",after);
            flag = Change;
        }else if (pre.equals(after) && flag != First){
            flag = Refresh;
        }
        if (First.equals(flag)){
            list = mapPre.get("pre");
        }else if(Refresh.equals(flag)){
            list = mapPre.get("pre");
        }else if(Change.equals(flag)){
            list = dataQueue.take();
        }
        //List<String> collect = list.stream().map(DataStreams::getId).collect(Collectors.toList());
        // 将列表转换为 JSON 字符串
        //String dataList = objectMapper.writeValueAsString(collect);
        //logger.info("发送到前端的消息：{}", list);
        view.setViewName("index");
        view.addObject("dataList",list);
        return view;
    }


    @GetMapping("/test")
    public String test() {
        return "test";
    }


    @GetMapping("/temp")
    public String temp() {
        return "temp";
    }



    @GetMapping("/mybatisTestAll")
    public List<DataStreams> selectDataStreamsByIds(){
        List<DataStreams> dataStreams = dataSenderService.selectList();
        return dataStreams;
    }

    @GetMapping("/selectLastListByIds")
    public List<DataStreams> selectLastListByIds(){
        List<DataStreams> peek = dataQueue1.peek();
        return peek;
    }

    public void receiveData(Boolean msg) {
        logger.info("消费消息队列中的消息：{}", msg);
        List<String> stringList = new ArrayList<>(Arrays.asList("Temp1", "Temp2", "Temp3", "Temp4", "Temp5", "Temp6", "Temp7", "Temp8", "Temp9", "Temp10"));

        List<DataStreams> dataStreamsList = dataSenderService.selectLastListByIds(stringList);

        while (dataQueue1.size()>=1){
            try {
                dataQueue1.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        dataQueue1.add(dataStreamsList);
    }


    @GetMapping("/insertTest")
    public String  insertTest(){
        boolean save = false;
        DataStreams dataStreams = new DataStreams();
        dataStreams.setId("3");      //本行注释则返回insert fail
        dataStreams.setUpdateAt(new Date());
        dataStreams.setCreateTime(new Date());
        dataStreams.setCurrentValue("33");
        try {
            save = dataSenderService.save(dataStreams);
        }catch (Exception e){
            logger.error("插入数据出错:",e);
        }
        if (save){
            return "insert success";
        }
        return "insert fail";
    }


    @GetMapping("/updateTest")
    public String updateTest(){
        boolean update = false;
        UpdateWrapper<DataStreams> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("data_stream_id","2").set("id","2");//设置null则返回update fail
        try {
            update = dataSenderService.update(updateWrapper);
        }catch (Exception e){
            logger.error("更新数据出错:",e);
        }
        if (update){
            return "update success";
        }
        return "update fail";
    }


    @GetMapping("deleteTest")
    public String deleteTest(){
        boolean remove = false;
        QueryWrapper<DataStreams> wrapper = new QueryWrapper<>();
        //wrapper.eq("ks111","3");             //设置不存在的列名返回delete fail
        wrapper.eq("id","3");
        wrapper.last("limit 1");
        try {
            remove = dataSenderService.remove(wrapper);
        }catch (Exception e){
            logger.error("删除数据出错",e);
        }
        if (remove){
            return "delete success";
        }
        return "delete fail";
    }

}
