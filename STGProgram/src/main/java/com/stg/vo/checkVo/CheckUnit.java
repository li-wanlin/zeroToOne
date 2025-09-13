package com.stg.vo.checkVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    Integer id;

    Integer orderNum;

    /**
     * 时间
     */
    String ckTime;

    /**
     * 工程名称
     */
    String ckName;

    /**
     * 排查内容
     */
    String ckContent;

    /**
     * 排查人
     */
    String ckPerson;

    /**
     * 发现问题
     */
    String ckProb;

    /**
     * 处理措施
     */
    String handleMea;

    /**
     * 处理结果
     */
    String handleRes;

    /**
     * 备注
     */
    String proNotes;
}
