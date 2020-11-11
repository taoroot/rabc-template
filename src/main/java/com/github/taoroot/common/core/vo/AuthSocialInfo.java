package com.github.taoroot.common.core.vo;

import lombok.Data;

@Data
public class AuthSocialInfo {
    private String type;
    private String title;
    private String authorizeUri;
    private String icon;
}
