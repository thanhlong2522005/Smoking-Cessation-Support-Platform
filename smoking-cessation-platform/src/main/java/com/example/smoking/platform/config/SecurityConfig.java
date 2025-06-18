package com.example.smoking.platform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomSuccessHandler customSuccessHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Cho phép truy cập các URL này mà không cần xác thực
                .requestMatchers("/", "/register", "/css/**", "/js/**", "/images/**", "/api/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/rate").hasAnyRole("MEMBER", "COACH")
                // Yêu cầu xác thực cho tất cả các request khác
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // Chỉ định trang đăng nhập tùy chỉnh
                .successHandler(customSuccessHandler) // URL sau khi đăng nhập thành công
                .permitAll() // Cho phép tất cả mọi người truy cập trang đăng nhập
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // URL để đăng xuất
                .logoutSuccessUrl("/") // URL sau khi đăng xuất thành công
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // Tạm thời tắt CSRF để đơn giản hóa việc test (KHÔNG KHUYẾN KHÍCH TRONG PRODUCTION)
                                          // Chúng ta sẽ bật lại và xử lý sau.
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng BCryptPasswordEncoder để mã hóa mật khẩu
        return new BCryptPasswordEncoder();
    }

}