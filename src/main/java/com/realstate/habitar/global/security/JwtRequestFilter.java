package com.realstate.habitar.global.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.realstate.habitar.domain.dtos.token.TokenInfo;
import com.realstate.habitar.global.security.constants.ConstantsSecurity;
import com.realstate.habitar.global.security.specialObjects.SimpleGrantedAuthoritiesCreator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import java.io.IOException;
import java.util.*;

public class JwtRequestFilter extends BasicAuthenticationFilter {

    private final ConstantsSecurity constantsSecurity;

    public JwtRequestFilter(AuthenticationManager authenticationManager, ConstantsSecurity constantsSecurity) {
        super(authenticationManager);
        this.constantsSecurity = constantsSecurity;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(ConstantsSecurity.HEADER_AUTHORIZATION);

        if(header == null || !header.startsWith(ConstantsSecurity.PREFIX_TOKEN)){
            chain.doFilter(request,response);
            return;
        }

        String token = header.replace(ConstantsSecurity.PREFIX_TOKEN,"");

        try {

            Claims claims = Jwts.parser()
                    .verifyWith(constantsSecurity.getSECRET_KEY())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();


            System.out.println("PASO FIRMA TOKEN");

            String username = claims.getSubject();
            Object authoritiesClaims = claims.get("authorities");


            Collection<? extends GrantedAuthority> authrorities = Arrays.asList(new ObjectMapper().
                    addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthoritiesCreator.class).
                    readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username,
                    null,authrorities);

            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("BEFORE CHAIN DO FILTER");
            TokenInfo tokenInfo = new TokenInfo(username,claims.getIssuedAt(),claims.getExpiration());
            request.setAttribute("tokenInfo",tokenInfo);
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
