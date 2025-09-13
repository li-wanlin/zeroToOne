package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.SeepageInfo;
import com.stg.mapper.SeepageInfoMapper;
import com.stg.service.SeepageInfoService;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.seepageVo.SeepageUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeepageInfoServiceImpl extends ServiceImpl<SeepageInfoMapper, SeepageInfo> implements SeepageInfoService {

    @Resource
    SeepageInfoMapper seepageInfoMapper;

    private static final Logger logger = LoggerFactory.getLogger(SeepageInfoServiceImpl.class);



    @Override
    public List<SeepageUnit> getSixHour(String type) {

        try {
            if (type == null){
                return null;
            }

            LocalDateTime now = LocalDateTime.now();
            int five = (now.getMinute() / 5) * 5;
            LocalDateTime last = now.withMinute(five).withSecond(0).withNano(0);
            LocalDateTime initial = last.minusHours(6);
            QueryWrapper<SeepageInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",initial,now);
            queryWrapper.orderByAsc("update_time");
            List<SeepageInfo> seepageInfos = seepageInfoMapper.selectList(queryWrapper);
            if (seepageInfos == null || seepageInfos.size() == 0){
                return null;
            }

            List<SeepageUnit> units = new ArrayList<>();

            //处理前6小时的数据
            for (int i = 0; i < 73; i++) {
                SeepageUnit unit = new SeepageUnit();
                LocalDateTime start = initial.plusMinutes(i * 5);
                LocalDateTime end = initial.plusMinutes((i + 1) * 5);
                List<SeepageInfo> seeList = seepageInfos.stream()
                        .filter(seepageInfo -> seepageInfo.getUpdateTime().after(DateLocalUtils.parseTimeToDate(start)))
                        .filter(seepageInfo -> seepageInfo.getUpdateTime().before(DateLocalUtils.parseTimeToDate(end)))
                        .collect(Collectors.toList());
                if (seeList.size() > 0){
                    if (type.equals("1")){
                        unit.setInputTime(DateLocalUtils.parseTimeToStr(start));
                        unit.setValue(seeList.get(0).getLv());
                    }else if (type.equals("2")){
                        unit.setInputTime(DateLocalUtils.parseTimeToStr(start));
                        unit.setValue(seeList.get(0).getLv2());
                    }else {
                        unit.setInputTime(DateLocalUtils.parseTimeToStr(start));
                        unit.setValue(seeList.get(0).getLv3());
                    }
                }else {
                    unit.setInputTime(DateLocalUtils.parseTimeToStr(start));
                    unit.setValue(null);
                }
                units.add(unit);
            }

            return units;


        }catch (Exception e){
            logger.error("",e);
        }

        return null;
    }


}
