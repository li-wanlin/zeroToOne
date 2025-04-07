package com.jnl.sevice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.ImagesInfo;
import com.jnl.mapper.ImagesInfoMapper;
import com.jnl.sevice.ImagesInfoService;
import org.springframework.stereotype.Service;

@Service
public class ImagesInfoServiceImpl extends ServiceImpl<ImagesInfoMapper, ImagesInfo> implements ImagesInfoService {
}
