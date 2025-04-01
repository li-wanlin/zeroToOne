package com.jnl.Interceptor;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class DataLoggingInterceptor implements InnerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(DataLoggingInterceptor.class);

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        logger.info("SQL:{}",boundSql.getSql());
        logger.info("Parameters:{}", JSON.toJSONString(parameter));
    }


    public Object plugin(Object target){
        return target;
    }
}
