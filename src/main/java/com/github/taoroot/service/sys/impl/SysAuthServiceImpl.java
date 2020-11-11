package com.github.taoroot.service.sys.impl;

import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.core.utils.TreeUtils;
import com.github.taoroot.common.core.vo.AuthUserInfo;
import com.github.taoroot.common.security.SecurityUtils;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysMenu;
import com.github.taoroot.entity.sys.SysUser;
import com.github.taoroot.mapper.sys.*;
import com.github.taoroot.service.sys.SysAuthService;
import com.github.taoroot.service.sys.SysUserRoleService;
import com.github.taoroot.service.sys.SysUserService;
import com.github.taoroot.service.sys.SysUserSocialService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
@Service
public class SysAuthServiceImpl implements SysAuthService {

    private final SysUserMapper sysUserMapper;
    private final SysUserService sysUserService;
    private final SysUserRoleService sysUserRoleService;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysSocialDetailsMapper sysSocialDetailsMapper;
    private final SysUserSocialService sysUserSocialService;
    private final SysUserSocialMapper sysUserSocialMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public R<Object> userInfo() {
        Integer userId = SecurityUtils.userId();
        HashMap<String, Object> result = new HashMap<>();
        SysUser sysUser = sysUserMapper.selectById(SecurityUtils.userId());
        // 查询用户个人信息
        result.put("info", sysUser);
        // 查询用户角色信息
        result.put("roles", sysUserMapper.roles(userId));
        // 所属部门
        result.put("dept", sysDeptMapper.selectById(sysUser.getDeptId()).getName());
        // 前端功能
        List<String> functions = sysUserMapper.menus(userId, SysMenu.FUNCTION).stream()
                .map(SysMenu::getAuthority).collect(Collectors.toList());
        result.put("functions", functions);
        // 前端菜单
        List<SysMenu> menus = sysUserMapper.menus(userId, SysMenu.MENU);
        result.put("menus", TreeUtil.build(menus, TreeUtils.ROOT_PARENT_ID, (treeNode, tree) -> {
            tree.setId(treeNode.getId());
            tree.setParentId(treeNode.getParentId());
            tree.setWeight(treeNode.getWeight());
            tree.setName(treeNode.getName());
            tree.putExtra("path", treeNode.getPath());
            tree.putExtra("hidden", treeNode.getHidden());
            tree.putExtra("alwaysShow", treeNode.getAlwaysShow());
            tree.putExtra("redirect", treeNode.getRedirect());
            tree.putExtra("type", treeNode.getType());
            tree.put("component", treeNode.getComponent());
            HashMap<String, Object> meta = new HashMap<>();
            meta.put("title", treeNode.getTitle());
            meta.put("icon", treeNode.getIcon());
            meta.put("breadcrumb", treeNode.getBreadcrumb());
            meta.put("noCache", treeNode.getNoCache());
            tree.putExtra("meta", meta);
        }));
        // 社交账号
        result.put("socials", sysUserMapper.socials(userId));
        return R.ok(result);
    }

    @Override
    public AuthUserInfo authByUsername(String username) {
        SysUser sysUser = sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));

        if (sysUser == null) {
            return null;
        }

        Integer userId = sysUser.getId();
        AuthUserInfo userInfo = new AuthUserInfo();
        userInfo.setUsername(String.valueOf(sysUser.getId()));
        userInfo.setPassword(sysUser.getPassword());
        userInfo.setNickname(sysUser.getNickname());
        userInfo.setDeptId(sysUser.getDeptId());
        userInfo.setRoleIds(sysUserMapper.roleIds(userId));
        return userInfo;
    }


    @Override
    public R<String> updatePass(String oldPass, String newPass) {
        SysUser user = sysUserService.getById(SecurityUtils.userId());
        String password = user.getPassword();
        boolean matches = passwordEncoder.matches(oldPass, password);
        Assert.isTrue(matches, "密码错误");
        user.setPassword(passwordEncoder.encode(newPass));
        Assert.isTrue(sysUserService.updateById(user), "未知原因,更新失败");
        return R.okMsg("更新成功");
    }
}
