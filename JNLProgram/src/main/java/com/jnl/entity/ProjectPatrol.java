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
@TableName("ProjectPatrol")
@AllArgsConstructor
@NoArgsConstructor
public class ProjectPatrol implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    Integer id;


    @TableField(value = "patrol_area")
    String patrolArea;


    @TableField(value = "plan_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date planTime;


    @TableField(value = "finish_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date finishTime;




    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;


    @TableField(value = "patrol_state")
    String patrolState;


    @TableField(value = "reform_state")
    String reformState;


    @TableField(value = "reform_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date reformTime;


    @TableField(value = "if_reform")
    String ifReform;



    @TableField(value = "patrol_person")
    String patrolPerson;


    @TableField(value = "leader_info")
    String leaderInfo;


    @TableField(value = "patrol_notes")
    String patrolNotes;



    @TableField(value = "reserves1")
    String reserves1;


}
