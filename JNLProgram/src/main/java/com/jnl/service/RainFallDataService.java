package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.RainFallData;
import com.jnl.vo.fourPreventVo.RainRangeUnit;
import com.jnl.vo.onenetVo.RainHisUnit;
import com.jnl.vo.onenetVo.RainReportUnit;
import com.jnl.vo.onenetVo.RainUnit;

import java.util.Date;
import java.util.List;

public interface RainFallDataService extends IService<RainFallData> {


    List<RainUnit> calRain(List<RainFallData> rainFallDatas, Date dayStart, Date dayEnd);


    void rainFallCall();

    List<RainRangeUnit> dayAgo();


    List<RainHisUnit> dataByGivenHour(Integer agoHourNum);


    List<RainReportUnit> selectRainReport(String dateStr);




}
