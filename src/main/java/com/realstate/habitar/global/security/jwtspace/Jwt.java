package com.realstate.habitar.global.security.jwtspace;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;


public class Jwt {

    private static SecretKey SECRET_KEY = null;

    public static void setSecretKey( SecretKey SECRET_KEY){
        Jwt.SECRET_KEY = SECRET_KEY;
        System.out.println(SECRET_KEY);
    }
    public static Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static SecretKey getSecretKey(){
        return Jwt.SECRET_KEY;
    }
}
