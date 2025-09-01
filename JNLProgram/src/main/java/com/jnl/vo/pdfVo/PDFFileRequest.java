package com.jnl.vo.pdfVo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDFFileRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("filename")
    String filename;

}
