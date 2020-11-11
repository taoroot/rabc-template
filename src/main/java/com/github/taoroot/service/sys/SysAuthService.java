package com.github.taoroot.service.sys;


import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.core.vo.AuthUserInfo;

public interface SysAuthService {
    R<Object> userInfo();

    AuthUserInfo authByUsername(String username);

    R<String> updatePass(String oldPass, String newPass);
}
