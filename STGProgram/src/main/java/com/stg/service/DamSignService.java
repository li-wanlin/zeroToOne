package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.DamSign;
import com.stg.vo.damSignVo.SignUnit;

public interface DamSignService extends IService<DamSign> {

    Boolean insertByInfo(SignUnit unit);

    Boolean deleteByInfo(SignUnit unit);

    Boolean updateByInfo(SignUnit unit);

    SignUnit selectEnable();

}
