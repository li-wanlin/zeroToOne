package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.GNSSData;
import com.jnl.vo.southVo.DataLatestUnit;
import com.jnl.vo.southVo.GNSSReportUnit;
import com.jnl.vo.southVo.MonitorUnit;

import java.util.List;

public interface GNSSDataService extends IService<GNSSData> {

    Boolean LocalSaveOrUpdata(DataLatestUnit Latest, String platformId, Integer moduleId, String deviceName);


    List<MonitorUnit> selectLatestGNSS();


    List<MonitorUnit> selectFortyEight();


    List<MonitorUnit> dataByGivenHourGNSS(Integer agoHourNum);


    List<GNSSReportUnit> selectGNSSReport(String dateStr);

}
