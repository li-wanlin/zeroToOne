package com.jnl.controller;

import com.jnl.entity.XSJData;
import com.jnl.sevice.impl.XSJDataServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class XSJDataController {

    @Resource
    XSJDataServiceImpl xsjDataService;

    @GetMapping("/insertByGive")
    public String insertByGive(){
        Boolean insert = xsjDataService.insertByGive();
        if (insert){
            return "insert success";
        }
        return "insert failed";
    }


    @GetMapping("/deleteByGiveId")
    public String deleteByGiveId(){
        Boolean delete = xsjDataService.deleteByGiveId(2);
        if (delete){
            return "delete success";
        }
        return "delete failed";
    }

    @GetMapping("/updateByGiveId")
    public String updateByGiveId(){
        Boolean update = xsjDataService.updateByGiveId(3);
        if (update){
            return "update success";
        }
        return "update failed";
    }

    @GetMapping("/selectDataById")
    public XSJData selectDataById(){
        XSJData xsjData = xsjDataService.selectDataById(1);
        if (xsjData!= null && xsjData.getId() != null){
            return xsjData;
        }
        return null;
    }


}
