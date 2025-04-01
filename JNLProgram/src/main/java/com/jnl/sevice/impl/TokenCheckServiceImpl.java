package com.jnl.sevice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.TokenCheck;
import com.jnl.mapper.TokenCheckMapper;
import com.jnl.sevice.TokenCheckService;
import org.springframework.stereotype.Service;

@Service
public class TokenCheckServiceImpl extends ServiceImpl<TokenCheckMapper, TokenCheck> implements TokenCheckService {

}
