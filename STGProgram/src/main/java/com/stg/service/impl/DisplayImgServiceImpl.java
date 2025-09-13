package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.DisplayImg;
import com.stg.mapper.DisplayImgMapper;
import com.stg.service.DisplayImgService;
import com.stg.vo.display.DisplayImgResponse;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.imageInfoVo.ImageGeneralInfo;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DisplayImgServiceImpl extends ServiceImpl<DisplayImgMapper, DisplayImg> implements DisplayImgService {

    @Resource
    DisplayImgMapper displayImgMapper;


    @Resource
    ImagesInfoServiceImpl imagesInfoService;


    @Value("${image.upload.display}")
    private String displayImage;


    private static final Logger logger = LoggerFactory.getLogger(DisplayImgServiceImpl.class);




    @Override
    public Boolean insertByInfo(Integer catalogNumber, MultipartFile[] files) {
        try {
            if (catalogNumber == null || files == null){
                return false;
            }

            List<Integer> listAll = Arrays.asList(1, 2, 3, 4, 5);
            if (!listAll.contains(catalogNumber)){
                return false;
            }

            List<String> filePaths = this.selectPathsByNum(catalogNumber);
            if (filePaths != null && filePaths.size() > 0){
                this.batchDeleteFiles(filePaths,catalogNumber);
            }



            List<DisplayImg> infos = new ArrayList<>();

            for (MultipartFile file:files) {

                ImageGeneralInfo generalInfo = imagesInfoService.generalUpload(file, displayImage);
                if (!generalInfo.getIfCom()){
                    continue;
                }
                DisplayImg info = new DisplayImg();
                info.setCatalogNumber(catalogNumber);
                info.setFileName(generalInfo.getFileName());
                info.setFilePath(generalInfo.getFilePath());
                info.setUpdateTime(new Date());
                infos.add(info);
            }

            return saveBatch(infos);
        }catch (Exception e){
            logger.error("插入显示图片数据发生异常",e);
        }
        return false;
    }


    /**
     * catalogNumber:1为注册登记信息，返回一张图片；2为取水许可信息，返回一张图片；3为水库基本信息，返回5张图片；
     * 4为水厂基本信息，返回5张图片；5为库区要素中保护范围，返回一张图片
     * @param catalogNumber
     * @return
     */
    @Override
    public DisplayImgResponse selectDisplay(Integer catalogNumber) {
        DisplayImgResponse response = new DisplayImgResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        meta.setStatus(400);
        meta.setMsg("查询显示图片失败");

        try {
            List<Integer> listAll = Arrays.asList(1, 2, 3, 4, 5);
            List<Integer> listOne = Arrays.asList(1, 2, 5);
            if (catalogNumber == null || !listAll.contains(catalogNumber)){
                return response;
            }

            List<String> locations = new ArrayList<>();
            if (listOne.contains(catalogNumber)){
                QueryWrapper<DisplayImg> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("catalog_number",catalogNumber);
                queryWrapper.isNotNull("file_name");
                queryWrapper.isNotNull("file_path");
                queryWrapper.orderByDesc("update_time");
                queryWrapper.last("limit 1");

                List<DisplayImg> displayImgs = displayImgMapper.selectList(queryWrapper);
                if (displayImgs == null || displayImgs.size() == 0){
                    return null;
                }

                DisplayImg displayImg = displayImgs.get(0);
                String fileName = displayImg.getFileName();



                String location = "http://8.140.23.248:8082/STGProgram/display/" + fileName;   //服务器地址
                //String location = "http://localhost:8082/STGProgram/display/" + fileName;   //本地地址

                locations.add(location);
                response.setLocations(locations);
                meta.setStatus(200);
                meta.setMsg("查询显示图片成功");

                return response;

            }


            QueryWrapper<DisplayImg> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("catalog_number",catalogNumber);
            queryWrapper.isNotNull("file_name");
            queryWrapper.isNotNull("file_path");
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 5");

            List<DisplayImg> displayImgs = displayImgMapper.selectList(queryWrapper);
            if (displayImgs == null || displayImgs.size() == 0){
                return response;
            }


            for (DisplayImg displayImg: displayImgs) {
                locations.add("http://8.140.23.248:8082/STGProgram/display/" + displayImg.getFileName());  //服务器地址

                //locations.add("http://localhost:8082/STGProgram/display/" + displayImg.getFileName());  //本地地址

            }

            response.setLocations(locations);
            meta.setStatus(200);
            meta.setMsg("查询显示图片成功");
            return response;


        }catch (Exception e){
            logger.error("查询显示图片发生异常",e);
        }
        return response;
    }



    public List<String> selectPathsByNum(Integer catalogNumber){
        try {
            QueryWrapper<DisplayImg> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("catalog_number",catalogNumber);
            queryWrapper.isNotNull("file_path");

            List<DisplayImg> displayImgs = displayImgMapper.selectList(queryWrapper);
            if (displayImgs == null || displayImgs.size() == 0){
                return null;
            }

            return displayImgs.stream().map(DisplayImg::getFilePath).collect(Collectors.toList());

        }catch (Exception e){
            logger.error("获取显示图片路径发生异常",e);
        }
        return null;

    }








    public void batchDeleteFiles(List<String> filePaths,Integer catalogNumber) {
        try {
            for (String path : filePaths) {
                deleteFile(path);
            }

            QueryWrapper<DisplayImg> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("catalog_number",catalogNumber);

            remove(queryWrapper);

        }catch (Exception e){
            logger.error("",e);
        }
    }



    /**
     * 删除单个文件（绝对路径）
     * @param absolutePath 文件绝对路径
     * @return 删除结果
     */
    public boolean deleteFile(String absolutePath) {
        if (absolutePath == null || absolutePath.isEmpty()) {
            logger.warn("文件路径为空，删除失败");
            return false;
        }

        try {
            // 1. 规范化路径（防目录遍历攻击）
            Path normalizedPath = Paths.get(absolutePath).normalize();
            String normalizedPathStr = normalizedPath.toString();


            // 4. 检查文件是否存在
            if (!Files.exists(normalizedPath)) {
                logger.warn("文件不存在: {}", normalizedPathStr);
                return false;
            }

            // 5. 检查是否为文件（非目录）
            if (!Files.isRegularFile(normalizedPath)) {
                logger.warn("路径不是文件: {}", normalizedPathStr);
                return false;
            }

            // 6. 执行删除
            Files.delete(normalizedPath);
            logger.info("文件删除成功: {}", normalizedPathStr);
            return true;
        } catch (Exception e) {
            logger.error("删除文件异常: {}", absolutePath, e);
            return false;
        }
    }

}
