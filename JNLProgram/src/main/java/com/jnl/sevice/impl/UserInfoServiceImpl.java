package com.jnl.sevice.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.UserInfo;
import com.jnl.mapper.UserInfoMapper;
import com.jnl.sevice.UserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    UserInfoMapper userInfoMapper;


    @Override
    public UserInfo loginByName(String username, String password) {
        UserInfo userInfo = new UserInfo();
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        wrapper.eq("password",password);
        List<UserInfo> userInfos = userInfoMapper.selectList(wrapper);
        if (userInfos != null && userInfos.size() > 0){
            userInfo = userInfos.get(0);
        }
        return userInfo;
    }
}
