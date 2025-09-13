package com.stg.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.stg.Interceptor.DataLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class MybatisPlusConfig {


    @Bean
    public GlobalConfig globalConfig(){
        GlobalConfig globalConfig = new GlobalConfig();
        //确保DbConfig不为null
        if (globalConfig.getDbConfig() == null){
            globalConfig.setDbConfig(new GlobalConfig.DbConfig());
        }

        //设置数据库表名和列名驼峰命名
        globalConfig.getDbConfig().setTableUnderline(false);
        return globalConfig;
    }


    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSource datasource, GlobalConfig globalConfig) throws IOException {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(datasource);
        factoryBean.setGlobalConfig(globalConfig);

        //添加插件
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new DataLoggingInterceptor());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        factoryBean.setPlugins(interceptor);

        Resource[] mapperLocations = new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/**/*.xml");
        factoryBean.setMapperLocations(mapperLocations);

        return factoryBean;
    }


}
