package com.jnl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("Reinforcement")
@AllArgsConstructor
@NoArgsConstructor
public class Reinforcement implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id",type = IdType.AUTO)
    Integer id;


    @TableField(value = "start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date startTime;


    @TableField(value = "pro_name")
    String proName;

    @TableField(value = "pro_content")
    String proContent;


    @TableField(value = "design_unit")
    String designUnit;

    @TableField(value = "app_unit")
    String appUnit;

    @TableField(value = "act_unit")
    String actUnit;

    @TableField(value = "man_unit")
    String manUnit;


    @TableField(value = "plan_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date planTime;


    @TableField(value = "finish_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date finishTime;

    @TableField(value = "pro_notes")
    String proNotes;

    @TableField(value = "file_name")
    String fileName;

    @TableField(value = "file_path")
    String filePath;

    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;

    @TableField(value = "reserves1")
    String reserves1;


}
