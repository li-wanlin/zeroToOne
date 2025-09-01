package com.jnl.vo.StaticTextVo;

import com.jnl.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    Meta meta;

    String display;

    List<String> assessments;

    List<String> safetyMan;

}
