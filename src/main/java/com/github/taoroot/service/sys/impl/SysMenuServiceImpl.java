package com.github.taoroot.service.sys.impl;


import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.core.utils.TreeUtils;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysMenu;
import com.github.taoroot.entity.sys.SysMenuAuthority;
import com.github.taoroot.entity.sys.SysRoleMenu;
import com.github.taoroot.mapper.sys.SysMenuAuthorityMapper;
import com.github.taoroot.mapper.sys.SysMenuMapper;
import com.github.taoroot.mapper.sys.SysRoleMenuMapper;
import com.github.taoroot.service.sys.SysMenuAuthorityService;
import com.github.taoroot.service.sys.SysMenuService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;


@DS(DBName.SYS)
@Service
@AllArgsConstructor
public class SysMenuServiceImpl extends CustomServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysMenuAuthorityService sysMenuAuthorityService;
    private final SysMenuAuthorityMapper sysMenuAuthorityMapper;

    @Override
    public R<List<?>> getTree(String title, Boolean hidden) {
        LambdaQueryWrapper<SysMenu> query = Wrappers.lambdaQuery();

        if (hidden != null) {
            query.eq(SysMenu::getHidden, hidden);
        }

        if (!StringUtils.isEmpty(title)) {
            query.like(SysMenu::getTitle, title);
        }

        List<SysMenu> menuList = list(query);

        List<Tree<Integer>> authorityTree = TreeUtil.build(menuList, TreeUtils.ROOT_PARENT_ID, (treeNode, tree) -> {
            tree.setId(treeNode.getId());
            tree.setParentId(treeNode.getParentId());
            tree.setWeight(treeNode.getWeight());
            tree.setName(treeNode.getName());
            tree.putExtra("path", treeNode.getPath());
            tree.putExtra("type", treeNode.getType());
            tree.putExtra("component", treeNode.getComponent());
            tree.putExtra("hidden", treeNode.getHidden());
            tree.putExtra("alwaysShow", treeNode.getAlwaysShow());
            tree.putExtra("redirect", treeNode.getRedirect());
            tree.putExtra("title", treeNode.getTitle());
            tree.putExtra("icon", treeNode.getIcon());
            tree.putExtra("authority", treeNode.getAuthority());
            tree.putExtra("breadcrumb", treeNode.getBreadcrumb());
        });

        if (authorityTree.size() == 0) {
            return R.ok(menuList);
        }

        // 排序
        TreeUtils.computeSort(authorityTree);

        // 计算路径
        computePath(authorityTree);

        return R.ok(authorityTree);
    }


    public static void computePath(List<? extends Tree<Integer>> trees) {
        if (trees == null || trees.size() == 0) {
            return;
        }
        for (Tree<Integer> parent : trees) {
            if (parent.getChildren() == null) {
                return;
            }
            parent.putExtra("absPath", parent.get("path"));
            for (Tree<Integer> child : parent.getChildren()) {
                String path1 = (String) parent.get("path");
                String path2 = (String) child.get("path");
                if (!StrUtil.startWithAny(path2, "http", "https")) {
                    child.putExtra("absPath", path1 + "/" + path2);
                } else {
                    child.putExtra("absPath", path2);
                }
            }
            computePath(parent.getChildren());
        }
    }


    @Transactional
    @Override
    public R<String> remove(List<Integer> ids) {
        for (Integer id : ids) {
            Integer count = sysRoleMenuMapper.selectCount(Wrappers.<SysRoleMenu>lambdaQuery()
                    .eq(SysRoleMenu::getMenuId, id));
            Assert.isTrue(count == 0, "资源被角色绑定,请先解绑");

            count = sysMenuAuthorityService.count(Wrappers.<SysMenuAuthority>lambdaQuery()
                    .eq(SysMenuAuthority::getMenuId, id));
            Assert.isTrue(count == 0, "请先移除权限");

            count = count(Wrappers.<SysMenu>lambdaQuery()
                    .eq(SysMenu::getParentId, id));
            Assert.isTrue(count == 0, "请先移除子集");

        }
        Assert.isTrue(removeByIds(ids), "创建失败");

        return R.okMsg("删除成功");
    }

    @Override
    @Transactional
    public R<String> sort(Integer menuId, Integer index) {
        SysMenu sysMenu = getById(menuId);
        Assert.notNull(sysMenu, "菜单不存在");

        List<SysMenu> list = list(Wrappers.<SysMenu>lambdaQuery()
                .eq(SysMenu::getParentId, sysMenu.getParentId())
                .ne(SysMenu::getId, sysMenu.getId())
                .orderByAsc(SysMenu::getWeight));

        list.add(index, sysMenu);

        // 同一级重新排序
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setWeight(i);
        }
        Assert.isTrue(updateBatchById(list), "更新失败");

        return R.okMsg("更新成功");
    }

    @Override
    @Transactional
    public R<String> removeForce(Integer id) {
        removeForceChild(id);
        return R.okMsg("删除成功");
    }

    private void removeForceChild(Integer menuId) {
        // 删除绑定的角色
        sysRoleMenuMapper.delete(Wrappers.<SysRoleMenu>lambdaQuery()
                .eq(SysRoleMenu::getMenuId, menuId));
        // 删除绑定的权限
        sysMenuAuthorityMapper.delete(Wrappers.<SysMenuAuthority>lambdaQuery()
                .eq(SysMenuAuthority::getMenuId, menuId));
        // 删除自己
        getBaseMapper().delete(Wrappers.<SysMenu>lambdaQuery()
                .eq(SysMenu::getId, menuId));
        // 递归
        list(Wrappers.<SysMenu>lambdaQuery()
                .eq(SysMenu::getParentId, menuId))
                .forEach(item -> removeForceChild(item.getId()));
    }


    @Transactional
    @Override
    public R<String> update(SysMenu menuPut) {
        Assert.isTrue(!menuPut.getParentId().equals(menuPut.getId()), "参数有误, 不能设置自己为上一级");
        menuPut.setWeight(null); // 使用排序接口更新

        if (TreeUtils.ROOT_PARENT_ID != menuPut.getParentId()) {
            SysMenu parentMenu = getById(menuPut.getParentId());
            Assert.notNull(parentMenu, "父级不存在");
            Assert.isTrue(SysMenu.MENU.equals(parentMenu.getType()), "功能不能包含菜单");
        }

        Assert.isTrue(updateById(menuPut), "更新失败");

        return R.okMsg("更新成功");
    }

    @Transactional
    @Override
    public R<String> create(SysMenu entity) {
        if (entity.getParentId() != TreeUtils.ROOT_PARENT_ID) {
            SysMenu parent = getById(entity.getParentId());
            Assert.notNull(parent, "父级不存在");
            Assert.isTrue(SysMenu.MENU.equals(parent.getType()), "功能不能包含菜单");
        }


        Assert.isTrue(save(entity), "创建失败");


        int index = count(Wrappers.<SysMenu>lambdaQuery()
                .eq(SysMenu::getParentId, entity.getParentId())
                .orderByAsc(SysMenu::getWeight));

        // 重新排序(插入最后)
        sort(entity.getId(), index - 1);

        return R.okMsg("创建成功");
    }
}
