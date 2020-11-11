package com.github.taoroot.common.security.social;


import com.github.taoroot.common.core.vo.AuthUserInfo;

import java.util.Map;

public abstract class AbstractSocialUserHandler implements SocialUserHandler {

    @Override
    public Boolean checkParams(String code, String redirectUri) {
        return true;
    }

    @Override
    public AuthUserInfo login(String code, String redirectUri) {
        SocialUser socialUser = getSocialUser(code, redirectUri);
        if (socialUser == null) return null;

        return loadAuthUserInfo(socialUser);
    }

    @Override
    public String bind(String code, String redirectUri) {
        SocialUser socialUser = getSocialUser(code, redirectUri);
        if (socialUser == null) return null;

        return bindAuthUserInfo(socialUser);
    }

    private SocialUser getSocialUser(String code, String redirectUri) {
        if (!checkParams(code, redirectUri)) {
            return null;
        }

        Map<String, Object> token = getToken(code, redirectUri);

        if (token == null) {
            return null;
        }

        return loadSocialUser(token);
    }
}
