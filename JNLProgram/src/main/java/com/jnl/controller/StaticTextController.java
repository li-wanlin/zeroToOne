package com.jnl.controller;

import com.jnl.entity.DisplayText;
import com.jnl.entity.Safety;
import com.jnl.service.impl.DisplayTextServiceImpl;
import com.jnl.service.impl.SafetyServiceImpl;
import com.jnl.vo.StaticTextVo.DisplayRequest;
import com.jnl.vo.StaticTextVo.DisplayResponse;
import com.jnl.vo.StaticTextVo.SafetyRequest;
import com.jnl.vo.StaticTextVo.SafetyResponse;
import com.jnl.vo.functionVo.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class StaticTextController {


    @Resource
    SafetyServiceImpl safetyService;

    @Resource
    DisplayTextServiceImpl displayTextService;

    private static final Logger logger = LoggerFactory.getLogger(StaticTextController.class);


    @GetMapping("/safety/selectAllSort")
    public SafetyResponse selectAllSort(){
        SafetyResponse safetyResponse = new SafetyResponse();
        Meta meta = new Meta();
        safetyResponse.setMeta(meta);
        try {
            List<Safety> safetyList = safetyService.selectAllSort();
            if (safetyList == null || safetyList.size() == 0){
                meta.setStatus(400);
                meta.setMsg("获取失败");
                return safetyResponse;
            }

            meta.setStatus(200);
            meta.setMsg("获取成功");
            safetyResponse.setSafetyList(safetyList);
            return safetyResponse;

        }catch (Exception e){
            logger.error("在获取安全责任人controller层发生异常",e);
            meta.setStatus(400);
            meta.setMsg("获取失败");
        }

        return safetyResponse;
    }


    @PostMapping("/safety/updateByList")
    public Meta updateByList(@RequestBody SafetyRequest safetyRequest){
        Meta meta = new Meta();
        try{
            List<Safety> safeties = safetyRequest.getSafetyList();
            Boolean updateByList = safetyService.updateByList(safeties);
            if (updateByList){
                meta.setStatus(200);
                meta.setMsg("更新成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("在更新安全责任人controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("更新失败");
        return meta;
    }


    /**
     * 水库概要显示
     * @return
     */
    @GetMapping("/display/selectDis")
    public DisplayResponse selectDis(){
        DisplayResponse displayResponse = new DisplayResponse();
        Meta meta = new Meta();
        displayResponse.setMeta(meta);

        try {
            DisplayText displayText = displayTextService.selectDis();
            if (displayText == null || displayText.getId() == null){
                meta.setStatus(400);
                meta.setMsg("获取失败");
                return displayResponse;
            }
            meta.setStatus(200);
            meta.setMsg("获取成功");
            displayResponse.setDisplay(displayText.getDisplay());
            return displayResponse;

        }catch (Exception e){
            logger.error("在获取显示文字controller层发生异常",e);
            meta.setStatus(400);
            meta.setMsg("获取失败");
        }
        return displayResponse;
    }

    /**
     * 水库概要修改
     * @param displayRequest
     * @return
     */
    @PostMapping("/display/updateByDis")
    public Meta updateByDis(@RequestBody DisplayRequest displayRequest){
        Meta meta = new Meta();
        try{
            String display = displayRequest.getDisplay();
            Boolean updataByDis = displayTextService.updataByDis(display);
            if (updataByDis){
                meta.setStatus(200);
                meta.setMsg("更新成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("在更新显示文字controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("更新失败");
        return meta;
    }


    /**
     * 安全鉴定显示
     * @return
     */
    @GetMapping("/fourManagement/selectAssessment")
    public DisplayResponse selectAssessment(){
        DisplayResponse displayResponse = new DisplayResponse();
        Meta meta = new Meta();
        displayResponse.setMeta(meta);
        try {
            List<String> assessments = displayTextService.selectAssessment();
            if (assessments != null){
                meta.setStatus(200);
                meta.setMsg("获取安全鉴定数据成功");

                displayResponse.setAssessments(assessments);
                return displayResponse;
            }
        }catch (Exception e){
            logger.error("在获取安全鉴定Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("获取安全鉴定数据失败");
        return displayResponse;
    }


    /**
     * 安全鉴定修改
     * @param displayRequest
     * @return
     */
    @PostMapping("/fourManagement/updateByAs")
    public Meta updateByAs(@RequestBody DisplayRequest displayRequest){
        Meta meta = new Meta();
        try{
            List<String> assessments = displayRequest.getAssessments();
            Boolean updateByAs = displayTextService.updateByAs(assessments);
            if (updateByAs){
                meta.setStatus(200);
                meta.setMsg("修改安全鉴定数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("在修改安全鉴定Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("修改安全鉴定数据失败");
        return meta;
    }



    /**
     * 安全管理显示
     * @return
     */
    @GetMapping("/fourManagement/selectSafetyMan")
    public DisplayResponse selectSafetyMan(){
        DisplayResponse displayResponse = new DisplayResponse();
        Meta meta = new Meta();
        displayResponse.setMeta(meta);
        try {
            List<String> safetyMan = displayTextService.selectSafetyMan();
            if (safetyMan != null){
                meta.setStatus(200);
                meta.setMsg("获取安全管理数据成功");

                displayResponse.setSafetyMan(safetyMan);
                return displayResponse;
            }
        }catch (Exception e){
            logger.error("在获取安全管理Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("获取安全管理数据失败");
        return displayResponse;
    }


    /**
     * 安全管理修改
     * @param displayRequest
     * @return
     */
    @PostMapping("/fourManagement/updateBySafetyMan")
    public Meta updateBySafetyMan(@RequestBody DisplayRequest displayRequest){
        Meta meta = new Meta();
        try{
            List<String> safetyMans = displayRequest.getSafetyMan();
            Boolean updateByAs = displayTextService.updateBySafeMans(safetyMans);
            if (updateByAs){
                meta.setStatus(200);
                meta.setMsg("修改安全管理数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("在修改安全管理Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("修改安全管理数据失败");
        return meta;
    }

}
