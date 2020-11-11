package com.github.taoroot.common.security.social;



import com.github.taoroot.common.core.vo.AuthUserInfo;

import java.util.Map;

public interface SocialUserHandler {

    Boolean checkParams(String code, String redirectUri);

    Map<String, Object> getToken(String code, String redirectUri);

    SocialUser loadSocialUser(Map<String, Object> token);

    AuthUserInfo loadAuthUserInfo(SocialUser socialUser);

    String bindAuthUserInfo(SocialUser socialUser);

    AuthUserInfo login(String code, String redirectUri);

    String bind(String code, String redirectUri);
}
