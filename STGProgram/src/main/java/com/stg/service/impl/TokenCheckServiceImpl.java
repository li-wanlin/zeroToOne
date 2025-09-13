package com.stg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.TokenCheck;
import com.stg.mapper.TokenCheckMapper;
import com.stg.service.TokenCheckService;
import org.springframework.stereotype.Service;

@Service
public class TokenCheckServiceImpl extends ServiceImpl<TokenCheckMapper, TokenCheck> implements TokenCheckService {

}
