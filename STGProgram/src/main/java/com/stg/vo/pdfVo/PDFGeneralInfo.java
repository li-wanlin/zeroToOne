package com.stg.vo.pdfVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PDFGeneralInfo implements Serializable {

    private static final long serialVersionUID = 1L;


    Boolean ifCom;


    String fileName;


    String filePath;



}
