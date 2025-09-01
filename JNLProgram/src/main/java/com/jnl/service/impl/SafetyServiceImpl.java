package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.Safety;
import com.jnl.mapper.SafetyMapper;
import com.jnl.service.SafetyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SafetyServiceImpl extends ServiceImpl<SafetyMapper, Safety> implements SafetyService {


    @Resource
    SafetyMapper safetyMapper;

    private static final Logger logger = LoggerFactory.getLogger(SafetyServiceImpl.class);




    @Override
    public List<Safety> selectAllSort() {
        try{
            QueryWrapper<Safety> wrapper = new QueryWrapper<>();

            wrapper.orderByAsc("id");

            List<Safety> safetyList = safetyMapper.selectList(wrapper);
            if (safetyList == null || safetyList.size() == 0){
                return null;
            }

            List<Safety> safeties = new ArrayList<>();
            String typeOne = "安全责任人";
            String typeTwo = "防汛责任人";

            String typeOnePsOne = "政府责任人";
            String typeOnePsTwo = "主管部门责任人";
            String typeOnePsThree = "管理单位责任人";
            String typeTwoPsOne = "主管部门责任人";
            String typeTwoPsTwo = "管理单位责任人";
            String typeTwoPsThree = "政府责任人";

            Safety one = null;
            Safety two = null;
            Safety three = null;
            Safety four = null;
            Safety five = null;
            Safety six = null;

            List<Safety> oneList = safetyList.stream()
                    .filter(safety -> typeOne.equals(safety.getSafetyType()))
                    .filter(safety -> typeOnePsOne.equals(safety.getPosition()))
                    .collect(Collectors.toList());
            if (oneList.size() > 0){
                one = oneList.get(0);
            }

            List<Safety> twoList = safetyList.stream()
                    .filter(safety -> typeOne.equals(safety.getSafetyType()))
                    .filter(safety -> typeOnePsTwo.equals(safety.getPosition()))
                    .collect(Collectors.toList());

            if (twoList.size() > 0){
                two = twoList.get(0);
            }


            List<Safety> threeList = safetyList.stream()
                    .filter(safety -> typeOne.equals(safety.getSafetyType()))
                    .filter(safety -> typeOnePsThree.equals(safety.getPosition()))
                    .collect(Collectors.toList());

            if (threeList.size() > 0){
                three = threeList.get(0);
            }

            List<Safety> fourList = safetyList.stream()
                    .filter(safety -> typeTwo.equals(safety.getSafetyType()))
                    .filter(safety -> typeTwoPsOne.equals(safety.getPosition()))
                    .collect(Collectors.toList());
            if (fourList.size() > 0){
                four = fourList.get(0);
            }


            List<Safety> fiveList = safetyList.stream()
                    .filter(safety -> typeTwo.equals(safety.getSafetyType()))
                    .filter(safety -> typeTwoPsTwo.equals(safety.getPosition()))
                    .collect(Collectors.toList());
            if (fiveList.size() > 0){
                five = fiveList.get(0);
            }

            List<Safety> sixList = safetyList.stream()
                    .filter(safety -> typeTwo.equals(safety.getSafetyType()))
                    .filter(safety -> typeTwoPsThree.equals(safety.getPosition()))
                    .collect(Collectors.toList());
            if (sixList.size() > 0){
                six = sixList.get(0);
            }


            safeties.add(one);
            safeties.add(two);
            safeties.add(three);
            safeties.add(six);
            safeties.add(four);
            safeties.add(five);


            return safeties;
        }catch (Exception e){
            logger.error("获取安全责任人列表时出错",e);
        }
        return null;
    }

    @Override
    public Boolean updateByList(List<Safety> safeties) {
        try {
            if (safeties == null){
                return false;
            }

            return updateBatchById(safeties);
        }catch (Exception e){
            logger.error("更新安全负责人发生异常",e);
        }
        return false;
    }


}
