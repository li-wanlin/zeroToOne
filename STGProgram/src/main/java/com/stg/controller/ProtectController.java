package com.stg.controller;

import com.stg.service.impl.ProtectServiceImpl;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.protectVo.ProtectResponse;
import com.stg.vo.protectVo.ProtectUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class ProtectController {

    @Resource
    ProtectServiceImpl protectService;


    private static final Logger logger = LoggerFactory.getLogger(ProtectController.class);



    @GetMapping("/fourManagement/Protect/displayInfo")
    public ProtectResponse displayInfo(){
        ProtectResponse response = new ProtectResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {

            ProtectResponse protectResponse = protectService.displayInfo();
            if (protectResponse != null){
                return protectResponse;
            }


        }catch (Exception e){
            logger.error("获取保护管理当年数据发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取保护管理当年数据发生失败");
        return response;
    }


    @PostMapping ("/fourManagement/Protect/insertByInfo")
    public Meta insertByInfo(@RequestBody ProtectUnit unit){
        Meta meta = new Meta();
        try {
            Boolean insert = protectService.insertByInfo(unit);
            if (insert){
                meta.setStatus(200);
                meta.setMsg("插入保护管理数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("插入保护管理数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("插入保护管理数据失败");
        return meta;
    }


    @DeleteMapping("/fourManagement/Protect/deleteByInfo")
    public Meta deleteByInfo(@RequestBody ProtectUnit unit){
        Meta meta = new Meta();
        try {
            Boolean delete = protectService.deleteByInfo(unit);
            if (delete){
                meta.setStatus(200);
                meta.setMsg("删除保护管理数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("删除保护管理数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("删除保护管理数据失败");
        return meta;
    }


    @PutMapping("/fourManagement/Protect/updateByInfo")
    public Meta updateByInfo(@RequestBody ProtectUnit unit){
        Meta meta = new Meta();
        try {
            Boolean update = protectService.updateByInfo(unit);
            if (update){
                meta.setStatus(200);
                meta.setMsg("更新保护管理数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("更新保护管理数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("更新保护管理数据失败");
        return meta;
    }


    @GetMapping("/fourManagement/Protect/pagedQuery")
    public ProtectResponse pagedQuery(@RequestParam(value = "pageNum") Integer pageNum,
                                      @RequestParam(value = "pageSize") Integer pageSize){
        ProtectResponse response = new ProtectResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            ProtectResponse protectResponse = protectService.pagedQuery(pageNum, pageSize);
            if (protectResponse != null){
                return protectResponse;
            }
        }catch (Exception e){
            logger.error("获取保护管理分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取保护管理分页数据失败");
        return response;

    }



    @GetMapping("/fourManagement/Protect/pagedQueryByYear")
    public ProtectResponse pagedQueryByYear(@RequestParam(value = "pageNum") Integer pageNum,
                                            @RequestParam(value = "pageSize") Integer pageSize,
                                            @RequestParam(value = "year") String year){
        ProtectResponse response = new ProtectResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            ProtectResponse protectResponse = protectService.pagedQueryByYear(pageNum, pageSize,year);
            if (protectResponse != null){
                return protectResponse;
            }
        }catch (Exception e){
            logger.error("获取保护管理分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取保护管理分页数据失败");
        return response;

    }


}
