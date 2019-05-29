package com.cjq.springbootblog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 安全配置类
 */
@EnableWebSecurity  //启动此配置
@EnableGlobalMethodSecurity(prePostEnabled = true)  //启用方法级别的安全设置
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String KEY = "cjq";

    @Qualifier("userServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        //设置密码的加密方式
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    /**
     * 权限设置
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/fonts/**", "/index").permitAll()  //静态资源文件和主页都可以访问
                .antMatchers("/h2-console/**").permitAll()  //允许所有的H2数据库控制台
                .antMatchers("/admins/**").hasRole("ADMIN")
                .and()
                .formLogin()  //基于form表单的登录
                .loginPage("/login").failureUrl("/login-error")
                .and().rememberMe().key(KEY)
                .and().exceptionHandling().accessDeniedPage("/403"); //异常处理，拒绝访问就重定位到403页面
        http.csrf().ignoringAntMatchers("/h2-console/**");  //禁用h2控制台的CSRF防护
        http.headers().frameOptions().sameOrigin(); //允许统一来源的H2控制台请求
    }

    /**
     * 认证设置
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService); //使用数据库中的用户
        auth.authenticationProvider(authenticationProvider);
    }
}
