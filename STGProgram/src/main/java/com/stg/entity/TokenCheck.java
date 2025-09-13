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
@TableName("tokenCheck")
@NoArgsConstructor
@AllArgsConstructor
public class TokenCheck implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id",type = IdType.AUTO)
    Integer id;

    @TableField(value = "username")
    String username;

    @TableField(value = "login_time")
    Date loginTime;


    @TableField(value = "logout_time")
    Date logoutTime;


    @TableField(value = "new_access_token")
    String newAccessToken;

    @TableField(value = "old_access_token")
    String oldAccessToken;

    @TableField(value = "new_refresh_token")
    String newRefreshToken;

    @TableField(value = "old_refresh_token")
    String oldRefreshToken;

    @TableField(value = "delayed_access_token")
    Integer delayedAccessToken;

    @TableField(value = "delayed_refresh_token")
    Long delayedRefreshToken;

    @TableField(value = "reserves1")
    String reserves1;

    @TableField(value = "reserves2")
    String reserves2;

    @TableField(value = "reserves3")
    String reserves3;
}
