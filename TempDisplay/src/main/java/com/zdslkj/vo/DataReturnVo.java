package com.zdslkj.vo;

import com.zdslkj.entity.DataStreams;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataReturnVo {
    private String errno;
    private String error;
    private List<DataStreamsVo> data;

}
