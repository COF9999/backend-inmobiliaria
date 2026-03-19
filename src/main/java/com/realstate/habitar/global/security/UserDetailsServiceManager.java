package com.realstate.habitar.global.security;


import com.realstate.habitar.domain.dtos.user.AuthUser;
import com.realstate.habitar.domain.ports.user.UserDaoPort;
import com.realstate.habitar.infraestructure.classes.model.User;
import com.realstate.habitar.infraestructure.classes.custom.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceManager implements UserDetailsService {

    private final UserDaoPort userPersistencePort;

    public UserDetailsServiceManager(UserDaoPort userPersistencePort){
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userPersistencePort
                .findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(String.format("email - %s -passed not exists in the DATABASE",email)));


        List<GrantedAuthority> grantedAuthorityList = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new CustomUserDetails(user.getEmail(),
                user.getPassword(),
                user.getIsActive(),
                true,
                true,
                true,
                grantedAuthorityList,
                new AuthUser(user.getUsername()));
    }
}
