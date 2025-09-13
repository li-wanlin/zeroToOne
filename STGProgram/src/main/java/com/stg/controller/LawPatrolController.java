package com.stg.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stg.entity.LawPatrol;
import com.stg.service.impl.LawPatrolServiceImpl;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.patrolVo.LawImgResponse;
import com.stg.vo.patrolVo.PatrolInfo;
import com.stg.vo.patrolVo.RePatrolResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
public class LawPatrolController {


    @Resource
    LawPatrolServiceImpl lawPatrolService;


    private static final Logger logger = LoggerFactory.getLogger(LawPatrolController.class);



    @GetMapping("/fourGovern/RePatrol/selectLatestFifty")
    public RePatrolResponse selectLatestFifty(){
        RePatrolResponse patrolResponse = new RePatrolResponse();
        Meta meta = new Meta();
        patrolResponse.setMeta(meta);
        try {

            List<PatrolInfo> patrolInfos = lawPatrolService.selectLatestFifty();
           if (patrolInfos != null && patrolInfos.size() != 0){
               meta.setMsg("获取执法巡查最新50条数据成功");
               meta.setStatus(200);

               patrolResponse.setPatrolInfos(patrolInfos);
               return patrolResponse;
           }
        }catch (Exception e){
            logger.error("获取执法巡查最新50条数据Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("获取执法巡查最新50条数据失败");

        return patrolResponse;
    }



    @PostMapping("/fourGovern/RePatrol/testFile")
    public void testFile(PatrolInfo patrolInfo,@RequestParam(value = "files",required = false) MultipartFile[] files){
        logger.info("这是执法巡查除文件以外的数据：{}", JSON.toJSONString(patrolInfo));
        if (files == null){
            logger.info("这次执法巡查未加入图片数据");
        }else {
            logger.info("这次执法巡查加入图片数量为：{}",files.length);
        }
    }






    @PostMapping("/fourGovern/RePatrol/insertByInfo")
    public Meta insertByInfo(PatrolInfo patrolInfo,@RequestParam(value = "files",required = false) MultipartFile[] files){
        Meta meta = new Meta();
        logger.info("这是执法巡查除文件以外的数据：{}", JSON.toJSONString(patrolInfo));
        if (files == null){
            logger.info("这次执法巡查未加入图片数据");
        }else {
            logger.info("这次执法巡查加入图片数量为：{}",files.length);
        }


        try {
            Boolean insert = lawPatrolService.InsertByInfo(patrolInfo,files);
            if (insert){
                meta.setStatus(200);
                meta.setMsg("插入执法巡查数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("插入执法巡查数据Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("插入执法巡查数据失败");

        return meta;
    }

    @DeleteMapping("/fourGovern/RePatrol/deleteByInfo")
    public Meta deleteByInfo(@RequestBody PatrolInfo patrolInfo){
        Meta meta = new Meta();
        try {
            Boolean delete = lawPatrolService.DeleteByInfo(patrolInfo);
            if (delete){
                meta.setStatus(200);
                meta.setMsg("删除执法巡查数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("删除执法巡查数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("删除执法巡查数据失败");
        return meta;
    }


    @PutMapping("/fourGovern/RePatrol/updateByInfo")
    public Meta updateByInfo(PatrolInfo patrolInfo,@RequestParam(value = "files",required = false) MultipartFile[] files){
        Meta meta = new Meta();
        try {
            Boolean update = lawPatrolService.UpdateByInfo(patrolInfo,files);
            if (update){
                meta.setStatus(200);
                meta.setMsg("更新执法巡查数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("更新执法巡查数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("更新执法巡查数据失败");
        return meta;
    }


    @GetMapping("/fourGovern/RePatrol/pagedQuery")
    public RePatrolResponse pagedQuery(@RequestParam(value = "pageNum") Integer pageNum,
                                       @RequestParam(value = "pageSize") Integer pageSize){

        RePatrolResponse response = new RePatrolResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            IPage<LawPatrol> patrolPage = lawPatrolService.pagedQuery(pageNum, pageSize);
            if (patrolPage != null){
                List<LawPatrol> patrolList = patrolPage.getRecords();
                if (patrolList.size() > 0){
                    int startIndex = (pageNum - 1) * pageSize + 1;
                    List<PatrolInfo> patrolInfos = new ArrayList<>();
                    for (LawPatrol re:patrolList) {
                        PatrolInfo patrolInfo = new PatrolInfo();
                        patrolInfo.setId(re.getId());
                        patrolInfo.setOrderNum(startIndex++);
                        patrolInfo.setPatrolTime(DateLocalUtils.parseGiveDaToStr(re.getPatrolTime(),"yyyy-MM-dd"));
                        patrolInfo.setPatrolType(re.getPatrolType());
                        patrolInfo.setPatrolArea(re.getPatrolArea());
                        patrolInfo.setPatrolState(re.getPatrolState());
                        patrolInfo.setPatrolProblem(re.getPatrolProblem());
                        patrolInfo.setSolutions(re.getSolutions());
                        patrolInfo.setPatrolLeader(re.getPatrolLeader());
                        patrolInfo.setPatrolPerson(re.getPatrolPerson());
                        patrolInfo.setIfComplete(re.getIfComplete());
                        patrolInfo.setCompleteState(re.getCompleteState());
                        patrolInfo.setPatrolNotes(re.getPatrolNotes());
                        patrolInfos.add(patrolInfo);
                    }
                    meta.setStatus(200);
                    meta.setMsg("获取执法巡查分页数据成功");

                    response.setCurrentPage(pageNum);
                    response.setTotalPage((int)patrolPage.getPages());
                    response.setTotalCount((int)patrolPage.getTotal());
                    response.setPatrolInfos(patrolInfos);
                    return response;
                }
            }
        }catch (Exception e){
            logger.error("获取执法巡查分页数据Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("获取执法巡查分页数据失败");
        return response;
    }



    @GetMapping("/fourGovern/RePatrol/pagedQueryByYear")
    public RePatrolResponse pagedQueryByYera(@RequestParam(value = "pageNum") Integer pageNum,
                                             @RequestParam(value = "pageSize") Integer pageSize,
                                             @RequestParam(value = "year") String year){

        RePatrolResponse response = new RePatrolResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            IPage<LawPatrol> patrolPage = lawPatrolService.pagedQueryByYear(pageNum, pageSize,year);
            if (patrolPage != null){
                List<LawPatrol> patrolList = patrolPage.getRecords();
                if (patrolList.size() > 0){
                    int startIndex = (pageNum - 1) * pageSize + 1;
                    List<PatrolInfo> patrolInfos = new ArrayList<>();
                    for (LawPatrol re:patrolList) {
                        PatrolInfo patrolInfo = new PatrolInfo();
                        patrolInfo.setId(re.getId());
                        patrolInfo.setOrderNum(startIndex++);
                        patrolInfo.setPatrolTime(DateLocalUtils.parseGiveDaToStr(re.getPatrolTime(),"yyyy-MM-dd"));
                        patrolInfo.setPatrolType(re.getPatrolType());
                        patrolInfo.setPatrolArea(re.getPatrolArea());
                        patrolInfo.setPatrolState(re.getPatrolState());
                        patrolInfo.setPatrolProblem(re.getPatrolProblem());
                        patrolInfo.setSolutions(re.getSolutions());
                        patrolInfo.setPatrolLeader(re.getPatrolLeader());
                        patrolInfo.setPatrolPerson(re.getPatrolPerson());
                        patrolInfo.setIfComplete(re.getIfComplete());
                        patrolInfo.setCompleteState(re.getCompleteState());
                        patrolInfo.setPatrolNotes(re.getPatrolNotes());
                        patrolInfos.add(patrolInfo);
                    }
                    meta.setStatus(200);
                    meta.setMsg("获取执法巡查分页数据成功");

                    response.setCurrentPage(pageNum);
                    response.setTotalPage((int)patrolPage.getPages());
                    response.setTotalCount((int)patrolPage.getTotal());
                    response.setPatrolInfos(patrolInfos);
                    return response;
                }
            }
        }catch (Exception e){
            logger.error("获取执法巡查分页数据Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("获取执法巡查分页数据失败");
        return response;
    }


    @PostMapping("/fourGovern/RePatrol/getFileNames")
    public LawImgResponse getFileNames(@RequestBody PatrolInfo info){

        LawImgResponse response = new LawImgResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<String> fileNames = lawPatrolService.getFileNames(info.getId());
            if (fileNames != null && fileNames.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取执法巡查图片数据成功");
                response.setFileNames(fileNames);
                return response;
            }else if (fileNames != null){
                meta.setStatus(200);
                meta.setMsg("该行没有图片");
                return response;
            }

        }catch (Exception e){
            logger.error("获取执法巡查图片数据发生异常",e);
        }
        meta.setStatus(100);
        meta.setMsg("获取执法巡查图片数据失败");
        return response;
    }



    @GetMapping("/fourGovern/RePatrol/previewByName")
    public ResponseEntity<org.springframework.core.io.Resource> previewByName(@RequestParam(value = "fileName") String fileName){
        try {

            byte[] fileBytes = lawPatrolService.previewByName(fileName);

            org.springframework.core.io.Resource resource = new ByteArrayResource(fileBytes);

            //对文件名进行编码
            String encodeFilename = URLEncoder.encode(fileName, "UTF-8");


            //设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,"inline; filename*=UTF-8'' " + encodeFilename);//inline,attachment


            headers.setContentType(this.getContentType(fileName));
            headers.setContentLength(fileBytes.length);


            //30天缓存时间
            Integer cache = 60 * 60 * 24 * 30;
            headers.setCacheControl("public, max-age=" + cache.toString());


            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        }catch (Exception e){
            logger.error("在controller中服务器中下载文件时发生异常",e);
        }
        return ResponseEntity.notFound().build();
    }


    //根据文件扩展名获取Content-Type
    private MediaType getContentType(String fileName){
        try{
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            switch(fileExtension){
                case "jpg":
                case "jpeg":
                    return MediaType.IMAGE_JPEG;
                case "png":
                    return MediaType.IMAGE_PNG;
                case "gif":
                    return MediaType.IMAGE_GIF;
                default:
                    return MediaType.APPLICATION_OCTET_STREAM;
            }
        }catch (Exception e){
            logger.error("获取图片扩展名时发生错误",e);
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
