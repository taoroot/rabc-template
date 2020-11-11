package com.github.taoroot.common.security.social;

import lombok.Data;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.util.Map;

@Data
public class SocialUser implements AuthenticatedPrincipal {

    private String username;

    private String nickname;

    private String avatar;

    private String city;

    private Map<String, Object> attrs;

    @Override
    public String getName() {
        return username;
    }
}
