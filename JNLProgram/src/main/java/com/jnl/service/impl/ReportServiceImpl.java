package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnl.entity.*;
import com.jnl.mapper.*;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.onenetVo.RainReportResponse;
import com.jnl.vo.onenetVo.RainReportUnit;
import com.jnl.vo.reportVo.*;
import com.jnl.vo.southVo.GNSSReportResponse;
import com.jnl.vo.southVo.GNSSReportUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl {


    @Resource
    RainFallDataMapper rainFallDataMapper;


    @Resource
    RainFallDataServiceImpl rainFallDataService;


    @Resource
    RainDayMapper rainDayMapper;

    @Resource
    RainDayServiceImpl rainDayService;



    @Resource
    GNSSDataMapper gnssDataMapper;

    @Resource
    GNSSDataServiceImpl gnssDataService;


    @Resource
    GNSSHourMapper gnssHourMapper;


    @Resource
    GNSSHourServiceImpl gnssHourService;


    @Resource
    GNSSDayMapper gnssDayMapper;

    @Resource
    GNSSDayServiceImpl gnssDayService;



    @Resource
    StageHourServiceImpl stageHourService;

    @Resource
    StageHourMapper stageHourMapper;


    @Resource
    StageDayServiceImpl stageDayService;

    @Resource
    StageDayMapper stageDayMapper;


    @Resource
    SeepageHourMapper seepageHourMapper;

    @Resource
    SeepageHourServiceImpl seepageHourService;

    @Resource
    SeepageDayMapper seepageDayMapper;

    @Resource
    SeepageDayServiceImpl seepageDayService;

    @Resource
    PowerHourMapper powerHourMapper;

    @Resource
    PowerHourServiceImpl powerHourService;

    @Resource
    PowerDayMapper powerDayMapper;

    @Resource
    PowerDayServiceImpl powerDayService;

    @Resource
    FlowHourMapper flowHourMapper;

    @Resource
    FlowHourServiceImpl flowHourService;

    @Resource
    FlowDayMapper flowDayMapper;

    @Resource
    FlowDayServiceImpl flowDayService;





    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);


    public RainhReportResponse selectRainhReport(String dateStart, String dateEnd, Integer pageNum, Integer pageSize){
        RainhReportResponse response = new RainhReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("获取雨量日报表分页数据失败");


        try {

            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseStrToDate(dateStart);
            Date end = DateLocalUtils.parseStrToDate(dateEnd);


            // 1. 查询实际数据（不分页，获取全量）
            QueryWrapper<RainFallData> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("input_time",start,end);
            queryWrapper.orderByAsc("input_time");


            List<RainFallData> allData = rainFallDataMapper.selectList(queryWrapper);



            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateHourlyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, RainFallData> dataMap = allData.stream()
                    .collect(Collectors.toMap(RainFallData::getInputTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<RainFallData> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                RainFallData untreated = dataMap.getOrDefault(time, new RainFallData());
                if (untreated.getInputTime() == null) {
                    untreated.setInputTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setRainfall1(null);
                    untreated.setRainfall2(null);
                    untreated.setRainfall3(null);
                    untreated.setRainfall4(null);
                    untreated.setRainfall5(null);
                }
                filledData.add(untreated);
            }

            // 5. 计算分页信息
            int totalCount = filledData.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 处理页码越界情况
            if (pageNum > totalPages && totalPages > 0) {
                pageNum = totalPages;
            }

            int startIndex = Math.max(0, (pageNum - 1) * pageSize);
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            // 6. 获取当前页数据
            List<RainFallData> pageData = filledData.subList(startIndex, endIndex);


            int orderNum = startIndex + 1; // 序号从1开始
            List<RainUnit> units = new ArrayList<>();
            for (RainFallData record:pageData) {
                RainUnit unit = new RainUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStr(record.getInputTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getInputTime())){
                    unit.setRainfall1(-999d);
                    unit.setRainfall2(-999d);
                    unit.setRainfall3(-999d);
                    unit.setRainfall4(-999d);
                    unit.setRainfall5(-999d);
                }else {
                    unit.setRainfall1(record.getRainfall1() != null ? Math.round((double)record.getRainfall1() * 100)/100.0 : null);
                    unit.setRainfall2(record.getRainfall2() != null ? Math.round((double)record.getRainfall2() * 100)/100.0 : null);
                    unit.setRainfall3(record.getRainfall3() != null ? Math.round((double)record.getRainfall3() * 100)/100.0 : null);
                    unit.setRainfall4(record.getRainfall4() != null ? Math.round((double)record.getRainfall4() * 100)/100.0 : null);
                    unit.setRainfall5(record.getRainfall5() != null ? Math.round((double)record.getRainfall5() * 100)/100.0 : null);
                }
                units.add(unit);
            }

            response.setTotalPage(totalPages);
            response.setTotalCount(totalCount);
            response.setCurrentPage(pageNum);
            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("获取雨量日报表分页数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取雨量日报表分页数据发生异常",e);
        }
        return response;
    }


    public RaindReportResponse selectRaindReport(String dateStart, String dateEnd, Integer pageNum, Integer pageSize){
        RaindReportResponse response = new RaindReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("获取雨量月报表分页数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseGiveStrToDate(dateStart);
            Date end = DateLocalUtils.parseGiveStrToDate(dateEnd);


            QueryWrapper<RainDay> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<RainDay> allData = rainDayMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateDailyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, RainDay> dataMap = allData.stream()
                    .collect(Collectors.toMap(RainDay::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<RainDay> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                RainDay untreated = dataMap.getOrDefault(time, new RainDay());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setRainfall1(null);
                    untreated.setRainfall2(null);
                    untreated.setRainfall3(null);
                    untreated.setRainfall4(null);
                    untreated.setRainfall5(null);
                }
                filledData.add(untreated);
            }

            // 5. 计算分页信息
            int totalCount = filledData.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 处理页码越界情况
            if (pageNum > totalPages && totalPages > 0) {
                pageNum = totalPages;
            }

            int startIndex = Math.max(0, (pageNum - 1) * pageSize);
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            // 6. 获取当前页数据
            List<RainDay> pageData = filledData.subList(startIndex, endIndex);


            int orderNum = startIndex + 1; // 序号从1开始
            List<RainUnit> units = new ArrayList<>();


            for (RainDay record:pageData) {
                RainUnit unit = new RainUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStrTwo(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setRainfall1(-999d);
                    unit.setRainfall2(-999d);
                    unit.setRainfall3(-999d);
                    unit.setRainfall4(-999d);
                    unit.setRainfall5(-999d);
                }else {
                    unit.setRainfall1(record.getRainfall1());
                    unit.setRainfall2(record.getRainfall2());
                    unit.setRainfall3(record.getRainfall3());
                    unit.setRainfall4(record.getRainfall4());
                    unit.setRainfall5(record.getRainfall5());
                }
                units.add(unit);
            }

            response.setTotalPage(totalPages);
            response.setTotalCount(totalCount);
            response.setCurrentPage(pageNum);
            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("获取雨量月报表分页数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取雨量月报表分页数据发生异常",e);
        }
        return response;
    }



    public RainhReportResponse exportRainhReport(String dateStart, String dateEnd){
        RainhReportResponse response = new RainhReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("导出雨量日报表数据失败");


        try {

            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseStrToDate(dateStart);
            Date end = DateLocalUtils.parseStrToDate(dateEnd);


            // 1. 查询实际数据（不分页，获取全量）
            QueryWrapper<RainFallData> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("input_time",start,end);
            queryWrapper.orderByAsc("input_time");


            List<RainFallData> allData = rainFallDataMapper.selectList(queryWrapper);



            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateHourlyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, RainFallData> dataMap = allData.stream()
                    .collect(Collectors.toMap(RainFallData::getInputTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<RainFallData> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                RainFallData untreated = dataMap.getOrDefault(time, new RainFallData());
                if (untreated.getInputTime() == null) {
                    untreated.setInputTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setRainfall1(null);
                    untreated.setRainfall2(null);
                    untreated.setRainfall3(null);
                    untreated.setRainfall4(null);
                    untreated.setRainfall5(null);
                }
                filledData.add(untreated);
            }



            int orderNum = 1; // 序号从1开始
            List<RainUnit> units = new ArrayList<>();
            for (RainFallData record:filledData) {
                RainUnit unit = new RainUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStr(record.getInputTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getInputTime())){
                    unit.setRainfall1(-999d);
                    unit.setRainfall2(-999d);
                    unit.setRainfall3(-999d);
                    unit.setRainfall4(-999d);
                    unit.setRainfall5(-999d);
                }else {
                    unit.setRainfall1(record.getRainfall1() != null ? Math.round((double)record.getRainfall1() * 100)/100.0 : null);
                    unit.setRainfall2(record.getRainfall2() != null ? Math.round((double)record.getRainfall2() * 100)/100.0 : null);
                    unit.setRainfall3(record.getRainfall3() != null ? Math.round((double)record.getRainfall3() * 100)/100.0 : null);
                    unit.setRainfall4(record.getRainfall4() != null ? Math.round((double)record.getRainfall4() * 100)/100.0 : null);
                    unit.setRainfall5(record.getRainfall5() != null ? Math.round((double)record.getRainfall5() * 100)/100.0 : null);
                }
                units.add(unit);
            }

            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("导出雨量日报表数据成功");
            return response;


        }catch (Exception e){
            logger.error("导出雨量日报表数据发生异常",e);
        }
        return response;
    }






    public GNSShReportResponse selectGNSShReport(String dateStart, String dateEnd, Integer pageNum, Integer pageSize){
        GNSShReportResponse response = new GNSShReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("获取GNSS日报表分页数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseStrToDate(dateStart);
            Date end = DateLocalUtils.parseStrToDate(dateEnd);

            QueryWrapper<GNSSHour> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<GNSSHour> allData = gnssHourMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateHourlyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, GNSSHour> dataMap = allData.stream()
                    .collect(Collectors.toMap(GNSSHour::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<GNSSHour> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                GNSSHour untreated = dataMap.getOrDefault(time, new GNSSHour());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setM1X(null);
                    untreated.setM1Y(null);
                    untreated.setM1H(null);
                    untreated.setM2X(null);
                    untreated.setM2Y(null);
                    untreated.setM2H(null);
                    untreated.setM3X(null);
                    untreated.setM3Y(null);
                    untreated.setM3H(null);
                }
                filledData.add(untreated);
            }

            // 5. 计算分页信息
            int totalCount = filledData.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 处理页码越界情况
            if (pageNum > totalPages && totalPages > 0) {
                pageNum = totalPages;
            }

            int startIndex = Math.max(0, (pageNum - 1) * pageSize);
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            // 6. 获取当前页数据
            List<GNSSHour> pageData = filledData.subList(startIndex, endIndex);


            int orderNum = startIndex + 1; // 序号从1开始
            List<GNSSUnit> units = new ArrayList<>();
            for (GNSSHour record:pageData) {
                GNSSUnit unit = new GNSSUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStr(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setM1X(-999d);
                    unit.setM1Y(-999d);
                    unit.setM1H(-999d);
                    unit.setM2X(-999d);
                    unit.setM2Y(-999d);
                    unit.setM2H(-999d);
                    unit.setM3X(-999d);
                    unit.setM3Y(-999d);
                    unit.setM3H(-999d);
                }else {
                    unit.setM1X(record.getM1X());
                    unit.setM1Y(record.getM1Y());
                    unit.setM1H(record.getM1H());
                    unit.setM2X(record.getM2X());
                    unit.setM2Y(record.getM2Y());
                    unit.setM2H(record.getM2H());
                    unit.setM3X(record.getM3X());
                    unit.setM3Y(record.getM3Y());
                    unit.setM3H(record.getM3H());
                }
                units.add(unit);
            }

            response.setTotalPage(totalPages);
            response.setTotalCount(totalCount);
            response.setCurrentPage(pageNum);
            response.setUnits(units);

            meta.setStatus(200);
            meta.setMsg("获取GNSS日报表分页数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取GNSS日报表分页数据发生异常",e);
        }
        return response;
    }





    public GNSSdReportResponse selectGNSSdReport(String dateStart, String dateEnd, Integer pageNum, Integer pageSize){
        GNSSdReportResponse response = new GNSSdReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("获取GNSS月报表分页数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseGiveStrToDate(dateStart);
            Date end = DateLocalUtils.parseGiveStrToDate(dateEnd);


            QueryWrapper<GNSSDay> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<GNSSDay> allData = gnssDayMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateDailyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, GNSSDay> dataMap = allData.stream()
                    .collect(Collectors.toMap(GNSSDay::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<GNSSDay> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                GNSSDay untreated = dataMap.getOrDefault(time, new GNSSDay());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setM1X(null);
                    untreated.setM1Y(null);
                    untreated.setM1H(null);
                    untreated.setM2X(null);
                    untreated.setM2Y(null);
                    untreated.setM2H(null);
                    untreated.setM3X(null);
                    untreated.setM3Y(null);
                    untreated.setM3H(null);
                }
                filledData.add(untreated);
            }

            // 5. 计算分页信息
            int totalCount = filledData.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 处理页码越界情况
            if (pageNum > totalPages && totalPages > 0) {
                pageNum = totalPages;
            }

            int startIndex = Math.max(0, (pageNum - 1) * pageSize);
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            // 6. 获取当前页数据
            List<GNSSDay> pageData = filledData.subList(startIndex, endIndex);


            int orderNum = startIndex + 1; // 序号从1开始
            List<GNSSUnit> units = new ArrayList<>();


            for (GNSSDay record:pageData) {
                GNSSUnit unit = new GNSSUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStrTwo(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setM1X(-999d);
                    unit.setM1Y(-999d);
                    unit.setM1H(-999d);
                    unit.setM2X(-999d);
                    unit.setM2Y(-999d);
                    unit.setM2H(-999d);
                    unit.setM3X(-999d);
                    unit.setM3Y(-999d);
                    unit.setM3H(-999d);
                }else {
                    unit.setM1X(record.getM1X());
                    unit.setM1Y(record.getM1Y());
                    unit.setM1H(record.getM1H());
                    unit.setM2X(record.getM2X());
                    unit.setM2Y(record.getM2Y());
                    unit.setM2H(record.getM2H());
                    unit.setM3X(record.getM3X());
                    unit.setM3Y(record.getM3Y());
                    unit.setM3H(record.getM3H());
                }
                units.add(unit);
            }

            response.setTotalPage(totalPages);
            response.setTotalCount(totalCount);
            response.setCurrentPage(pageNum);
            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("获取GNSS月报表分页数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取GNSS月报表分页数据发生异常",e);
        }
        return response;
    }



    public GNSShReportResponse exportGNSShReport(String dateStart, String dateEnd){
        GNSShReportResponse response = new GNSShReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("导出GNSS日报表数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseStrToDate(dateStart);
            Date end = DateLocalUtils.parseStrToDate(dateEnd);

            QueryWrapper<GNSSHour> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<GNSSHour> allData = gnssHourMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateHourlyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, GNSSHour> dataMap = allData.stream()
                    .collect(Collectors.toMap(GNSSHour::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<GNSSHour> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                GNSSHour untreated = dataMap.getOrDefault(time, new GNSSHour());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setM1X(null);
                    untreated.setM1Y(null);
                    untreated.setM1H(null);
                    untreated.setM2X(null);
                    untreated.setM2Y(null);
                    untreated.setM2H(null);
                    untreated.setM3X(null);
                    untreated.setM3Y(null);
                    untreated.setM3H(null);
                }
                filledData.add(untreated);
            }



            int orderNum = 1; // 序号从1开始
            List<GNSSUnit> units = new ArrayList<>();
            for (GNSSHour record:filledData) {
                GNSSUnit unit = new GNSSUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStr(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setM1X(-999d);
                    unit.setM1Y(-999d);
                    unit.setM1H(-999d);
                    unit.setM2X(-999d);
                    unit.setM2Y(-999d);
                    unit.setM2H(-999d);
                    unit.setM3X(-999d);
                    unit.setM3Y(-999d);
                    unit.setM3H(-999d);
                }else {
                    unit.setM1X(record.getM1X());
                    unit.setM1Y(record.getM1Y());
                    unit.setM1H(record.getM1H());
                    unit.setM2X(record.getM2X());
                    unit.setM2Y(record.getM2Y());
                    unit.setM2H(record.getM2H());
                    unit.setM3X(record.getM3X());
                    unit.setM3Y(record.getM3Y());
                    unit.setM3H(record.getM3H());
                }
                units.add(unit);
            }


            response.setUnits(units);

            meta.setStatus(200);
            meta.setMsg("导出GNSS日报表数据成功");
            return response;


        }catch (Exception e){
            logger.error("导出GNSS日报表数据失败发生异常",e);
        }
        return response;
    }








    public StagehReportResponse selectStagehReport(String dateStart, String dateEnd, Integer pageNum, Integer pageSize){
        StagehReportResponse response = new StagehReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("获取水位日报表分页数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseStrToDate(dateStart);
            Date end = DateLocalUtils.parseStrToDate(dateEnd);

            QueryWrapper<StageHour> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<StageHour> allData = stageHourMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateHourlyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, StageHour> dataMap = allData.stream()
                    .collect(Collectors.toMap(StageHour::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<StageHour> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                StageHour untreated = dataMap.getOrDefault(time, new StageHour());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setLv(null);
                    untreated.setLv2(null);
                    untreated.setLv3(null);
                    untreated.setLv4(null);
                    untreated.setLv5(null);
                    untreated.setLv6(null);
                }
                filledData.add(untreated);
            }

            // 5. 计算分页信息
            int totalCount = filledData.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 处理页码越界情况
            if (pageNum > totalPages && totalPages > 0) {
                pageNum = totalPages;
            }

            int startIndex = Math.max(0, (pageNum - 1) * pageSize);
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            // 6. 获取当前页数据
            List<StageHour> pageData = filledData.subList(startIndex, endIndex);


            int orderNum = startIndex + 1; // 序号从1开始
            List<StageUnit> units = new ArrayList<>();
            for (StageHour record:pageData) {
                StageUnit unit = new StageUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStr(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setLv(-999d);
                    unit.setLv2(-999d);
                    unit.setLv3(-999d);
                    unit.setLv4(-999d);
                    unit.setLv5(-999d);
                    unit.setLv6(-999d);
                }else {
                    unit.setLv(record.getLv());
                    unit.setLv2(record.getLv2());
                    unit.setLv3(record.getLv3());
                    unit.setLv4(record.getLv4());
                    unit.setLv5(record.getLv5());
                    unit.setLv6(record.getLv6());
                }
                units.add(unit);
            }

            response.setTotalPage(totalPages);
            response.setTotalCount(totalCount);
            response.setCurrentPage(pageNum);
            response.setUnits(units);



            meta.setStatus(200);
            meta.setMsg("获取水位日报表分页数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取水位日报表分页数据发生异常",e);
        }
        return response;
    }




    public StagedReportResponse selectStagedReport(String dateStart, String dateEnd, Integer pageNum, Integer pageSize){
        StagedReportResponse response = new StagedReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("获取水位月报表分页数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseGiveStrToDate(dateStart);
            Date end = DateLocalUtils.parseGiveStrToDate(dateEnd);


            QueryWrapper<StageDay> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<StageDay> allData = stageDayMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateDailyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, StageDay> dataMap = allData.stream()
                    .collect(Collectors.toMap(StageDay::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<StageDay> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                StageDay untreated = dataMap.getOrDefault(time, new StageDay());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setLv(null);
                    untreated.setLv2(null);
                    untreated.setLv3(null);
                    untreated.setLv4(null);
                    untreated.setLv5(null);
                    untreated.setLv6(null);
                }
                filledData.add(untreated);
            }

            // 5. 计算分页信息
            int totalCount = filledData.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 处理页码越界情况
            if (pageNum > totalPages && totalPages > 0) {
                pageNum = totalPages;
            }

            int startIndex = Math.max(0, (pageNum - 1) * pageSize);
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            // 6. 获取当前页数据
            List<StageDay> pageData = filledData.subList(startIndex, endIndex);


            int orderNum = startIndex + 1; // 序号从1开始
            List<StageUnit> units = new ArrayList<>();


            for (StageDay record:pageData) {
                StageUnit unit = new StageUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStrTwo(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setLv(-999d);
                    unit.setLv2(-999d);
                    unit.setLv3(-999d);
                    unit.setLv4(-999d);
                    unit.setLv5(-999d);
                    unit.setLv6(-999d);
                }else {
                    unit.setLv(record.getLv());
                    unit.setLv2(record.getLv2());
                    unit.setLv3(record.getLv3());
                    unit.setLv4(record.getLv4());
                    unit.setLv5(record.getLv5());
                    unit.setLv6(record.getLv6());
                }
                units.add(unit);
            }

            response.setTotalPage(totalPages);
            response.setTotalCount(totalCount);
            response.setCurrentPage(pageNum);
            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("获取水位月报表分页数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取水位月报表分页数据发生异常",e);
        }
        return response;
    }





    public StagehReportResponse exportStagehReport(String dateStart, String dateEnd){
        StagehReportResponse response = new StagehReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("导出水位日报数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseStrToDate(dateStart);
            Date end = DateLocalUtils.parseStrToDate(dateEnd);

            QueryWrapper<StageHour> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<StageHour> allData = stageHourMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateHourlyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, StageHour> dataMap = allData.stream()
                    .collect(Collectors.toMap(StageHour::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<StageHour> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                StageHour untreated = dataMap.getOrDefault(time, new StageHour());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setLv(null);
                    untreated.setLv2(null);
                    untreated.setLv3(null);
                    untreated.setLv4(null);
                    untreated.setLv5(null);
                    untreated.setLv6(null);
                }
                filledData.add(untreated);
            }



            int orderNum = 1; // 序号从1开始
            List<StageUnit> units = new ArrayList<>();
            for (StageHour record:filledData) {
                StageUnit unit = new StageUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStr(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setLv(-999d);
                    unit.setLv2(-999d);
                    unit.setLv3(-999d);
                    unit.setLv4(-999d);
                    unit.setLv5(-999d);
                    unit.setLv6(-999d);
                }else {
                    unit.setLv(record.getLv());
                    unit.setLv2(record.getLv2());
                    unit.setLv3(record.getLv3());
                    unit.setLv4(record.getLv4());
                    unit.setLv5(record.getLv5());
                    unit.setLv6(record.getLv6());
                }
                units.add(unit);
            }
            response.setUnits(units);



            meta.setStatus(200);
            meta.setMsg("导出水位日报数据成功");
            return response;


        }catch (Exception e){
            logger.error("导出水位日报数据发生异常",e);
        }
        return response;
    }







    public SeepagehReportResponse selectSeepagehReport(String dateStart, String dateEnd, Integer pageNum, Integer pageSize){
        SeepagehReportResponse response = new SeepagehReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("获取渗流日报表分页数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseStrToDate(dateStart);
            Date end = DateLocalUtils.parseStrToDate(dateEnd);

            QueryWrapper<SeepageHour> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<SeepageHour> allData = seepageHourMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateHourlyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, SeepageHour> dataMap = allData.stream()
                    .collect(Collectors.toMap(SeepageHour::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<SeepageHour> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                SeepageHour untreated = dataMap.getOrDefault(time, new SeepageHour());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setLv(null);
                    untreated.setLv2(null);
                    untreated.setLv3(null);
                }
                filledData.add(untreated);
            }

            // 5. 计算分页信息
            int totalCount = filledData.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 处理页码越界情况
            if (pageNum > totalPages && totalPages > 0) {
                pageNum = totalPages;
            }

            int startIndex = Math.max(0, (pageNum - 1) * pageSize);
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            // 6. 获取当前页数据
            List<SeepageHour> pageData = filledData.subList(startIndex, endIndex);


            int orderNum = startIndex + 1; // 序号从1开始
            List<SeepageUnit> units = new ArrayList<>();
            for (SeepageHour record:pageData) {
                SeepageUnit unit = new SeepageUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStr(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setLv(-999d);
                    unit.setLv2(-999d);
                    unit.setLv3(-999d);
                }else {
                    unit.setLv(record.getLv());
                    unit.setLv2(record.getLv2());
                    unit.setLv3(record.getLv3());
                }
                units.add(unit);
            }

            response.setTotalPage(totalPages);
            response.setTotalCount(totalCount);
            response.setCurrentPage(pageNum);
            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("获取渗流日报表分页数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取渗流日报表分页数据发生异常",e);
        }
        return response;
    }



    public SeepagedReportResponse selectSeepagedReport(String dateStart, String dateEnd, Integer pageNum, Integer pageSize){
        SeepagedReportResponse response = new SeepagedReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("获取渗流月报表分页数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseGiveStrToDate(dateStart);
            Date end = DateLocalUtils.parseGiveStrToDate(dateEnd);


            QueryWrapper<SeepageDay> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<SeepageDay> allData = seepageDayMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateDailyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, SeepageDay> dataMap = allData.stream()
                    .collect(Collectors.toMap(SeepageDay::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<SeepageDay> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                SeepageDay untreated = dataMap.getOrDefault(time, new SeepageDay());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setLv(null);
                    untreated.setLv2(null);
                    untreated.setLv3(null);
                }
                filledData.add(untreated);
            }

            // 5. 计算分页信息
            int totalCount = filledData.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 处理页码越界情况
            if (pageNum > totalPages && totalPages > 0) {
                pageNum = totalPages;
            }

            int startIndex = Math.max(0, (pageNum - 1) * pageSize);
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            // 6. 获取当前页数据
            List<SeepageDay> pageData = filledData.subList(startIndex, endIndex);


            int orderNum = startIndex + 1; // 序号从1开始
            List<SeepageUnit> units = new ArrayList<>();


            for (SeepageDay record:pageData) {
                SeepageUnit unit = new SeepageUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStrTwo(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setLv(-999d);
                    unit.setLv2(-999d);
                    unit.setLv3(-999d);
                }else {
                    unit.setLv(record.getLv());
                    unit.setLv2(record.getLv2());
                    unit.setLv3(record.getLv3());
                }
                units.add(unit);
            }

            response.setTotalPage(totalPages);
            response.setTotalCount(totalCount);
            response.setCurrentPage(pageNum);
            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("获取渗流月报表分页数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取渗流月报表分页数据发生异常",e);
        }
        return response;
    }




    public SeepagehReportResponse exportSeepagehReport(String dateStart, String dateEnd){
        SeepagehReportResponse response = new SeepagehReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("导出渗流日报表数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseStrToDate(dateStart);
            Date end = DateLocalUtils.parseStrToDate(dateEnd);

            QueryWrapper<SeepageHour> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<SeepageHour> allData = seepageHourMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateHourlyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, SeepageHour> dataMap = allData.stream()
                    .collect(Collectors.toMap(SeepageHour::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<SeepageHour> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                SeepageHour untreated = dataMap.getOrDefault(time, new SeepageHour());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setLv(null);
                    untreated.setLv2(null);
                    untreated.setLv3(null);
                }
                filledData.add(untreated);
            }


            int orderNum =  1; // 序号从1开始
            List<SeepageUnit> units = new ArrayList<>();
            for (SeepageHour record:filledData) {
                SeepageUnit unit = new SeepageUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStr(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setLv(-999d);
                    unit.setLv2(-999d);
                    unit.setLv3(-999d);
                }else {
                    unit.setLv(record.getLv());
                    unit.setLv2(record.getLv2());
                    unit.setLv3(record.getLv3());
                }
                units.add(unit);
            }

            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("导出渗流日报表数据成功");
            return response;


        }catch (Exception e){
            logger.error("导出渗流日报表数据发生异常",e);
        }
        return response;
    }




    public PowerhReportResponse selectPowerhReport(String dateStart, String dateEnd, Integer pageNum, Integer pageSize){
        PowerhReportResponse response = new PowerhReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("获取电站日报表分页数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseStrToDate(dateStart);
            Date end = DateLocalUtils.parseStrToDate(dateEnd);

            QueryWrapper<PowerHour> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<PowerHour> allData = powerHourMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateHourlyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, PowerHour> dataMap = allData.stream()
                    .collect(Collectors.toMap(PowerHour::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<PowerHour> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                PowerHour untreated = dataMap.getOrDefault(time, new PowerHour());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setPowerOne(null);
                    untreated.setPowerTwo(null);
                    untreated.setPowerThree(null);
                    untreated.setPowerSum(null);
                    untreated.setElectricSum(null);
                }
                filledData.add(untreated);
            }

            // 5. 计算分页信息
            int totalCount = filledData.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 处理页码越界情况
            if (pageNum > totalPages && totalPages > 0) {
                pageNum = totalPages;
            }

            int startIndex = Math.max(0, (pageNum - 1) * pageSize);
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            // 6. 获取当前页数据
            List<PowerHour> pageData = filledData.subList(startIndex, endIndex);


            int orderNum = startIndex + 1; // 序号从1开始
            List<PowerUnit> units = new ArrayList<>();
            for (PowerHour record:pageData) {
                PowerUnit unit = new PowerUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStr(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setPowerOne(-999d);
                    unit.setPowerTwo(-999d);
                    unit.setPowerThree(-999d);
                    unit.setPowerSum(-999d);
                    unit.setElectricSum(-999d);
                }else {
                    unit.setPowerOne(record.getPowerOne() == null?null: BigDecimal.valueOf(record.getPowerOne()).setScale(2, RoundingMode.DOWN).doubleValue());
                    unit.setPowerTwo(record.getPowerTwo() == null?null: BigDecimal.valueOf(record.getPowerTwo()).setScale(2, RoundingMode.DOWN).doubleValue());
                    unit.setPowerThree(record.getPowerThree() == null?null: BigDecimal.valueOf(record.getPowerThree()).setScale(2, RoundingMode.DOWN).doubleValue());
                    unit.setPowerSum(record.getPowerSum() == null?null: BigDecimal.valueOf(record.getPowerSum()).setScale(2, RoundingMode.DOWN).doubleValue());
                    unit.setElectricSum(record.getElectricSum() == null?null: BigDecimal.valueOf(record.getElectricSum()).setScale(2, RoundingMode.DOWN).doubleValue());
                }
                units.add(unit);
            }

            response.setTotalPage(totalPages);
            response.setTotalCount(totalCount);
            response.setCurrentPage(pageNum);
            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("获取电站日报表分页数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取电站日报表分页数据发生异常",e);
        }
        return response;
    }



    public PowerdReportResponse selectPowerdReport(String dateStart, String dateEnd, Integer pageNum, Integer pageSize){
        PowerdReportResponse response = new PowerdReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("获取电站月报表分页数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseGiveStrToDate(dateStart);
            Date end = DateLocalUtils.parseGiveStrToDate(dateEnd);


            QueryWrapper<PowerDay> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<PowerDay> allData = powerDayMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateDailyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, PowerDay> dataMap = allData.stream()
                    .collect(Collectors.toMap(PowerDay::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<PowerDay> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                PowerDay untreated = dataMap.getOrDefault(time, new PowerDay());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setPowerOne(null);
                    untreated.setPowerTwo(null);
                    untreated.setPowerThree(null);
                    untreated.setPowerSum(null);
                    untreated.setElectricSum(null);
                }
                filledData.add(untreated);
            }

            // 5. 计算分页信息
            int totalCount = filledData.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 处理页码越界情况
            if (pageNum > totalPages && totalPages > 0) {
                pageNum = totalPages;
            }

            int startIndex = Math.max(0, (pageNum - 1) * pageSize);
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            // 6. 获取当前页数据
            List<PowerDay> pageData = filledData.subList(startIndex, endIndex);


            int orderNum = startIndex + 1; // 序号从1开始
            List<PowerUnit> units = new ArrayList<>();


            for (PowerDay record:pageData) {
                PowerUnit unit = new PowerUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStrTwo(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setPowerOne(-999d);
                    unit.setPowerTwo(-999d);
                    unit.setPowerThree(-999d);
                    unit.setPowerSum(-999d);
                    unit.setElectricSum(-999d);
                }else {
                    unit.setPowerOne(record.getPowerOne() == null?null: BigDecimal.valueOf(record.getPowerOne()).setScale(2, RoundingMode.DOWN).doubleValue());
                    unit.setPowerTwo(record.getPowerTwo() == null?null: BigDecimal.valueOf(record.getPowerTwo()).setScale(2, RoundingMode.DOWN).doubleValue());
                    unit.setPowerThree(record.getPowerThree() == null?null: BigDecimal.valueOf(record.getPowerThree()).setScale(2, RoundingMode.DOWN).doubleValue());
                    unit.setPowerSum(record.getPowerSum() == null?null: BigDecimal.valueOf(record.getPowerSum()).setScale(2, RoundingMode.DOWN).doubleValue());
                    unit.setElectricSum(record.getElectricSum() == null?null: BigDecimal.valueOf(record.getElectricSum()).setScale(2, RoundingMode.DOWN).doubleValue());
                }
                units.add(unit);
            }

            response.setTotalPage(totalPages);
            response.setTotalCount(totalCount);
            response.setCurrentPage(pageNum);
            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("获取电站月报表分页数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取电站月报表分页数据发生异常",e);
        }
        return response;
    }



    public PowerhReportResponse exportPowerhReport(String dateStart, String dateEnd){
        PowerhReportResponse response = new PowerhReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("导出电站日报表数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseStrToDate(dateStart);
            Date end = DateLocalUtils.parseStrToDate(dateEnd);

            QueryWrapper<PowerHour> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<PowerHour> allData = powerHourMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateHourlyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, PowerHour> dataMap = allData.stream()
                    .collect(Collectors.toMap(PowerHour::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<PowerHour> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                PowerHour untreated = dataMap.getOrDefault(time, new PowerHour());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setPowerOne(null);
                    untreated.setPowerTwo(null);
                    untreated.setPowerThree(null);
                    untreated.setPowerSum(null);
                    untreated.setElectricSum(null);
                }
                filledData.add(untreated);
            }


            int orderNum = 1; // 序号从1开始
            List<PowerUnit> units = new ArrayList<>();
            for (PowerHour record:filledData) {
                PowerUnit unit = new PowerUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStr(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setPowerOne(-999d);
                    unit.setPowerTwo(-999d);
                    unit.setPowerThree(-999d);
                    unit.setPowerSum(-999d);
                    unit.setElectricSum(-999d);
                }else {
                    unit.setPowerOne(record.getPowerOne() == null?null: BigDecimal.valueOf(record.getPowerOne()).setScale(2, RoundingMode.DOWN).doubleValue());
                    unit.setPowerTwo(record.getPowerTwo() == null?null: BigDecimal.valueOf(record.getPowerTwo()).setScale(2, RoundingMode.DOWN).doubleValue());
                    unit.setPowerThree(record.getPowerThree() == null?null: BigDecimal.valueOf(record.getPowerThree()).setScale(2, RoundingMode.DOWN).doubleValue());
                    unit.setPowerSum(record.getPowerSum() == null?null: BigDecimal.valueOf(record.getPowerSum()).setScale(2, RoundingMode.DOWN).doubleValue());
                    unit.setElectricSum(record.getElectricSum() == null?null: BigDecimal.valueOf(record.getElectricSum()).setScale(2, RoundingMode.DOWN).doubleValue());
                }
                units.add(unit);
            }

            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("导出电站日报表数据成功");
            return response;


        }catch (Exception e){
            logger.error("导出电站日报表数据发生异常",e);
        }
        return response;
    }




    public FlowhReportResponse selectFlowhReport(String dateStart, String dateEnd, Integer pageNum, Integer pageSize){
        FlowhReportResponse response = new FlowhReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("获取流量日报表分页数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseStrToDate(dateStart);
            Date end = DateLocalUtils.parseStrToDate(dateEnd);

            QueryWrapper<FlowHour> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<FlowHour> allData = flowHourMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateHourlyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, FlowHour> dataMap = allData.stream()
                    .collect(Collectors.toMap(FlowHour::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<FlowHour> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                FlowHour untreated = dataMap.getOrDefault(time, new FlowHour());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setPower(null);
                    untreated.setFlood(null);
                    untreated.setAverFlow(null);
                }
                filledData.add(untreated);
            }

            // 5. 计算分页信息
            int totalCount = filledData.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 处理页码越界情况
            if (pageNum > totalPages && totalPages > 0) {
                pageNum = totalPages;
            }

            int startIndex = Math.max(0, (pageNum - 1) * pageSize);
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            // 6. 获取当前页数据
            List<FlowHour> pageData = filledData.subList(startIndex, endIndex);


            int orderNum = startIndex + 1; // 序号从1开始
            List<FlowUnit> units = new ArrayList<>();
            for (FlowHour record:pageData) {
                FlowUnit unit = new FlowUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStr(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setPower(-999d);
                    unit.setFlood(-999d);
                    unit.setAverFlow(-999d);
                }else {
                    unit.setPower(record.getPower());
                    unit.setFlood(record.getFlood());
                    unit.setAverFlow(record.getAverFlow());
                }
                units.add(unit);
            }

            response.setTotalPage(totalPages);
            response.setTotalCount(totalCount);
            response.setCurrentPage(pageNum);
            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("获取流量日报表分页数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取流量日报表分页数据发生异常",e);
        }
        return response;
    }




    public FlowdReportResponse selectFlowdReport(String dateStart, String dateEnd, Integer pageNum, Integer pageSize){
        FlowdReportResponse response = new FlowdReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("获取流量月报表分页数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseGiveStrToDate(dateStart);
            Date end = DateLocalUtils.parseGiveStrToDate(dateEnd);


            QueryWrapper<FlowDay> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<FlowDay> allData = flowDayMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateDailyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, FlowDay> dataMap = allData.stream()
                    .collect(Collectors.toMap(FlowDay::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<FlowDay> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                FlowDay untreated = dataMap.getOrDefault(time, new FlowDay());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setPower(null);
                    untreated.setFlood(null);
                    untreated.setAverFlow(null);
                }
                filledData.add(untreated);
            }

            // 5. 计算分页信息
            int totalCount = filledData.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 处理页码越界情况
            if (pageNum > totalPages && totalPages > 0) {
                pageNum = totalPages;
            }

            int startIndex = Math.max(0, (pageNum - 1) * pageSize);
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            // 6. 获取当前页数据
            List<FlowDay> pageData = filledData.subList(startIndex, endIndex);


            int orderNum = startIndex + 1; // 序号从1开始
            List<FlowUnit> units = new ArrayList<>();


            for (FlowDay record:pageData) {
                FlowUnit unit = new FlowUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStrTwo(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setPower(-999d);
                    unit.setFlood(-999d);
                    unit.setAverFlow(-999d);
                }else {
                    unit.setPower(record.getPower());
                    unit.setFlood(record.getFlood());
                    unit.setAverFlow(record.getAverFlow());
                }
                units.add(unit);
            }

            response.setTotalPage(totalPages);
            response.setTotalCount(totalCount);
            response.setCurrentPage(pageNum);
            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("获取流量月报表分页数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取流量月报表分页数据发生异常",e);
        }
        return response;
    }





    public FlowhReportResponse exportFlowhReport(String dateStart, String dateEnd){
        FlowhReportResponse response = new FlowhReportResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        meta.setStatus(400);
        meta.setMsg("导出流量日报表数据失败");


        try {
            LocalDateTime now = LocalDateTime.now();
            Date start = DateLocalUtils.parseStrToDate(dateStart);
            Date end = DateLocalUtils.parseStrToDate(dateEnd);

            QueryWrapper<FlowHour> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByAsc("update_time");


            List<FlowHour> allData = flowHourMapper.selectList(queryWrapper);


            // 2. 生成完整时间序列（按小时）
            List<Date> fullTimeSeries = generateHourlyTimeSeries(start, end);

            // 3. 转换为Map以便快速查找
            Map<Date, FlowHour> dataMap = allData.stream()
                    .collect(Collectors.toMap(FlowHour::getUpdateTime, e -> e, (e1, e2) -> e1));


            // 4. 合并数据，缺失的时间点创建空记录
            List<FlowHour> filledData = new ArrayList<>();
            for (Date time : fullTimeSeries) {
                FlowHour untreated = dataMap.getOrDefault(time, new FlowHour());
                if (untreated.getUpdateTime() == null) {
                    untreated.setUpdateTime(time);
                    // 其他字段设为默认值（如null或0）
                    untreated.setPower(null);
                    untreated.setFlood(null);
                    untreated.setAverFlow(null);
                }
                filledData.add(untreated);
            }


            int orderNum = 1; // 序号从1开始
            List<FlowUnit> units = new ArrayList<>();
            for (FlowHour record:filledData) {
                FlowUnit unit = new FlowUnit();
                unit.setOrderNum(orderNum++);
                unit.setUpdateTime(DateLocalUtils.parseDateToStr(record.getUpdateTime()));
                if (DateLocalUtils.parseTimeToDate(now).before(record.getUpdateTime())){
                    unit.setPower(-999d);
                    unit.setFlood(-999d);
                    unit.setAverFlow(-999d);
                }else {
                    unit.setPower(record.getPower());
                    unit.setFlood(record.getFlood());
                    unit.setAverFlow(record.getAverFlow());
                }
                units.add(unit);
            }

            response.setUnits(units);


            meta.setStatus(200);
            meta.setMsg("导出流量日报表数据成功");
            return response;


        }catch (Exception e){
            logger.error("导出流量日报表数据发生异常",e);
        }
        return response;
    }










    /**
     * 生成指定范围内的整点时间序列（按小时递增）
     */
    private List<Date> generateHourlyTimeSeries(Date startTime, Date endTime) {
        List<Date> timeSeries = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);

        while (!calendar.getTime().after(endTime)) {
            timeSeries.add(new Date(calendar.getTimeInMillis()));
            calendar.add(Calendar.HOUR, 1); // 按小时递增
        }

        return timeSeries;
    }



    /**
     * 生成指定范围内的日期序列（按天递增）
     */
    private List<Date> generateDailyTimeSeries(Date startTime, Date endTime) {
        List<Date> timeSeries = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);

        while (!calendar.getTime().after(endTime)) {
            timeSeries.add(new Date(calendar.getTimeInMillis()));
            calendar.add(Calendar.DAY_OF_MONTH, 1); // 按天递增
        }

        return timeSeries;
    }




}
