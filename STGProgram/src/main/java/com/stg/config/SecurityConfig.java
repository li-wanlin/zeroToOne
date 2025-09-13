package com.stg.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity // 启用 Spring Security
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers( "/STGProgram/index.html", "/STGProgram/login", "/STGProgram/**","/STGProgram/display/**").permitAll() // 放行登录页和静态资源
                .anyRequest().permitAll() // 其他请求放行
                .and()
                .formLogin()
                .loginPage("/STGProgram/login") // 指定登录页路径
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/STGProgram/logout") // 指定登出请求路径
                .permitAll()
                .logoutSuccessUrl("/STGProgram/login?logout") // 登出成功后跳转的页面
                .invalidateHttpSession(true);// 使会话失效
    }
}
