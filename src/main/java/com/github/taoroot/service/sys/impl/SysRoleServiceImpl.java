package com.github.taoroot.service.sys.impl;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.taoroot.common.core.datascope.DataScopeTypeEnum;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.common.security.SecurityUtils;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysRole;
import com.github.taoroot.entity.sys.SysRoleMenu;
import com.github.taoroot.entity.sys.SysUser;
import com.github.taoroot.mapper.sys.SysRoleMapper;
import com.github.taoroot.mapper.sys.SysUserMapper;
import com.github.taoroot.service.sys.SysDeptService;
import com.github.taoroot.service.sys.SysRoleMenuService;
import com.github.taoroot.service.sys.SysRoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@DS(DBName.SYS)
@Service
@AllArgsConstructor
public class SysRoleServiceImpl extends CustomServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuService sysRoleMenuService;
    private final SysUserMapper sysUserMapper;
    private final SysDeptService sysDeptService;

    @Override
    public R getPage(Page<SysRole> page) {
        return R.ok(sysRoleMapper.getPage(page));
    }

    @Override
    public R saveOrUpdateItem(SysRole sysRole) {
        List<Integer> deptIds = new ArrayList<>();
        SysUser sysUser = sysUserMapper.selectById(SecurityUtils.userId());

        // 全部
        if (sysRole.getScopeType().equals(DataScopeTypeEnum.ALL.getValue())) {
            sysRole.setScope(new Integer[]{});
            sysRole.setScope(deptIds.toArray(new Integer[0]));
        }

        // 本级
        if (sysRole.getScopeType().equals(DataScopeTypeEnum.THIS_LEVEL.getValue())) {
            deptIds.add(sysUser.getDeptId());
            sysRole.setScope(deptIds.toArray(new Integer[0]));
        }

        // 本级, 下级
        if (sysRole.getScopeType().equals(DataScopeTypeEnum.THIS_LEVEL_CHILDREN.getValue())) {
            deptIds.add(sysUser.getDeptId()); // 本级
            List<Tree<Integer>> tree = sysDeptService.tree(sysUser.getDeptId()); // 下级
            treeToList(deptIds, tree);
            sysRole.setScope(deptIds.toArray(new Integer[0]));
        }

        // 自定义不能空
        if (sysRole.getScopeType().equals(DataScopeTypeEnum.CUSTOMIZE.getValue())) {
            if (sysRole.getScope() == null || sysRole.getScope().length == 0) {
                throw new RuntimeException("自定义权限范围时,必须至少包含一个范围");
            }
        }

        // 用户级别,无部门id
        if (sysRole.getScopeType().equals(DataScopeTypeEnum.CUSTOMIZE.getValue())) {
            deptIds.clear();
        }


        saveOrUpdate(sysRole);

        // todo 更新角色权限
        if (sysRole.getMenus() != null) {
            List<SysRoleMenu> roleMenuList = Arrays.stream(sysRole.getMenus()).map(menuId -> {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(sysRole.getId());
                roleMenu.setMenuId(menuId);
                return roleMenu;
            }).collect(Collectors.toList());

            sysRoleMenuService.remove(Wrappers.<SysRoleMenu>query().lambda()
                    .eq(SysRoleMenu::getRoleId, sysRole.getId()));
            sysRoleMenuService.saveBatch(roleMenuList);
        }

        return R.ok();
    }

    private void treeToList(List<Integer> list, List<Tree<Integer>> tree) {
        for (Tree<Integer> node : tree) {
            list.add(node.getId());
            if (node.getChildren() != null && node.getChildren().size() > 0) {
                treeToList(list, node.getChildren());
            }
        }
    }
}
