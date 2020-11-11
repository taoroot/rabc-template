package com.github.taoroot.common.security.social;

public interface SocialType {
    String SMS = "SMS";
    String QQ = "QQ";
    String GITHUB = "GITHUB";
    String WX_MP = "WX_MP";
    String WX_OPEN = "WX_OPEN";
    String WX_OAUTH2_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    String WX_OAUTH2_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=%s";
    String GITEE = "GITEE";
    String GITEE_ACCESS_TOKEN_URL = "https://gitee.com/oauth/token?grant_type=authorization_code&code=%S&client_id=%s&redirect_uri=%s&client_secret=%s";
    String ZHIYI = "ZHIYI_CLOUD";
    String ZHIYI_ACCESS_TOKEN_URL = "http://zhiyi-authorization/oauth/token?client_id=%s&client_secret=%s&code=%s&redirect_uri=%s&grant_type=authorization_code";
}
