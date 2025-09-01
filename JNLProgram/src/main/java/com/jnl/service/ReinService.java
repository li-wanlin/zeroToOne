package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.Reinforcement;
import com.jnl.vo.reinVo.ReinResponse;
import com.jnl.vo.reinVo.ReinUnit;
import org.springframework.web.multipart.MultipartFile;

public interface ReinService extends IService<Reinforcement> {

    Boolean insertByInfo(ReinUnit unit, MultipartFile file);

    Boolean deleteByInfo(ReinUnit unit);

    Boolean updateByInfo(ReinUnit unit, MultipartFile file);

    ReinResponse pagedQuery(Integer pageNum,Integer pageSize);

    byte[] previewById(Integer id, long startByte);


}
