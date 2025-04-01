package com.jnl.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.UserInfo;


public interface UserInfoService extends IService<UserInfo> {

    UserInfo loginByName(String username, String password);


}
