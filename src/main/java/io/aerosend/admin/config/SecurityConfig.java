package io.aerosend.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/ip-pools/**").hasAnyRole("OPS", "ADMIN")
                .requestMatchers("/resellers/**").hasRole("ADMIN")
                .anyRequest().hasAnyRole("SUPPORT", "OPS", "ADMIN"))
            .oauth2Login(o -> o.loginPage("/oauth2/authorization/google"));
        return http.build();
    }
}
