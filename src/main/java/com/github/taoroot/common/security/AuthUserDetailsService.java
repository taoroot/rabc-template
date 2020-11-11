package com.github.taoroot.common.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 密码登录走 loadUserByUsername
 * JWT登录走 loadUserById
 */
public interface AuthUserDetailsService extends UserDetailsService {

    UserDetails loadUserById(String userId);
}
