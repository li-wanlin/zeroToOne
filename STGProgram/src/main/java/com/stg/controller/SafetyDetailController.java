package com.stg.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stg.entity.SafetyDetail;
import com.stg.service.impl.SafetyDetailServiceImpl;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.SafetyDetailVo.SafetyDetailResponse;
import com.stg.vo.SafetyDetailVo.SafetyUnit;
import com.stg.vo.functionVo.Meta;
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
public class SafetyDetailController {

    @Resource
    SafetyDetailServiceImpl safetyDetailService;


    private static final Logger logger = LoggerFactory.getLogger(SafetyDetailController.class);



    @PostMapping("/fourManagement/SafetyDetail/insertByInfo")
    public Meta insertByInfo(@RequestParam(value = "project",required = false) String project,
                             @RequestParam(value = "time",required = false) String time,
                             @RequestParam(value = "orgUnit",required = false) String orgUnit,
                             @RequestParam(value = "bearUnit",required = false) String bearUnit,
                             @RequestParam(value = "conclusion",required = false) String conclusion,
                             @RequestParam(value = "file",required = false) MultipartFile file){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("安全鉴定详情插入数据失败");
        try {
            if (project == null && time == null && orgUnit == null && bearUnit == null
                    && conclusion == null && file == null){
                return meta;
            }

            Boolean insert = safetyDetailService.insertByInfo(project, time, orgUnit, bearUnit, conclusion, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("安全鉴定详情插入数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("安全鉴定详情插入数据Controller层发生异常",e);
        }
        return meta;
    }


    @DeleteMapping("/fourManagement/SafetyDetail/deleteByInfo")
    public Meta deleteByInfo(@RequestBody SafetyUnit safetyUnit){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("安全鉴定详情删除数据失败");
        try {
            Boolean delete = safetyDetailService.deleteByInfo(safetyUnit.getId());
            if (delete != null && delete){
                meta.setStatus(200);
                meta.setMsg("安全鉴定详情删除数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("安全鉴定详情删除数据Controller层发生异常",e);
        }
        return meta;
    }


    @PutMapping("/fourManagement/SafetyDetail/updateByInfo")
    public Meta updateByInfo(@RequestParam(value = "id") Integer id,
                             @RequestParam(value = "project",required = false) String project,
                             @RequestParam(value = "time",required = false) String time,
                             @RequestParam(value = "orgUnit",required = false) String orgUnit,
                             @RequestParam(value = "bearUnit",required = false) String bearUnit,
                             @RequestParam(value = "conclusion",required = false) String conclusion,
                             @RequestParam(value = "file",required = false) MultipartFile file){

        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("安全鉴定详情更新数据失败");
        try {
            if (project == null && time == null && orgUnit == null && bearUnit == null
                    && conclusion == null && file == null){
                return meta;
            }

            Boolean insert = safetyDetailService.updateByInfo(id,project, time, orgUnit, bearUnit, conclusion, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("安全鉴定详情更新数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("安全鉴定详情更新数据Controller层发生异常",e);
        }
        return meta;
    }


    @GetMapping("/fourManagement/SafetyDetail/pagedQuery")
    public SafetyDetailResponse pagedQuery(@RequestParam(value = "pageNum")Integer pageNum,
                                          @RequestParam(value = "pageSize")Integer pageSize){

        SafetyDetailResponse response = new SafetyDetailResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            IPage<SafetyDetail> page = safetyDetailService.pageQuery(pageNum, pageSize);
            if (page != null && page.getRecords().size() > 0){
                List<SafetyDetail> safetyDetails = page.getRecords();
                int startIndex = (pageNum - 1) * pageSize + 1;
                List<SafetyUnit> safetyUnits = new ArrayList<>();
                for (SafetyDetail sa:safetyDetails) {
                    SafetyUnit safetyUnit = new SafetyUnit();
                    safetyUnit.setId(sa.getId());
                    safetyUnit.setOrderNum(startIndex++);
                    safetyUnit.setTime(DateLocalUtils.parseGiveDaToStr(sa.getTime(),"yyyy-MM-dd HH:mm:ss"));
                    safetyUnit.setProject(sa.getProject());
                    safetyUnit.setOrgUnit(sa.getOrgUnit());
                    safetyUnit.setBearUnit(sa.getBearUnit());
                    safetyUnit.setConclusion(sa.getConclusion());

                    safetyUnits.add(safetyUnit);
                }

                response.setSafetyUnits(safetyUnits);
                response.setTotalPage((int)page.getPages());
                response.setTotalCount((int)page.getTotal());
                response.setCurrentPage(pageNum);
                meta.setStatus(200);
                meta.setMsg("获取安全鉴定详情分页数据成功");
                return response;

            }
        }catch (Exception e){
            logger.error("获取安全鉴定详情分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取安全鉴定详情分页数据失败");
        return response;
    }



    @PostMapping("/fourManagement/SafetyDetail/previewById")
    public ResponseEntity<org.springframework.core.io.Resource> previewById(@RequestBody SafetyUnit safetyUnit,
                                                                           @RequestHeader(value = "Range", required = false) String rangeHeader){

        try{
            long startByte = 0;
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")){
                String[] range = rangeHeader.substring(6).split("-");
                startByte = Long.parseLong(range[0]);
            }


            byte[] fileBytes = safetyDetailService.previewById(safetyUnit.getId(), startByte);

            org.springframework.core.io.Resource resource = new ByteArrayResource(fileBytes);

            //对文件名进行编码
            String encodeFilename = URLEncoder.encode(safetyDetailService.getFileNameById(safetyUnit.getId()), "UTF-8");


            //设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,"inline; filename*=UTF-8'' " + encodeFilename);//inline,attachment


            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(fileBytes.length);


            //30天缓存时间
            Integer cache = 60 * 60 * 24 * 30;
            headers.setCacheControl("public, max-age=" + cache.toString());


            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        }catch (Exception e){
            logger.error("在controller中服务器中下载PDF文件时发生异常",e);
        }
        return ResponseEntity.notFound().build();
    }


}
