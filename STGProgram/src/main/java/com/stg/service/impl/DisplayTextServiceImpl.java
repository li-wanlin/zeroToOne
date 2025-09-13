package com.stg.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.DisplayText;
import com.stg.mapper.DisplayTextMapper;
import com.stg.service.DisplayTextService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisplayTextServiceImpl extends ServiceImpl<DisplayTextMapper, DisplayText> implements DisplayTextService {


    private static final Logger logger = LoggerFactory.getLogger(DisplayTextServiceImpl.class);


    /**
     * 水库概要显示
     * @return
     */
    @Override
    public DisplayText selectDis() {
        try {
            DisplayText byId = getById(1);
            if(byId == null || byId.getId() == null){
                return null;
            }
            return byId;

        }catch (Exception e){
            logger.error("在获取水库概要时发生异常",e);
        }
        return null;
    }


    /**
     * 水库概要修改
     * @param display
     * @return
     */
    @Override
    public Boolean updataByDis(String display) {
        try {
            if (StringUtils.isEmpty(display)){
                return false;
            }
            DisplayText displayText = new DisplayText();
            displayText.setId(1);
            displayText.setLocation("水库概要");
            displayText.setDisplay(display);

            return updateById(displayText);
        }catch (Exception e){
            logger.error("在修改水库概要时发生异常",e);
        }
        return false;
    }


    /**
     * 安全鉴定显示
     *
     * @return
     */
    @Override
    public List<String> selectAssessment() {
        try {
            DisplayText byId = getById(2);
            if(byId == null || byId.getId() == null){
                return null;
            }

            List<String> assessments = JSON.parseArray(byId.getDisplay(),String.class);
            if (assessments != null && assessments.size() == 3){
                return assessments;
            }
        }catch (Exception e){
            logger.error("在获取安全鉴定时发生异常",e);
        }
        return null;
    }


    /**
     * 安全鉴定修改
     * @param assessments
     * @return
     */
    @Override
    public Boolean updateByAs(List<String> assessments) {
        try {
            if (assessments == null || assessments.size() != 3){
                return false;
            }

            String display = JSON.toJSONString(assessments);

            DisplayText displayText = new DisplayText();
            displayText.setId(2);
            displayText.setLocation("安全鉴定");
            displayText.setDisplay(display);
            displayText.setReserves1("鉴定时间,鉴定结果,下次鉴定");

            return updateById(displayText);
        }catch (Exception e){
            logger.error("在修改安全鉴定时发生异常",e);
        }
        return false;
    }

    /**
     * 安全管理显示
     * @return
     */
    @Override
    public List<String> selectSafetyMan() {
        try {
            DisplayText byId = getById(3);
            if(byId == null || byId.getId() == null){
                return null;
            }

            List<String> safetyMan = JSON.parseArray(byId.getDisplay(),String.class);
            if (safetyMan != null && safetyMan.size() == 4){
                return safetyMan;
            }
        }catch (Exception e){
            logger.error("在获取安全鉴定时发生异常",e);
        }
        return null;
    }


    /**
     * 安全管理修改
     * @param safetyMans
     * @return
     */
    @Override
    public Boolean updateBySafeMans(List<String> safetyMans) {
        try {
            if (safetyMans == null || safetyMans.size() != 4){
                return false;
            }

            String display = JSON.toJSONString(safetyMans);

            DisplayText displayText = new DisplayText();
            displayText.setId(3);
            displayText.setLocation("安全管理");
            displayText.setDisplay(display);
            displayText.setReserves1("确权划界,水库岁修,除险加固,白蚁防治");

            return updateById(displayText);
        }catch (Exception e){
            logger.error("在修改安全管理时发生异常",e);
        }
        return false;
    }


    /**
     * 水量信息显示
     * @return
     */
    @Override
    public List<Double> selectAmountInfo() {
        try {
            DisplayText byId = getById(4);
            if(byId == null || byId.getId() == null){
                return null;
            }

            List<Double> amountInfo = JSON.parseArray(byId.getDisplay(),Double.class);
            if (amountInfo != null && amountInfo.size() == 3){
                return amountInfo;
            }
        }catch (Exception e){
            logger.error("在获取水量信息时发生异常",e);
        }
        return null;
    }



    /**
     * 水量信息修改
     * @return
     */
    @Override
    public Boolean updateByAi(List<Double> amountInfos) {
        try {
            if (amountInfos == null || amountInfos.size() != 3){
                return false;
            }

            String display = JSON.toJSONString(amountInfos);

            DisplayText displayText = new DisplayText();
            displayText.setId(4);
            displayText.setLocation("水量信息");
            displayText.setDisplay(display);

            return updateById(displayText);
        }catch (Exception e){
            logger.error("在修改水量信息时发生异常",e);
        }
        return false;
    }


}
