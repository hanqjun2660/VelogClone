package com.blog.velogclone.config;

import com.blog.velogclone.handler.AuthenticationAccessDeniedHandler;
import com.blog.velogclone.service.PrincipalOauthUserService;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private PrincipalOauthUserService principalOauthUserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().disable()
                .authorizeHttpRequests(request -> request
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .requestMatchers("/USER/**", "/board/write").authenticated()         // 인증이 필요한 요청
                        .requestMatchers("/ADMIN/**").hasAuthority("ADMIN")                    // 해당 요청은 해당 권한만 가진 인증된 사용자만
                        .anyRequest().permitAll()                                                // 그 외의 요청은 모두에게 허용한다.
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/member/login")
                        .usernameParameter("userId")
                        .passwordParameter("pwd")
                        .defaultSuccessUrl("/dashboard")
                )
                .oauth2Login(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard")
                        .userInfoEndpoint()
                        .userService(principalOauthUserService)             // oAuth 로그인 처리 서비스
                )
                .logout(withDefaults())     // Default로 하면 "/logout"
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler);     // 인가 관련 핸들러

        return http.build();
    }
}
