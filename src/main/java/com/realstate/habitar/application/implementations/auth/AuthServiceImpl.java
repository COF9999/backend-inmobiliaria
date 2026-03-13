package com.realstate.habitar.application.implementations.auth;

import com.realstate.habitar.domain.dtos.token.TokenDto;
import com.realstate.habitar.global.security.jwtspace.Jwt;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl {

    public Map<String,String> tokenIsValid(TokenDto tokenDto){
        Claims claims = Jwt.validateToken(tokenDto.token());

        if (claims!=null){
            System.out.println("ENTRO-----");
            String username = (String) claims.get("username");
            Map<String, String> response = new HashMap<>();
            response.put("username", username);
            return response;
        }else {
            return null;
        }
    }
}
