package com.app.quiz.config;

import com.app.quiz.service.QuizUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final QuizUserDetailsService userDetailsService;

    public WebSecurityConfig(QuizUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/register", "/login").permitAll() // Allow access to registration and login pages
                .requestMatchers("/QuizList").hasRole("ADMIN") // Restrict /admin to users with the ADMIN role
                .requestMatchers("/Quiz").hasRole("USER") // Restrict /user to users with the VIEWER role
                .anyRequest().authenticated() // Require authentication for all other endpoints
            )
            .formLogin(form -> form
                .loginPage("/login") // Custom login page
                .defaultSuccessUrl("/home", true) // Redirect to /greet after successful login
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(userDetailsService) // Use your custom UserDetailsService
            .passwordEncoder(passwordEncoder()); // Use the password encoder
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }	
}
