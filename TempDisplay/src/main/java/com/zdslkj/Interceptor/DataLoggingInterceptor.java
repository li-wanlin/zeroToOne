package com.zdslkj.Interceptor;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataLoggingInterceptor implements InnerInterceptor {

    private static final Logger logger =  LoggerFactory.getLogger(DataLoggingInterceptor.class);

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        // 在这里打印 SQL 和参数等信息
        //System.out.println("SQL: " + boundSql.getSql());
        //System.out.println("Parameters: " + parameter);

        logger.info("SQL:{}", boundSql.getSql());
        logger.info("Parameters: {}",JSON.toJSONString(parameter));
    }

/*    @Override
    public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        // 这里可以尝试获取查询结果，但此时可能还未完全映射到实体
        try {
            List<Object> resultList = executor.query(ms, parameter, rowBounds, resultHandler);
            logger.info("Unmapped query results:: {}",JSON.toJSONString(resultList));
            //System.out.println("Unmapped query results: " + resultList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }*/

    public Object plugin(Object target) {
        return target;
    }
}
