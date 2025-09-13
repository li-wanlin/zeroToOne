package com.stg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.TokenBlacklist;
import com.stg.mapper.TokenBlacklistMapper;
import com.stg.service.TokenBlacklistService;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistServiceImpl extends ServiceImpl<TokenBlacklistMapper, TokenBlacklist> implements TokenBlacklistService {

}
