package com.EC6.Convive.Config;

import com.EC6.Convive.Security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;

    @Value("${app.remember-me.key:chave-secreta-unica-do-projeto-convive}")
    private String rememberMeKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers("/", "/login", "/features", "/about", "/contact",
                                    "/privacy", "/terms",
                                    "/forgot-password", "/reset-password").permitAll()
                            .requestMatchers("/css/**", "/js/**", "/img/**").permitAll();

                    if (h2ConsoleEnabled) {
                        authorize.requestMatchers("/h2-console/**").permitAll();
                    }

                    authorize
                            .requestMatchers("/morador/**").hasAnyRole("MORADOR", "MODERADOR")
                            .requestMatchers("/moderador/**").hasRole("MODERADOR")

                            .anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler())
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key(rememberMeKey)
                        .userDetailsService(userDetailsService)
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> {
                    if (h2ConsoleEnabled) {
                        csrf.ignoringRequestMatchers("/h2-console/**");
                    }
                })
                .headers(headers -> {
                    if (h2ConsoleEnabled) {
                        // O console do H2 roda em um iframe; so relaxamos a protecao de clickjacking
                        // quando ele esta habilitado (nunca em producao).
                        headers.frameOptions(frame -> frame.disable());
                    }
                });

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Criptografia forte para senhas
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();
            String redirectUrl = "/";

            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MORADOR"))) {
                redirectUrl = "/morador/home";
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MODERADOR"))) {
                redirectUrl = "/moderador/dashboard";
            }

            response.sendRedirect(redirectUrl);
        };
    }
}