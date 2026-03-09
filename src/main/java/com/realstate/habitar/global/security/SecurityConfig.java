package com.realstate.habitar.global.security;


import com.realstate.habitar.global.security.constants.ConstantsSecurity;
import com.realstate.habitar.global.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    private final AuthenticationManager authenticationManager;

    private final ConstantsSecurity constantsSecurity;

    public SecurityConfig(AuthenticationManager authenticationManager, ConstantsSecurity constantsSecurity){
        this.authenticationManager = authenticationManager;
        this.constantsSecurity = constantsSecurity;
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
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
}
