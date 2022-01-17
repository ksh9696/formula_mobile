package com.example.inzent.service;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class Member implements UserDetails {
    private static final long serialVersionUID =1L;

    private String id;
    private String password;
    private String seq;
    private String name;
    private String userRole;
    private String passwordLock;
    private String email;

    /*UserDetails 기본 상속 변수*/
    private Collection<? extends GrantedAuthority> authorities;
    private boolean isEnabled = true;
    private String username;
    private boolean isCredentialsNonExpired = true;
    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
}
