package com.github.taoroot.service.component;

import com.github.taoroot.common.core.vo.AuthUserInfo;
import com.github.taoroot.common.security.AuthUserDetailsService;
import com.github.taoroot.common.security.SecurityUtils;
import com.github.taoroot.entity.sys.SysUser;
import com.github.taoroot.mapper.sys.SysUserMapper;
import com.github.taoroot.service.sys.SysAuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component(SysAuthUserService.NAME)
public class SysAuthUserService implements AuthUserDetailsService {
    public static final String NAME = "sys";

    private final SysUserMapper sysUserMapper;
    private final SysAuthService sysAuthService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUserInfo authUserInfo = sysAuthService.authByUsername(username);
        if(authUserInfo == null) {
            throw new UsernameNotFoundException("无记录");
        }
        return SecurityUtils.getAuthUser(authUserInfo);
    }

    @Override
    public UserDetails loadUserById(String userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);

        if (sysUser == null) {
            return null;
        }

        AuthUserInfo userInfo = new AuthUserInfo();
        userInfo.setUsername(userId);
        // 角色
        userInfo.setRoleIds(sysUserMapper.roleIds(sysUser.getId()));
        // 权限
        List<String> list = sysUserMapper.authorityNames(sysUser.getId());
        list.addAll(sysUserMapper.roleNames(sysUser.getId()));
        userInfo.setAuthorities(list.toArray(new String[0]));
        return SecurityUtils.getAuthUser(userInfo);
    }
}
