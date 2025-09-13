package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.GNSSData;
import com.stg.vo.southVo.DataLatestUnit;
import com.stg.vo.southVo.GNSSReportUnit;
import com.stg.vo.southVo.MonitorUnit;

import java.util.List;

public interface GNSSDataService extends IService<GNSSData> {

    Boolean LocalSaveOrUpdata(DataLatestUnit Latest, String platformId, Integer moduleId, String deviceName);


    List<MonitorUnit> selectLatestGNSS();


    List<MonitorUnit> selectFortyEight();


    List<MonitorUnit> dataByGivenHourGNSS(Integer agoHourNum);



    List<GNSSReportUnit> selectGNSSReport(String dateStr);

}
