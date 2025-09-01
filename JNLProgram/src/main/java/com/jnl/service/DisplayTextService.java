package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.DisplayText;

import java.util.List;

public interface DisplayTextService extends IService<DisplayText> {


    /**
     * 水库概要显示
     * @return
     */
    DisplayText selectDis();

    /**
     * 水库概要修改
     * @return
     */
    Boolean updataByDis(String display);


    /**
     * 安全鉴定显示
     * @return
     */
    List<String> selectAssessment();

    /**
     * 安全鉴定修改
     * @param assessments
     * @return
     */
    Boolean updateByAs(List<String> assessments);


    /**
     * 安全管理显示
     * @return
     */
    List<String> selectSafetyMan();


    /**
     * 安全管理修改
     * @param safetyMans
     * @return
     */
    Boolean updateBySafeMans(List<String> safetyMans);


}
