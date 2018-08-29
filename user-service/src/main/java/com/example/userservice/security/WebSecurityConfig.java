package com.example.userservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource(name = "userService")
    private UserDetailsService userDetailsService;
    
    @Autowired
    private JwtAuthenticationEntryPoint2 unauthorizedHandler;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(encoder());
    }

    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
        		.authorizeRequests()
                	.antMatchers("/token/**","/api/users/register/**","/token/email/**","/token/proba/**")
                	.permitAll()
                	.and()
                .authorizeRequests()
                	.antMatchers(HttpMethod.OPTIONS,"/token/**")
                	.permitAll()
                	.and()
                .authorizeRequests()	
                	.anyRequest()
                	.authenticated()
                	.and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        http
        		.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
         /*
    	 http
         .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class) // Main auth filter
         .sessionManagement()
             .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

     http.cors().and().csrf().disable().authorizeRequests()
         .antMatchers(HttpMethod.POST, "/token/**").permitAll()
         .anyRequest().authenticated();

     http.exceptionHandling()
         .authenticationEntryPoint(unauthorizedHandler);*/
    }
    

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

}
