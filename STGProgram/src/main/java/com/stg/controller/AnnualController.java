package com.stg.controller;

import com.stg.service.impl.AnnualServiceImpl;
import com.stg.vo.annualVo.AnnualResponse;
import com.stg.vo.annualVo.AnnualUnit;
import com.stg.vo.functionVo.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class AnnualController {


    @Resource
    AnnualServiceImpl annualService;


    private static final Logger logger = LoggerFactory.getLogger(AnnualController.class);



    @PostMapping("/fourManagement/Annual/insertByInfo")
    public Meta insertByInfo(@RequestBody AnnualUnit unit){
        Meta meta = new Meta();
        try {
            Boolean insert = annualService.insertByInfo(unit);
            if (insert){
                meta.setStatus(200);
                meta.setMsg("插入水库岁修数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("插入水库岁修数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("插入水库岁修数据失败");
        return meta;
    }



    @DeleteMapping("/fourManagement/Annual/deleteByInfo")
    public Meta deleteByInfo(@RequestBody AnnualUnit unit){
        Meta meta = new Meta();
        try {
            Boolean delete = annualService.deleteByInfo(unit);
            if (delete){
                meta.setStatus(200);
                meta.setMsg("删除水库岁修数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("删除水库岁修数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("删除水库岁修数据失败");
        return meta;
    }


    @PutMapping("/fourManagement/Annual/updateByInfo")
    public Meta updateByInfo(@RequestBody AnnualUnit unit){
        Meta meta = new Meta();
        try {
            Boolean update = annualService.updateByInfo(unit);
            if (update){
                meta.setStatus(200);
                meta.setMsg("更新水库岁修数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("更新水库岁修数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("更新水库岁修数据失败");
        return meta;
    }


    @GetMapping("/fourManagement/Annual/pagedQuery")
    public AnnualResponse pagedQueryByYear(@RequestParam(value = "pageNum") Integer pageNum,
                                           @RequestParam(value = "pageSize") Integer pageSize,
                                           @RequestParam(value = "year",required = false) String year){
        AnnualResponse response = new AnnualResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            AnnualResponse annualResponse = annualService.pagedQuery(pageNum, pageSize,year);
            if (annualResponse != null){
                return annualResponse;
            }
        }catch (Exception e){
            logger.error("获取水库岁修分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取水库岁修分页数据失败");
        return response;

    }



}
