package com.jnl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
@EnableWebSecurity // 启用 Spring Security
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers( "/JNLProgram/index.html", "/JNLProgram/login", "/JNLProgram/**","/JNLProgram/display/**").permitAll() // 放行登录页和静态资源
                .anyRequest().permitAll() // 其他请求放行
                .and()
                .formLogin()
                .loginPage("/JNLProgram/login") // 指定登录页路径
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/JNLProgram/logout") // 指定登出请求路径
                .permitAll()
                .logoutSuccessUrl("/JNLProgram/login?logout") // 登出成功后跳转的页面
                .invalidateHttpSession(true);// 使会话失效
    }




    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();

        // 允许URL中的换行符（谨慎使用，可能存在安全风险）
        firewall.setAllowUrlEncodedLineFeed(true);

        // 如果你还需要允许其他字符，可以在这里配置
        // firewall.setAllowSemicolon(true);
        // firewall.setAllowUrlEncodedSlash(true);
        // firewall.setAllowBackSlash(true);

        return firewall;
    }

}
