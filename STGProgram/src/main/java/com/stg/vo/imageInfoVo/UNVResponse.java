package com.stg.vo.imageInfoVo;

import com.stg.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UNVResponse implements Serializable {

    Meta meta;

    String inputTime;

    List<String> fileNames;

    List<String> dateStrs;


}
