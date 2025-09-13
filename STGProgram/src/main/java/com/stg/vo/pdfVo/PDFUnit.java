package com.stg.vo.pdfVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PDFUnit {

    String belongTo;

    List<FileInfo> fileInfos;

}
