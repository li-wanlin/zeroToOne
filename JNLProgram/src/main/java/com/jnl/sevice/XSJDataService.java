package com.jnl.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.XSJData;

public interface XSJDataService extends IService<XSJData> {

    XSJData selectDataById(Integer id);

    Boolean updateByGiveId(Integer id);

    Boolean deleteByGiveId(Integer id);

    Boolean insertByGive();

}
