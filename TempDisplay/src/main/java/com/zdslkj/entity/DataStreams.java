package com.zdslkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("DataStreams")
@NoArgsConstructor
@AllArgsConstructor
public class DataStreams implements Serializable {

    private static final long serialVersionUID = 1L;

    //自增主键
    @TableId(value = "data_stream_id",type = IdType.AUTO)
    Integer dataStreamId;


    //数据流ID（为了和传过来的数据匹配）
    @TableField(value = "id")
    String id;


    @TableField(value ="update_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateAt;


    @TableField(value ="create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date createTime;

    @TableField(value ="current_value")
    String currentValue;
}
