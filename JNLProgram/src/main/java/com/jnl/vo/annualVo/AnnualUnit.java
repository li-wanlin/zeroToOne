package com.jnl.vo.annualVo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnualUnit implements Serializable {

    private static final long serialVersionUID = 1L;


    Integer id;


    Integer orderNum;


    /**
     * 计划时间
     */
    String planTime;


    /**
     * 计划内容
     */
    String planContent;


    /**
     * 计划经费
     */
    Double planAmount;


    /**
     * 批复时间
     */
    String appTime;


    /**
     * 批复单位
     */
    String appUnit;


    /**
     * 批复金额
     */
    Double appAmount;


    /**
     * 完成情况
     */
    String development;

    /**
     * 完成金额
     */
    Double depAmount;

    /**
     * 遗留问题
     */
    String remainPro;

    /**
     * 备注
     */
    String proNotes;


}
