package com.stg.vo.fileVo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUnit implements Serializable {


    private static final long serialVersionUID = 1L;

    Integer id;


    String fileName;



}
