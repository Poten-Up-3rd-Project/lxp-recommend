//package com.lxp.auth.infrastructure.security.adapter;
//
//import com.lxp.auth.application.local.handler.UserInfoSearchPortHandler;
//import com.lxp.auth.application.local.port.required.command.HandleUserInfoCommand;
//import com.lxp.auth.infrastructure.security.model.CustomUserDetails;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Collection;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserInfoSearchPortHandler userInfoSearchPortHandler;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        HandleUserInfoCommand userInfoByEmail = userInfoSearchPortHandler.getUserInfoByEmail(username);
//
//        Collection<? extends GrantedAuthority> authorities = List.of(
//            new SimpleGrantedAuthority(userInfoByEmail.role())
//        );
//
//        return new CustomUserDetails(
//            userInfoByEmail.userId(),
//            userInfoByEmail.email(),
//            userInfoByEmail.email(),
//            "",
//            authorities
//        );
//    }
//}
