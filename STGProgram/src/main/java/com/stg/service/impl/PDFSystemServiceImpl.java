package com.stg.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stg.entity.PDFInfo;
import com.stg.vo.pdfVo.PDFInfoResponse;
import com.stg.vo.pdfVo.PDFUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PDFSystemServiceImpl {

    @Resource
    PDFInfoServiceImpl pdfInfoService;


    private static final Logger logger = LoggerFactory.getLogger(PDFSystemServiceImpl.class);


    /**
     * 获取每个大类中最新的5条数据
     * @return
     */
    public List<PDFUnit> selectNameAndBelongTo(){
        try {
            String belongTo1 = "安全管理类";
            String belongTo2 = "运行管护类";
            String belongTo3 = "综合管理类";


            List<String> arrs = Arrays.asList(belongTo1, belongTo2, belongTo3);
            List<String> belongToList = new ArrayList<>(arrs);

            return pdfInfoService.selectNameAndBelongTo(belongToList);
        }catch (Exception e){
            logger.error("获取制度管理局部数据失败发生异常");
        }
        return null;
    }


    /**
     * PDF预览
     * @param fileName
     * @param startByte
     * @return
     */
    public byte[] PDFPreview(String fileName,long startByte){
        try {
            if (fileName == null){
                return null;
            }
            return pdfInfoService.downloadPDF(fileName, startByte);

        }catch (Exception e){
            logger.error("制度管理预览发生异常");
        }
        return null;
    }


    /**
     * 获取大类下文件名
     * @param belongTo
     * @return
     */
    public PDFInfoResponse selectByBelongTo(Integer pageNum, Integer pageSize, String belongTo){
        PDFInfoResponse response = new PDFInfoResponse();

        try {
            if (belongTo == null || pageNum == null || pageSize == null
                    || pageNum == 0 || pageSize == 0){
                return null;
            }

            IPage<PDFInfo> page = pdfInfoService.selectByBelongTo(pageNum, pageSize, belongTo);
            if (page != null && page.getRecords() != null && page.getRecords().size() > 0){
                List<PDFInfo> pdfInfos = page.getRecords();


                List<PDFUnit> pdfUnits = new ArrayList<>();

                PDFUnit pdfUnit = pdfInfoService.tranInfoToUnit(pdfInfos, belongTo);
                pdfUnits.add(pdfUnit);


                response.setTotalPage((int)page.getPages());
                response.setCurrentPage(pageNum);
                response.setTotalCount((int)page.getTotal());
                response.setPdfUnits(pdfUnits);
                return response;
            }

        }catch (Exception e){
            logger.error("获取大类下文件名出错",e);
        }
        return null;
    }


    public Boolean uploadPDF(MultipartFile file, String belongTo,String fileNameReq){
        try {
            PDFInfo pdfInfo = pdfInfoService.uploadPDF(file, belongTo,null,fileNameReq);
            return pdfInfo != null && pdfInfo.getId() != null;
        }catch (Exception e){
            logger.error("制度管理PDF文件上传发生异常",e);
        }
        return null;
    }



    public Boolean deleteByInfo(String belongTo,String fileName){
        try {
            Boolean delete = pdfInfoService.deleteByInfo(belongTo, fileName);
            if (delete){
                return true;
            }
        }catch (Exception e){
            logger.error("删除制度管理PDF文件信息失败",e);
        }
        return false;
    }





}
