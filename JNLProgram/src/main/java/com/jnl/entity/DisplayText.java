package com.jnl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@TableName("DisplayText")
@AllArgsConstructor
@NoArgsConstructor
public class DisplayText implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    Integer id;

    @TableField(value = "location")
    String location;

    @TableField(value = "display")
    String display;

    @TableField(value = "reserves1")
    String reserves1;

}
