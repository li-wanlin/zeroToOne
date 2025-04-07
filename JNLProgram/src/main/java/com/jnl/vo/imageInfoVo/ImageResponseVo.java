package com.jnl.vo.imageInfoVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    byte[] imageBytes;


}
