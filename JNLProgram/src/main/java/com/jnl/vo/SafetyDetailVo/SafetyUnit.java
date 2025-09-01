package com.jnl.vo.SafetyDetailVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SafetyUnit {

    Integer id;

    /**
     * 序号
     */
    Integer orderNum;

    /**
     * 鉴定项目
     */
    String project;

    /**
     * 鉴定时间
     */
    String time;

    /**
     * 鉴定组织单位
     */
    String orgUnit;

    /**
     * 鉴定承担单位
     */
    String bearUnit;

    /**
     * 鉴定结论
     */
    String conclusion;




}
