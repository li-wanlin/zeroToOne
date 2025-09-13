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
@TableName("PDFInfo")
@NoArgsConstructor
@AllArgsConstructor
public class PDFInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    Integer id;

    @TableField(value = "file_name")
    String fileName;

    @TableField(value = "file_path")
    String filePath;

    @TableField(value = "file_size")
    Long fileSize;

    @TableField(value = "checksum")
    String checksum;


    @TableField(value = "time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date time;

    @TableField(value = "belong_to")
    String belongTo;

    @TableField(value = "catalog_number")
    Integer catalogNumber;

    @TableField(value = "reserves1")
    String reserves1;

    @TableField(value = "reserves2")
    String reserves2;

    @TableField(value = "reserves3")
    String reserves3;

    @TableField(value = "reserves4")
    String reserves4;




}
