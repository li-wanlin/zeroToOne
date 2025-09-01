package com.jnl.controller;


import com.jnl.service.impl.DamSignServiceImpl;
import com.jnl.service.impl.FourTotalServiceImpl;
import com.jnl.vo.damSignVo.SignResponse;
import com.jnl.vo.damSignVo.SignUnit;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.matlabVo.MatlabWaterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class MatlabController {



    @Resource
    DamSignServiceImpl damSignService;


    @Resource
    FourTotalServiceImpl fourTotalService;








    private static final Logger logger = LoggerFactory.getLogger(MatlabController.class);



    @PostMapping("/fourPrevent/Matlab/insertSign")
    public Meta insertByInfo(@RequestBody SignUnit unit){
        Meta meta = new Meta();
        try {

            Boolean insert = damSignService.insertByInfo(unit);
            if (insert){
                meta.setStatus(200);
                meta.setMsg("新增大坝标志位成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("新增大坝标志位Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("新增大坝标志位失败");
        return meta;
    }




    @DeleteMapping("/fourPrevent/Matlab/deleteSign")
    public Meta deleteByInfo(@RequestBody SignUnit unit){
        Meta meta = new Meta();
        try {

            Boolean delete = damSignService.deleteByInfo(unit);
            if (delete){
                meta.setStatus(200);
                meta.setMsg("删除大坝标志位成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("删除大坝标志位Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("删除大坝标志位失败");
        return meta;
    }


    @PutMapping("/fourPrevent/Matlab/updateSign")
    public Meta updateByInfo(@RequestBody SignUnit unit){
        Meta meta = new Meta();
        try {

            Boolean update = damSignService.updateByInfo(unit);
            if (update){
                meta.setStatus(200);
                meta.setMsg("更新大坝标志位成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("更新大坝标志位Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("更新大坝标志位失败");
        return meta;
    }


    @GetMapping("/fourPrevent/Matlab/selectSign")
    public SignResponse selectSign(){
        SignResponse response = new SignResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {

            SignUnit unit = damSignService.selectEnable();
            if (unit != null){
                response.setUnit(unit);
                meta.setStatus(200);
                meta.setMsg("获取大坝标志位成功");
                return response;
            }

        }catch (Exception e){
            logger.error("获取大坝标志位Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取大坝标志失败，请尽快修改");
        return response;
    }


    @GetMapping("/fourTotal/Matlab/selectMatlabWater")
    public MatlabWaterResponse selectMatlabWater(){
        return fourTotalService.selectMatlabWater();
    }

}
