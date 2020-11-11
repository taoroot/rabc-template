package com.github.taoroot.common.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;

/**
 * username 存 userId
 */
@Getter
@Setter
public class AuthUser extends User {
    // 昵称
    private String nickname;

    private Integer deptId = -1;

    private List<Integer> roleIds = new ArrayList<>();

    // 额外描述
    private Map<String, Object> attrs = new HashMap<>();

    public AuthUser(String username, String password, boolean enabled, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, true, authorities);
    }

    public AuthUser(String username, Collection<? extends GrantedAuthority> authorities) {
        super(username, "N/A", true, true, true, true, authorities);
    }
}
