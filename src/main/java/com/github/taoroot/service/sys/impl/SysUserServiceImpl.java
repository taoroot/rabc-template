package com.github.taoroot.service.sys.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysUser;
import com.github.taoroot.entity.sys.SysUserRole;
import com.github.taoroot.mapper.sys.SysUserMapper;
import com.github.taoroot.mapper.sys.SysUserRoleMapper;
import com.github.taoroot.service.sys.SysUserRoleService;
import com.github.taoroot.service.sys.SysUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@DS(DBName.SYS)
@Service
@AllArgsConstructor
public class SysUserServiceImpl extends CustomServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleService sysUserRoleService;
    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    public R getPage(Page<SysUser> page, String username, String phone, Integer deptId, Boolean enabled) {
        IPage<SysUser> result = sysUserMapper.getPage(page, username, phone, deptId, enabled);
        return R.ok(result);
    }

    @Override
    public R saveOrUpdateItem(SysUser sysUser) {
        // 更新用户信息
        sysUser.updateById();

        // 更新角色信息
        if (sysUser.getRoles() != null) {
            List<SysUserRole> roleMenuList = Arrays.stream(sysUser.getRoles()).map(userId -> {
                SysUserRole userRole = new SysUserRole();
                userRole.setRoleId(userId);
                userRole.setUserId(sysUser.getId());
                return userRole;
            }).collect(Collectors.toList());
            sysUserRoleMapper.delete(Wrappers.<SysUserRole>query().lambda()
                    .eq(SysUserRole::getUserId, sysUser.getId()));
            sysUserRoleService.saveBatch(roleMenuList);
        }

        return R.ok();
    }

}
