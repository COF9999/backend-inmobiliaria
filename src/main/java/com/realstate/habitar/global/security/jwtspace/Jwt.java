package com.realstate.habitar.global.security.jwtspace;


import com.realstate.habitar.global.security.constants.ConstantsSecurity;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class Jwt {

    private final ConstantsSecurity constantsSecurity;

    public Jwt(ConstantsSecurity constantsSecurity){
        this.constantsSecurity = constantsSecurity;
    };

    public SecretKey getKey(){
        return constantsSecurity.getSECRET_KEY();
    }


    

}
