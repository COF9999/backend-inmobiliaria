package com.realstate.habitar.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realstate.habitar.global.security.constants.ConstantsSecurity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import com.realstate.habitar.global.infraestructure.models.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ConstantsSecurity constantsSecurity;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,ConstantsSecurity constantsSecurity) {
        this.authenticationManager = authenticationManager;
        this.constantsSecurity = constantsSecurity;
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

            System.out.println(email);

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

        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult.getPrincipal();

        String email = user.getUsername(); // Here is the email
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();

        Claims claims = Jwts.claims()
                .add("authorities",new ObjectMapper().writeValueAsString(roles))
                .build();

        String token = Jwts.
                builder().
                subject(email).
                claims(claims).
                expiration(new Date(System.currentTimeMillis()+31557600000L)).
                issuedAt(new Date()).
                signWith(constantsSecurity.getSECRET_KEY())
                .compact();

        response.addHeader(ConstantsSecurity.HEADER_AUTHORIZATION, ConstantsSecurity.PREFIX_TOKEN+token);

        Map<String,String> body = new HashMap<>();
        body.put("token",token);
        body.put("message",String.format("Hola has iniciado sesión con exitoso usuario con el email %s",email));

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
