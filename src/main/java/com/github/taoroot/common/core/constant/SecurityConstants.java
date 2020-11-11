package com.github.taoroot.common.core.constant;

public interface SecurityConstants {
    /**
     * 标志
     */
    String INTERNAL_AUTHORIZATION = "Internal_Authorization";

    /**
     * 实时查询用户接口
     */
    String REAL_TIME_USER_DETAILS_SERVICE = "realTimeUserDetailsService";


    /**
     * {"userAuthPath":"http://mall-v1-api-admin/auth/username", "socialAuthPath": "http://mall-v1-api-admin/auth/social", "socialAuthListPath": "http://mall-v1-api-admin/auth/socials", "jwt": "mall-v1-api-admin-jwt-secret"}
     */
    String AUTH_USER_PATH = "userAuthPath";

    String AUTH_SOCIAL_PATH = "socialAuthPath";

    String AUTH_SOCIAL_LIST_PATH = "socialAuthListPath";

    String USER_ID = "user_id";
    String ROLE_IDS = "role_ids";
    String DEPT_ID = "dept_id";
    String TENANT_ID = "tenant_id";
    String NICKNAME = "nickname";

    String IMAGE_KEY_PARAM = "imageKey";
    String IMAGE_VALUE_PARAM = "imageCode";

    String OAUTH2_PROXY_ORIGIN_PARAM = "proxy_origin";

    /**
     * 授权码模式code key 前缀
     */
    String OAUTH_CODE_PREFIX = "oauth:code:";
}
