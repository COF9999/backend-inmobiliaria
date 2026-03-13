package com.realstate.habitar.infraestructure.rest.auth;

import com.realstate.habitar.domain.dtos.user.AuthUser;
import com.realstate.habitar.infraestructure.classes.custom.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/verify")
    public ResponseEntity<?> verifySession(Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(customUserDetails.getAuthUser());
    }
}
