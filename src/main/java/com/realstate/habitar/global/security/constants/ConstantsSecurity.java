package com.realstate.habitar.global.security.constants;

import com.realstate.habitar.global.security.jwtspace.Jwt;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class ConstantsSecurity {

    @Value("${config.secret.key}")
    private String secretKey;

    private SecretKey SECRET_KEY;

    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

    public SecretKey getSECRET_KEY(){
        return SECRET_KEY;
    }

    @PostConstruct
    public void init() {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretKey.getBytes());
        Jwt.setSecretKey(this.SECRET_KEY);
    }
}
