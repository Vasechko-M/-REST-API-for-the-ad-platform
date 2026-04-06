package ru.skypro.homework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    // Массив маршрутов, которые доступны всем без авторизации
    private static final String[] AUTH_WHITELIST = {
            "/public/**",
            "/auth/**",
            "/users/**",
            "/ads/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                // публичные маршруты
                .antMatchers(AUTH_WHITELIST).permitAll()
                // маршруты по ролям
                .antMatchers("/ads/**").hasRole("USER")
                .antMatchers("/users/**").hasRole("ADMIN")
                // остальные требуют аутентификации
                .anyRequest().authenticated()
                .and()
                .httpBasic();

        return http.build();
    }
}