package com.jnl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnl.entity.PDFInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PDFInfoMapper extends BaseMapper<PDFInfo> {

    @Select("select info.file_name,info.belong_to from\n" +
            "(select file_name,belong_to,ROW_NUMBER() OVER (PARTITION BY belong_to ORDER BY time DESC) as rn from PDFInfo ) as info\n" +
            "where info.rn <= 5")
    List<PDFInfo> selectNameAndBelongTo();


}
