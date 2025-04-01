package com.jnl.vo.functionVo;


import com.jnl.entity.TokenCheck;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class GlobalTokenCheckList {
    private final List<TokenCheck> globalCheckList;


    public GlobalTokenCheckList() {
        // 使用 CopyOnWriteArrayList 创建线程安全的 List
        this.globalCheckList = new CopyOnWriteArrayList<>();
    }

    public void addTokenCheck(TokenCheck tokenCheck) {
        // 向线程安全的 List 中添加元素
        globalCheckList.add(tokenCheck);
    }

    public List<TokenCheck> getGlobalCheckList() {
        // 获取线程安全的 List
        return globalCheckList;
    }


}
