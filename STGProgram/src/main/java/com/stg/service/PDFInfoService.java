package com.stg.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.PDFInfo;
import com.stg.vo.pdfVo.PDFGeneralInfo;
import com.stg.vo.pdfVo.PDFUnit;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PDFInfoService extends IService<PDFInfo> {

    /**
     * 上传PDF文件
     * @param file
     * @param belongTo
     * @param catalogNumber
     * @return
     */
    PDFInfo uploadPDF(MultipartFile file,String belongTo,Integer catalogNumber,String fileNameReq);


    PDFInfo selectPDFInfoByName(String filename);

    /**
     * 下载PDF文件
     * @param filename
     * @param startByte
     * @return
     */
    byte[] downloadPDF(String filename,long startByte);


    /**
     *
     * @return
     */
    List<PDFUnit> selectNameAndBelongTo(List<String> belongToList);


    /**
     * 根据大类名，查询该大类下所有文件名
     * @param belongTo
     * @return
     */
    IPage<PDFInfo> selectByBelongTo(Integer pageNum, Integer pageSize,String belongTo);


    /**
     * 根据类名和文件名删除文件信息
     * @param belongTo
     * @param fileName
     * @return
     */
    Boolean deleteByInfo(String belongTo,String fileName);


    /**
     * 通用方法，根据文件和文件前缀生成下载到服务器指定位置，并返回文件名和文件路径
     * @param file
     * @param prefixPath
     * @return
     */
    PDFGeneralInfo generalPDFUp(MultipartFile file,String prefixPath);


    /**
     * 通用方法，根据文件路径和和断点续传开始位置寻找文件
     * @param filePath
     * @param startByte
     * @return
     */
    byte[] generalPreview(String filePath,long startByte);

}
