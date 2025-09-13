package com.stg.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImgController {



    @GetMapping("/img/url")
    public ResponseEntity<String> getImg(){
        // 图片名称（实际项目中从数据库获取）
        String imageName = "success.png";
        // 拼接完整 URL（/img/ 对应配置的映射前缀）
        String imageUrl = "/img/" + "eduTrain" +imageName;
        // 返回 URL 给前端
        return ResponseEntity.ok(imageUrl);
    }


}
