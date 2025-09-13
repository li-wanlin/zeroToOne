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
@TableName("userInfo")
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    Integer id;

    @TableField(value ="rid")
    Integer rid;

    @TableField(value ="username")
    String username;

    @TableField(value ="password")
    String password;

    @TableField(value = "lv")
    Integer lv;

    @TableField(value ="mobile")
    String mobile;

    @TableField(value ="email")
    String email;

    @TableField(value ="token")
    String token;

    @TableField(value ="reserves1")
    String reserves1;

    @TableField(value ="reserves2")
    String reserves2;

    @TableField(value ="reserves3")
    String reserves3;

}
