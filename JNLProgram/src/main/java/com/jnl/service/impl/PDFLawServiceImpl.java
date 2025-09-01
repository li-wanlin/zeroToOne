package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jnl.entity.PDFInfo;
import com.jnl.vo.pdfVo.PDFInfoResponse;
import com.jnl.vo.pdfVo.PDFUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PDFLawServiceImpl {


    @Resource
    PDFInfoServiceImpl pdfInfoService;

    private static final Logger logger = LoggerFactory.getLogger(PDFLawServiceImpl.class);




    /**
     * 获取每个大类中最新的5条数据
     * @return
     */
    public List<PDFUnit> selectNameAndBelongTo(){
        try {
            String belongTo1 = "法律";
            String belongTo2 = "国家行政法规";
            String belongTo3 = "水利部规章";
            String belongTo4 = "河南省地方法规";
            String belongTo5 = "河南省政府规章";
            String belongTo6 = "河南省规范性文件";
            String belongTo7 = "其他";


            List<String> arrs = Arrays.asList(belongTo1, belongTo2, belongTo3, belongTo4, belongTo5, belongTo6, belongTo7);
            List<String> belongToList = new ArrayList<>(arrs);

            return pdfInfoService.selectNameAndBelongTo(belongToList);
        }catch (Exception e){
            logger.error("获取法律法规局部数据失败发生异常");
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
            logger.error("法律法规预览发生异常");
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
            logger.error("法律法规PDF文件上传发生异常",e);
        }
        return false;
    }



    public Boolean deleteByInfo(String belongTo,String fileName){
        try {
            Boolean delete = pdfInfoService.deleteByInfo(belongTo, fileName);
            if (delete){
                return true;
            }
        }catch (Exception e){
            logger.error("删除法律法规PDF文件信息失败",e);
        }
        return false;
    }





}
