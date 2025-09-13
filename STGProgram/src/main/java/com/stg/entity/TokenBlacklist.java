package com.stg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tokenBlacklist")
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlacklist implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    Integer id;

    @TableField(value = "username")
    String username;

    @TableField(value = "access_token")
    String accessToken;

    @TableField(value = "time")
    Date time;

    @TableField(value = "reserves1")
    String reserves1;

    @TableField(value = "reserves2")
    String reserves2;

    @TableField(value = "reserves3")
    String reserves3;

    @TableField(value = "reserves4")
    String reserves4;


}
