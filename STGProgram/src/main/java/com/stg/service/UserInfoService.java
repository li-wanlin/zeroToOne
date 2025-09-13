package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.UserInfo;


public interface UserInfoService extends IService<UserInfo> {

    UserInfo loginByName(String username, String password);


}
