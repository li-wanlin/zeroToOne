package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.DamSign;
import com.jnl.vo.damSignVo.SignUnit;

public interface DamSignService extends IService<DamSign> {

    Boolean insertByInfo(SignUnit unit);

    Boolean deleteByInfo(SignUnit unit);

    Boolean updateByInfo(SignUnit unit);

    SignUnit selectEnable();

}
