package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.RainFallData;
import com.stg.vo.fourPreventVo.RainRangeUnit;
import com.stg.vo.onenetVo.RainHisUnit;
import com.stg.vo.onenetVo.RainReportUnit;
import com.stg.vo.onenetVo.RainUnit;

import java.util.Date;
import java.util.List;

public interface RainFallDataService extends IService<RainFallData> {


    List<RainUnit> calRain(List<RainFallData> rainFallDatas, Date dayStart, Date dayEnd);


    void rainFallCall();

    List<RainRangeUnit> dayAgo();


    List<RainHisUnit> dataByGivenHour(Integer agoHourNum);


    List<RainReportUnit> selectRainReport(String dateStr);




}
