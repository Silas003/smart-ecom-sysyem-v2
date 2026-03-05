package com.amalitech.demo.config;

import com.amalitech.demo.security.*;
import com.amalitech.demo.services.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomOidcUserService customOidcUserService;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final AuthenticationFailureHandler oauth2AuthenticationFailureHandler;

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomOidcUserService customOidcUserService,
                          OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                          AuthenticationFailureHandler oauth2AuthenticationFailureHandler,
                          RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                          RestAccessDeniedHandler restAccessDeniedHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customOidcUserService = customOidcUserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oauth2AuthenticationFailureHandler = oauth2AuthenticationFailureHandler;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors(cors ->
                        cors.configurationSource(corsConfigurationSource)).csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**")
                        .permitAll().requestMatchers(HttpMethod.GET, "/api/v1/categories/**")
                        .permitAll().requestMatchers(HttpMethod.GET, "/api/v1/reviews/**")
                        .permitAll()
                        .requestMatchers("/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api/v1/users/login",
                                "/api/v1/users/logout",
                                "/api/v1/users",
                                "/api/v1/users/refresh",
                                "graphql", "/oauth2/**", "/login/oauth2/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo ->
                                userInfo.oidcUserService(customOidcUserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oauth2AuthenticationFailureHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        try {
            return configuration.getAuthenticationManager();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService customUserService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(customUserService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

}

