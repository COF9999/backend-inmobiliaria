package com.realstate.habitar.global.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.realstate.habitar.global.security.constants.ConstantsSecurity;
import com.realstate.habitar.global.security.jwtspace.Jwt;
import com.realstate.habitar.infraestructure.classes.custom.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import java.io.IOException;
import java.util.*;

public class JwtRequestFilter extends BasicAuthenticationFilter {

    private final UserDetailsService userDetailsService;

    public JwtRequestFilter(AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String token = null;
        if (request.getCookies() !=null){
            for (Cookie cookie: request.getCookies()){
                if ("p_token".equalsIgnoreCase(cookie.getName())){
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null){
            chain.doFilter(request,response);
            return;
        }


        try {

            Claims claims = Jwt.validateToken(token);
            System.out.println("CLAIMS   "+claims);
            String email = claims.getSubject();

            List<String> roles = claims.get("claims", List.class);

            List<SimpleGrantedAuthority> authorities =
                    roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authenticationToken = new
                    UsernamePasswordAuthenticationToken(userDetails,null,authorities);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request,response);

        }catch (JwtException e){
            Map<String,String> body = new HashMap<>();
            body.put("error",e.getMessage());
            body.put("message","The Token is invalid");


            Optional.of(body)
                    .map(this::convertAsStringJson)
                    .ifPresent(data-> insertDataInResponse(data,response));
        }


    }

    public String convertAsStringJson(Map<?,?> map){
        try {
            return new ObjectMapper().writeValueAsString(map);
        }catch (JsonProcessingException e){
            throw new RuntimeException("Serialization of token could not posibble");
        }
    }

    public void insertDataInResponse(String dataStringMap,HttpServletResponse response){
        try {
            response.getWriter().write(dataStringMap);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
