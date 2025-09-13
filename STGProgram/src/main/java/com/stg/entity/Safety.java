package com.stg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@TableName("Safety")
@AllArgsConstructor
@NoArgsConstructor
public class Safety implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    Integer id;


    @TableField(value = "safety_type")
    String safetyType;

    @TableField(value = "position")
    String position;

    @TableField(value = "person_name")
    String personName;


    @TableField(value = "person_tel")
    String personTel;

    @TableField(value = "person_unit")
    String personUnit;


    @TableField(value = "reserves1")
    String reserves1;


}
