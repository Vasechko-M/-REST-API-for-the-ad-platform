//package ru.skypro.homework.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//public class SecurityConfig {
//
//    // Массив маршрутов, которые доступны всем без авторизации
//    private static final String[] AUTH_WHITELIST = {
//            "/public/**",
//            "/auth/**",
//            // добавьте сюда все маршруты, которые должны быть публичными
//    };
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // отключаем CSRF, если API
//                .csrf().disable()
//                // настройка доступа к URL
//                .authorizeHttpRequests(auth -> auth
//                        // публичные маршруты
//                        .requestMatchers(AUTH_WHITELIST).permitAll()
//                        // защитные маршруты по ролям
//                        .requestMatchers("/ads/**").hasRole("USER")
//                        .requestMatchers("/users/**").hasRole("ADMIN")
//                        // остальные маршруты требуют аутентификации
//                        .anyRequest().authenticated()
//                )
//                // включить Basic аутентификацию
//                .httpBasic(withDefaults());
//
//        return http.build();
//    }
//}
