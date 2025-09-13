package com.stg.entity;

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
@TableName("LawPatrol")
@AllArgsConstructor
@NoArgsConstructor
public class LawPatrol implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    Integer id;


    @TableField(value = "patrol_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date patrolTime;


    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;


    @TableField(value = "patrol_type")
    String patrolType;


    @TableField(value = "patrol_area")
    String patrolArea;


    @TableField(value = "patrol_state")
    String patrolState;


    @TableField(value = "patrol_problem")
    String patrolProblem;


    @TableField(value = "solutions")
    String solutions;


    @TableField(value = "patrol_leader")
    String patrolLeader;


    @TableField(value = "patrol_person")
    String patrolPerson;


    @TableField(value = "if_complete")
    String ifComplete;


    @TableField(value = "complete_state")
    String completeState;


    @TableField(value = "patrol_notes")
    String patrolNotes;


    @TableField(value = "reserves1")
    String reserves1;


}
