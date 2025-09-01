package com.jnl.vo.protectVo;

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
public class ProtectUnit implements Serializable {

    private static final long serialVersionUID = 1L;



    Integer id;


    Integer orderNum;


    /**
     * 类型
     */
    String protectType;


    /**
     * 名称
     */
    String protectName;


    /**
     * 计划开展时间
     */
    String planTime;


    /**
     * 实际开展时间
     */
    String actTime;


    /**
     * 计划投资金额
     */
    Double planAmount;


    /**
     * 实际投资金额
     */
    Double actAmount;


    /**
     * 遗留问题
     */
    String remainPro;


    /**
     * 开展情况
     */
    String development;


    /**
     * 遗留问题整改情况
     */
    String reformState;


    /**
     * 整改时间
     */
    String reformTime;


    /**
     * 责任人及联系方式
     */
    String leaderInfo;


    /**
     * 备注
     */
    String proNotes;




}
