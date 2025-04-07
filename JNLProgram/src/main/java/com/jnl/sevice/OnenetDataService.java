package com.jnl.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.OnenetData;
import org.springframework.scheduling.annotation.Async;

public interface OnenetDataService extends IService<OnenetData> {

    @Async("asyncExecutor")
    void parseMessage(String msg);

}
