package com.jnl.controller;


import com.jnl.service.impl.ReportServiceImpl;
import com.jnl.vo.onenetVo.RainReportResponse;
import com.jnl.vo.reportVo.*;
import com.jnl.vo.southVo.GNSSReportResponse;
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


    @GetMapping("/Report/Seepage/selectSeepagehReport")
    public SeepagehReportResponse selectSeepagehReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                       @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectSeepagehReport(dateStart,dateEnd,pageNum,pageSize);
    }



    @GetMapping("/Report/Seepage/selectSeepagedReport")
    public SeepagedReportResponse selectSeepagedReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                       @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectSeepagedReport(dateStart,dateEnd,pageNum,pageSize);
    }



    @GetMapping("/Report/Seepage/exportSeepagehReport")
    public SeepagehReportResponse exportSeepagehReport(@RequestParam String dateStart, @RequestParam String dateEnd){
        return reportService.exportSeepagehReport(dateStart,dateEnd);
    }



    @GetMapping("/Report/Power/selectPowerhReport")
    public PowerhReportResponse selectPowerhReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                   @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectPowerhReport(dateStart,dateEnd,pageNum,pageSize);
    }


    @GetMapping("/Report/Power/selectPowerdReport")
    public PowerdReportResponse selectPowerdReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                       @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectPowerdReport(dateStart,dateEnd,pageNum,pageSize);
    }


    @GetMapping("/Report/Power/exportPowerhReport")
    public PowerhReportResponse exportPowerhReport(@RequestParam String dateStart, @RequestParam String dateEnd){
        return reportService.exportPowerhReport(dateStart,dateEnd);
    }



    @GetMapping("/Report/Flow/selectFlowhReport")
    public FlowhReportResponse selectFlowhReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                   @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectFlowhReport(dateStart,dateEnd,pageNum,pageSize);
    }


    @GetMapping("/Report/Flow/selectFlowdReport")
    public FlowdReportResponse selectFlowdReport(@RequestParam String dateStart, @RequestParam String dateEnd,
                                                   @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reportService.selectFlowdReport(dateStart,dateEnd,pageNum,pageSize);
    }


    @GetMapping("/Report/Flow/exportFlowhReport")
    public FlowhReportResponse exportFlowhReport(@RequestParam String dateStart, @RequestParam String dateEnd){
        return reportService.exportFlowhReport(dateStart,dateEnd);
    }






}
