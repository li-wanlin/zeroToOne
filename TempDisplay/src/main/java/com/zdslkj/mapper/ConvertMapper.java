package com.zdslkj.mapper;


import com.zdslkj.entity.DataStreams;
import com.zdslkj.vo.DataStreamsVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ConvertMapper {

    ConvertMapper INSTANCE = Mappers.getMapper(ConvertMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "update_at", target = "updateAt", qualifiedByName = "stringToDate")
    @Mapping(source = "create_time", target = "createTime", qualifiedByName = "stringToDate")
    @Mapping(source = "current_value", target = "currentValue")
    DataStreams map(DataStreamsVo dataStreamsVo);

    List<DataStreams> mapList(List<DataStreamsVo> dataStreamsVoList);

    @Named("stringToDate")
    default Date stringToDate(String dateStr) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(dateStr);
    }

    //List<TargetObject> targetList = ConvertMapper.INSTANCE.mapList(sourceList);
}
