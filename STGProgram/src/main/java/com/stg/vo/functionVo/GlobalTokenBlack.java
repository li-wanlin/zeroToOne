package com.stg.vo.functionVo;


import com.stg.entity.TokenBlacklist;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;



@Component
public class GlobalTokenBlack {
    private final List<TokenBlacklist> globalTokenBlackList;


    public GlobalTokenBlack() {
        // 使用 CopyOnWriteArrayList 创建线程安全的 List
        this.globalTokenBlackList = new CopyOnWriteArrayList<>();
    }

    public void addTokenBlacklist(TokenBlacklist tokenBlacklist) {
        // 向线程安全的 List 中添加元素
        globalTokenBlackList.add(tokenBlacklist);
    }

    public List<TokenBlacklist> getGlobalTokenBlackList() {
        // 获取线程安全的 List
        return globalTokenBlackList;
    }


}
