package com.stg.controller;


import com.stg.service.impl.ReportServiceImpl;
import com.stg.vo.onenetVo.RainReportResponse;
import com.stg.vo.reportVo.*;
import com.stg.vo.southVo.GNSSReportResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ReportController {

    @Resource
    ReportServiceImpl reportService;



    @GetMapping("/Report/Rain/selectRainhReport")
    public RainhReportResponse selectRainhReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                 @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectRainhReport(dateStart,dateEnd,pageNum,pageSize);
    }


    @GetMapping("/Report/Rain/selectRaindReport")
    public RaindReportResponse selectRaindReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                 @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectRaindReport(dateStart,dateEnd,pageNum,pageSize);
    }


    @GetMapping("/Report/Rain/exportRainhReport")
    public RainhReportResponse exportRainhReport(@RequestParam String dateStart, @RequestParam String dateEnd){
        return reportService.exportRainhReport(dateStart,dateEnd);
    }




    @GetMapping("/Report/GNSS/selectGNSShReport")
    public GNSShReportResponse selectGNSShReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                 @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectGNSShReport(dateStart,dateEnd,pageNum,pageSize);
    }



    @GetMapping("/Report/GNSS/selectGNSSdReport")
    public GNSSdReportResponse selectGNSSdReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                 @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectGNSSdReport(dateStart,dateEnd,pageNum,pageSize);
    }


    @GetMapping("/Report/GNSS/exportGNSShReport")
    public GNSShReportResponse exportGNSShReport(@RequestParam String dateStart, @RequestParam String dateEnd){
        return reportService.exportGNSShReport(dateStart,dateEnd);
    }



    @GetMapping("/Report/Stage/selectStagehReport")
    public StagehReportResponse selectStagehReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                   @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectStagehReport(dateStart,dateEnd,pageNum,pageSize);
    }


    @GetMapping("/Report/Stage/selectStagedReport")
    public StagedReportResponse selectStagedReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                   @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectStagedReport(dateStart,dateEnd,pageNum,pageSize);
    }


    @GetMapping("/Report/Stage/exportStagehReport")
    public StagehReportResponse exportStagehReport(@RequestParam String dateStart, @RequestParam String dateEnd){
        return reportService.exportStagehReport(dateStart,dateEnd);
    }



    @GetMapping("/Report/Water/selectWaterhReport")
    public WaterhReportResponse selectWaterhReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                   @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectWaterhReport(dateStart,dateEnd,pageNum,pageSize);
    }


    @GetMapping("/Report/Water/selectWaterdReport")
    public WaterdReportResponse selectWaterdReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                   @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectWaterdReport(dateStart,dateEnd,pageNum,pageSize);
    }

    @GetMapping("/Report/Water/exportWaterhReport")
    public WaterhReportResponse exportWaterhReport(@RequestParam String dateStart, @RequestParam String dateEnd){
        return reportService.exportWaterhReport(dateStart,dateEnd);
    }






}
