package com.jnl.controller;


import com.jnl.service.impl.CycleServiceImpl;
import com.jnl.vo.cycleVo.CycleResponse;
import com.jnl.vo.cycleVo.CycleUnit;
import com.jnl.vo.functionVo.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class CycleController {


    @Resource
    CycleServiceImpl cycleService;

    private static final Logger logger = LoggerFactory.getLogger(CycleController.class);



    @PostMapping("/fourTotal/Cycle/insertByInfo")
    public Meta insertByInfo(@RequestBody CycleUnit unit){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("插入设备全周期数据失败");
        try {

            Boolean insert = cycleService.insertByInfo(unit);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("插入设备全周期数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("插入设备全周期数据Controller层发生异常",e);
        }
        return meta;
    }



    @DeleteMapping("/fourTotal/Cycle/deleteByInfo")
    public Meta deleteByInfo(@RequestBody CycleUnit unit){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("删除设备全周期数据失败");
        try {
            Boolean delete = cycleService.deleteByInfo(unit);
            if (delete != null && delete){
                meta.setStatus(200);
                meta.setMsg("删除设备全周期数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("删除设备全周期数据成功Controller层发生异常",e);
        }
        return meta;
    }


    @PutMapping("/fourTotal/Cycle/updateByInfo")
    public Meta updateByInfo(@RequestBody CycleUnit unit){

        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("更新设备全周期数据失败");
        try {

            Boolean insert = cycleService.updateByInfo(unit);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("更新设备全周期数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("设备全周期更新数据Controller层发生异常",e);
        }
        return meta;
    }



    @GetMapping("/fourTotal/Cycle/pagedQuery")
    public CycleResponse pagedQuery(@RequestParam(value = "pageNum") Integer pageNum,
                                    @RequestParam(value = "pageSize") Integer pageSize,
                                    @RequestParam(value = "year", required = false) String year){
        CycleResponse response = new CycleResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            CycleResponse cycleResponse = cycleService.pagedQuery(pageNum, pageSize,year);
            if (cycleResponse != null){
                return cycleResponse;
            }
        }catch (Exception e){
            logger.error("获取设备全周期分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取设备全周期分页数据失败");
        return response;

    }


}
