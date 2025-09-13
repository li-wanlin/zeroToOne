package com.stg.vo.StaticTextVo;

import com.stg.entity.Safety;
import com.stg.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SafetyResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    Meta meta;

    List<Safety> safetyList;

}
