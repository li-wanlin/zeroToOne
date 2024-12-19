package com.zdslkj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zdslkj.entity.DataStreams;

import java.util.List;

public interface DataSenderService extends IService<DataStreams> {


    void sendMessage(Boolean msg);

    List<DataStreams> selectAllByIds(List<String> ids);
    
    //测试用
    List<DataStreams> selectList();


    //查询最近一次ids记录
    List<DataStreams> selectLastListByIds(List<String> ids);


}
