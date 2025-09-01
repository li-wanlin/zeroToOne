package com.jnl.vo.StaticTextVo;

import com.jnl.entity.Safety;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SafetyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    List<Safety> safetyList;

}
