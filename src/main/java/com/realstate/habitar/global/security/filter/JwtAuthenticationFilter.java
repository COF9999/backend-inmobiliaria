package com.realstate.habitar.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realstate.habitar.global.security.constants.ConstantsSecurity;
import com.realstate.habitar.global.security.jwtspace.Jwt;
import com.realstate.habitar.infraestructure.classes.custom.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import com.realstate.habitar.infraestructure.classes.model.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/login");

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        User user = null;
        String email = null;
        String password = null;

        try {
            user = new ObjectMapper().readValue(request.getInputStream(),User.class);
            email = user.getEmail();
            password = user.getPassword();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email,password
        );


        return authenticationManager.authenticate(authenticationToken);
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

         CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

        String email = customUserDetails.getUsername();// Here is the email
        String validUserName = customUserDetails.getAuthUser().username();

       List<String> rolesList = authResult.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Claims claims = Jwts.claims()
                .add("claims",rolesList)
                .add("username",validUserName)
                .build();

        long expirationTime = 31557600000L;

        String token = Jwts.
                builder().
                subject(email).
                claims(claims).
                expiration(new Date(System.currentTimeMillis()+expirationTime)).
                issuedAt(new Date()).
                signWith(Jwt.getSecretKey())
                .compact();

        ResponseCookie jwtCookie = ResponseCookie.from("p_token",token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(expirationTime / 1000)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());


        Map<String,String> body = new HashMap<>();


        body.put("username",validUserName);

        response.getWriter()
                .write(new ObjectMapper().writeValueAsString(body));


        response.setContentType(ConstantsSecurity.CONTENT_TYPE_APPLICATION_JSON);

        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        Map<String,String> body = new HashMap<>();
        body.put("message","Error en la autenticación username o password incorrectos");
        body.put("error",failed.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(ConstantsSecurity.CONTENT_TYPE_APPLICATION_JSON);
    }
}
