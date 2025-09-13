package com.stg.vo.fileVo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralInfo implements Serializable {


    private static final long serialVersionUID = 1L;


    Boolean ifCom;

    String inputTime;


    String fileName;


    String filePath;

}
