package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.PDFInfo;
import com.jnl.mapper.PDFInfoMapper;
import com.jnl.service.PDFInfoService;
import com.jnl.vo.pdfVo.FileInfo;
import com.jnl.vo.pdfVo.PDFGeneralInfo;
import com.jnl.vo.pdfVo.PDFUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PDFInfoServiceImpl extends ServiceImpl<PDFInfoMapper, PDFInfo> implements PDFInfoService {

    private static final Logger logger = LoggerFactory.getLogger(PDFInfoServiceImpl.class);


    @Value("${PDF.upload.path}")
    private String pdfUploadPath;


    @Resource
    PDFInfoMapper pdfInfoMapper;

    @Override
    public PDFInfo uploadPDF(MultipartFile file, String belongTo,Integer catalogNumber,String fileNameReq) {
        try{
            if (file.isEmpty() || StringUtils.isEmpty(belongTo)){
                return null;
            }

            //验证文件类型是否为PDF
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")){
                return null;
            }

            File uploadDir = new File(pdfUploadPath);
            if (!uploadDir.exists()){
                uploadDir.mkdirs();
            }

            int dotIndex = originalFilename.lastIndexOf('.');
            String namePart = originalFilename.substring(0, dotIndex);
            String suffixPart = originalFilename.substring(dotIndex + 1).toLowerCase();
            String fileName = namePart + "." + suffixPart;

            if (fileNameReq != null && !fileNameReq.equals(fileName)){
                return null;
            }

            //检查是否是新文件还是需要覆盖旧文件
            PDFInfo pdfInfo = this.selectPDFInfoByName(fileName);
            if (pdfInfo == null || pdfInfo.getId() ==null){
                pdfInfo = new PDFInfo();
            }


            Path filePath = Paths.get(pdfUploadPath, fileName);


            String checksum = null;


            //计算校验和
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try(InputStream inputStream = file.getInputStream()){
                byte[] buffer = new byte[8192];
                int bytesRead;
                while((bytesRead = inputStream.read(buffer)) != -1){
                    digest.update(buffer,0,bytesRead);
                }
            }


            //完成hash计算
            byte[] hash = digest.digest();
            //将hash转化为十六进制字符串
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b:hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1){
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            checksum = hexString.toString();

            //保存文件到指定目录
            try(OutputStream outputStream = new FileOutputStream(filePath.toFile())){
                outputStream.write(file.getBytes());
            }

            //保存文件信息到数据库
            pdfInfo.setFilePath(filePath.toString());
            pdfInfo.setFileName(fileName);
            pdfInfo.setFileSize(file.getSize());
            pdfInfo.setChecksum(checksum);
            pdfInfo.setBelongTo(belongTo);
            if (catalogNumber != null && catalogNumber != 0){
                pdfInfo.setCatalogNumber(catalogNumber);
            }
            pdfInfo.setTime(new Date());


            boolean save = saveOrUpdate(pdfInfo);

            //boolean save = pdfInfoMapper.insertOrUpdate(pdfInfo);

            //boolean save = save(pdfInfo);
            if (save){
                return pdfInfo;
            }
        }catch (Exception e){
            logger.error("在存储pdf文件时发生异常",e);
        }
        return null;
    }

    @Override
    public PDFInfo selectPDFInfoByName(String filename) {
        QueryWrapper<PDFInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("file_name",filename);
        return getOne(wrapper);
    }

    @Override
    public byte[] downloadPDF(String filename, long startByte) {
        try{
            PDFInfo pdfInfo = this.selectPDFInfoByName(filename);

            //根据文件名找不到文件，返回
            if (pdfInfo == null || pdfInfo.getId() == null){
                return null;
            }
            Path filePath = Paths.get(pdfInfo.getFilePath());
            File file = filePath.toFile();

            //存储的文件地址找不到文件，返回
            if (!file.exists()){
                return null;
            }

            long fileLength = file.length();
            if (startByte >= fileLength){
                return null;
            }

            byte[] buffer = new byte[(int) (fileLength - startByte)];
            try(RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r")){
                randomAccessFile.seek(startByte);
                randomAccessFile.readFully(buffer);
            }
            return buffer;
        }catch (Exception e){
            logger.error("在service中下载文件发生异常");
        }
        return null;
    }

    @Override
    public List<PDFUnit> selectNameAndBelongTo(List<String> belongToList) {
        try {
            List<PDFInfo> pdfInfos = pdfInfoMapper.selectNameAndBelongTo();
            if (pdfInfos == null && pdfInfos.size() == 0){
                return null;
            }

            List<PDFUnit> pdfUnits = new ArrayList<>();

            for (String belongTo:belongToList) {
                PDFUnit pdfUnit = new PDFUnit();
                pdfUnit = this.tranInfoToUnit(pdfInfos, belongTo);
                pdfUnits.add(pdfUnit);
            }

            return pdfUnits;

        }catch (Exception e){
            logger.error("获取PDF文件目录和文件名时发生异常",e);
        }
        return null;
    }



    @Override
    public IPage<PDFInfo> selectByBelongTo(Integer pageNum, Integer pageSize, String belongTo) {
        try {
            List<PDFUnit> pdfUnits = new ArrayList<>();
            QueryWrapper<PDFInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("belong_to",belongTo);
            queryWrapper.orderByAsc("id");

            Page<PDFInfo> page = new Page<>(pageNum, pageSize);

            return pdfInfoMapper.selectPage(page,queryWrapper);

        }catch (Exception e){
            logger.error("获取大类下文件名出错",e);
        }
        return null;
    }

    @Override
    public Boolean deleteByInfo(String belongTo, String fileName) {
        try {
            QueryWrapper<PDFInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("belong_to",belongTo);
            queryWrapper.eq("file_name",fileName);
            return remove(queryWrapper);
        }catch (Exception e){
            logger.error("删除失败",e);
        }
        return false;
    }

    @Override
    public PDFGeneralInfo generalPDFUp(MultipartFile file, String prefixPath) {
        PDFGeneralInfo generalInfo = new PDFGeneralInfo();
        generalInfo.setIfCom(false);
        try {
            if (file.isEmpty()){
                return generalInfo;
            }

            //验证文件类型是否为PDF
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")){
                return generalInfo;
            }

            File uploadDir = new File(prefixPath);
            if (!uploadDir.exists()){
                uploadDir.mkdirs();
            }

            int dotIndex = originalFilename.lastIndexOf('.');
            String namePart = originalFilename.substring(0, dotIndex);
            String suffixPart = originalFilename.substring(dotIndex + 1).toLowerCase();
            String fileName = namePart + "." + suffixPart;




            Path filePath = Paths.get(prefixPath, fileName);


            //保存文件到指定目录
            try(OutputStream outputStream = new FileOutputStream(filePath.toFile())){
                outputStream.write(file.getBytes());
            }

            generalInfo.setIfCom(true);
            generalInfo.setFileName(fileName);
            generalInfo.setFilePath(filePath.toString());
            return generalInfo;
        }catch (Exception e){
            logger.error("上传PDF通用方法发生异常",e);
        }
        return generalInfo;
    }





    @Override
    public byte[] generalPreview(String filePath, long startByte) {
        try{
            Path path = Paths.get(filePath);
            File file = path.toFile();

            //存储的文件地址找不到文件，返回
            if (!file.exists()){
                return null;
            }

            long fileLength = file.length();
            if (startByte >= fileLength){
                return null;
            }

            byte[] buffer = new byte[(int) (fileLength - startByte)];
            try(RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r")){
                randomAccessFile.seek(startByte);
                randomAccessFile.readFully(buffer);
            }
            return buffer;
        }catch (Exception e){
            logger.error("在service中下载文件发生异常");
        }
        return null;
    }


    public PDFUnit tranInfoToUnit(List<PDFInfo> pdfInfos,String belongTo){
        try {
            if (pdfInfos == null || pdfInfos.size() == 0 || belongTo == null){
                return null;
            }

            List<String> fileNames = pdfInfos.stream()
                    .filter(pdfInfo -> pdfInfo.getBelongTo().equals(belongTo))
                    .map(PDFInfo::getFileName)
                    .collect(Collectors.toList());

            Integer startIndex = 1;
            List<FileInfo> fileInfos = new ArrayList<>();

            for (String fileName:fileNames) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setOrderNum(startIndex++);
                fileInfo.setFileName(fileName);
                fileInfos.add(fileInfo);
            }

            PDFUnit pdfUnit = new PDFUnit();
            pdfUnit.setBelongTo(belongTo);
            pdfUnit.setFileInfos(fileInfos);
            return pdfUnit;

        }catch (Exception e){
            logger.error("转化PDFInfo出错",e);
        }
        return null;
    }


}
