package com.jnl.sevice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jnl.entity.TokenBlacklist;
import com.jnl.mapper.TokenBlacklistMapper;
import com.jnl.sevice.TokenBlacklistService;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistServiceImpl extends ServiceImpl<TokenBlacklistMapper, TokenBlacklist> implements TokenBlacklistService {

}
