package com.example.boardProject.config;

import com.example.boardProject.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    MemberService memberService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http 요청에 대한 보안을 설정. 페이지권한설정,로그인페이지설정,로그아웃 메소드 등에 대한 설정을 작성한다.
        http
                .authorizeHttpRequests((request)-> request
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/main", "/members/loginForm","/members/new").permitAll()
                        //.requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/item/**").permitAll()
                        .anyRequest().authenticated())

                .formLogin((form)->form
                        .loginProcessingUrl("/login")
                        .loginPage("/members/loginForm")
                        .failureUrl("/members/login/error")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/main"))
                .logout((out)->out.logoutSuccessUrl("/main")
                        .logoutUrl("/members/logout"))
                        //.logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
        // 비밀번호를 데이터베이스에 그대로 저장했을경우, 해킹당하면 고객회원정보가 그대로 노출됨
        // BC어쩌고 해시함수를 이용하여 비밀번호를 암호화하여 저장한다.
    }

}
