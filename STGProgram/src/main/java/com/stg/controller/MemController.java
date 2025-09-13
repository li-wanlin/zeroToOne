package com.stg.controller;

import com.stg.service.impl.MemServiceImpl;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.memoirsVo.MemResponse;
import com.stg.vo.memoirsVo.MemUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class MemController {

    @Resource
    MemServiceImpl memService;


    private static final Logger logger = LoggerFactory.getLogger(MemController.class);


    @PostMapping("/fourTotal/Mem/insertByInfo")
    public Meta insertByInfo(@RequestBody MemUnit unit){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("插入工程大事记数据失败");
        try {

            Boolean insert = memService.insertByInfo(unit);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("插入工程大事记数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("插入工程大事记数据Controller层发生异常",e);
        }
        return meta;
    }



    @DeleteMapping("/fourTotal/Mem/deleteByInfo")
    public Meta deleteByInfo(@RequestBody MemUnit unit){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("删除工程大事记数据失败");
        try {
            Boolean delete = memService.deleteByInfo(unit);
            if (delete != null && delete){
                meta.setStatus(200);
                meta.setMsg("删除工程大事记数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("删除工程大事记数据成功Controller层发生异常",e);
        }
        return meta;
    }


    @PutMapping("/fourTotal/Mem/updateByInfo")
    public Meta updateByInfo(@RequestBody MemUnit unit){

        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("更新工程大事记数据失败");
        try {

            Boolean insert = memService.updateByInfo(unit);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("更新工程大事记数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("工程大事记更新数据Controller层发生异常",e);
        }
        return meta;
    }



    @GetMapping("/fourTotal/Mem/selectAll")
    public MemResponse selectAll(){
        MemResponse response = new MemResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<MemUnit> memUnits = memService.selectAll();
            if (memUnits != null){
                meta.setStatus(200);
                meta.setMsg("获取工程大事记分页数据成功");
                response.setMemUnits(memUnits);

                return response;
            }
        }catch (Exception e){
            logger.error("获取工程大事记数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取工程大事记数据失败");
        return response;

    }




}
