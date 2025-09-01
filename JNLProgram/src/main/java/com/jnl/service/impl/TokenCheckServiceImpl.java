package com.jnl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.TokenCheck;
import com.jnl.mapper.TokenCheckMapper;
import com.jnl.service.TokenCheckService;
import org.springframework.stereotype.Service;

@Service
public class TokenCheckServiceImpl extends ServiceImpl<TokenCheckMapper, TokenCheck> implements TokenCheckService {

}
