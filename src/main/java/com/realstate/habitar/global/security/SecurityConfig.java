package com.realstate.habitar.global.security;


import com.realstate.habitar.global.security.constants.ConstantsSecurity;
import com.realstate.habitar.global.security.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class SecurityConfig {


    private final AuthenticationManager authenticationManager;

    private final ConstantsSecurity constantsSecurity;

    @Value("${config.my.local.ip}")
    private String myLocalIp;

    public SecurityConfig(AuthenticationManager authenticationManager, ConstantsSecurity constantsSecurity){
        this.authenticationManager = authenticationManager;
        this.constantsSecurity = constantsSecurity;
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((request)->{
                    request.requestMatchers("/login").permitAll();
                            request.requestMatchers("user/**").permitAll()
                    .anyRequest().authenticated();
                })
                .sessionManagement(management-> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(new JwtAuthenticationFilter(authenticationManager,constantsSecurity))
                .addFilter(new JwtRequestFilter(authenticationManager,constantsSecurity));
              /*  .addFilterBefore(
                        new JwtRequestFilter(authenticationManager, constantsSecurity),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilter(
                        new JwtAuthenticationFilter(authenticationManager, constantsSecurity)
                );

               */

        return httpSecurity.build();
    }
/*
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost");
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://localhost:4173");
        config.addAllowedOrigin(String.format("http://%s",myLocalIp));
        config.addAllowedOrigin(String.format("http://%s:5173",myLocalIp));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

 */

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:4173",
                String.format("http://%s", myLocalIp),
                String.format("http://%s:5173", myLocalIp)
        ));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
