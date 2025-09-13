package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.FloodEvolute;

public interface FloodEvoluteService extends IService<FloodEvolute> {

    FloodEvolute selectFloodEvolute();

}
