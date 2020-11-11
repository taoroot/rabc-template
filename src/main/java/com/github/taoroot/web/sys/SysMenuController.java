package com.github.taoroot.web.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.core.utils.TreeUtils;
import com.github.taoroot.common.security.annotation.Log;
import com.github.taoroot.entity.sys.SysAuthority;
import com.github.taoroot.entity.sys.SysMenu;
import com.github.taoroot.service.sys.SysAuthorityService;
import com.github.taoroot.service.sys.SysMenuAuthorityService;
import com.github.taoroot.service.sys.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "菜单管理")
@RestController
@AllArgsConstructor
public class SysMenuController {
    private final SysMenuService sysMenuService;
    private final SysAuthorityService sysAuthorityService;
    private final SysMenuAuthorityService menuAuthorityService;

    @Log(value = "菜单树")
    @ApiOperation("菜单树")
    @PreAuthorize("hasAuthority('sys:menu:tree')")
    @GetMapping(value = "/sys/menu/tree")
    public R<List<?>> tree(@RequestParam(defaultValue = "") String title,
                           @RequestParam(required = false) Boolean hidden) {
        return sysMenuService.getTree(title, hidden);
    }

    @Log("菜单详情")
    @ApiOperation("菜单详情")
    @PreAuthorize("hasAuthority('sys:menu:get')")
    @GetMapping("/sys/menu/{id}")
    public R<SysMenu> getById(@PathVariable Integer id) {
        return R.ok(sysMenuService.getById(id));
    }


    @Log("菜单删除")
    @ApiOperation("菜单强删")
    @PreAuthorize("hasAuthority('sys:menu:delforce')")
    @DeleteMapping("/sys/menu/force")
    public R<String> deleteForce(@RequestParam Integer id) {
        Assert.isTrue(id != TreeUtils.ROOT_PARENT_ID, "不能删除根节点");
        return sysMenuService.removeForce(id);
    }


    @Log("菜单删除")
    @ApiOperation("菜单删除")
    @PreAuthorize("hasAuthority('sys:menu:del')")
    @DeleteMapping("/sys/menu")
    public R<String> delete(@RequestParam List<Integer> ids) {
        return sysMenuService.remove(ids);
    }


    @Log("菜单创建")
    @ApiOperation("菜单创建")
    @PreAuthorize("hasAuthority('sys:menu:add')")
    @PostMapping("/sys/menu")
    public R<String> create(@RequestBody SysMenu sysMenu) {
        return sysMenuService.create(sysMenu);
    }


    @Log("菜单更新")
    @ApiOperation("菜单更新")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    @PutMapping("/sys/menu")
    public R<String> update(@RequestBody SysMenu sysMenu) {
        return sysMenuService.update(sysMenu);
    }


    @Log("菜单排序")
    @ApiOperation("菜单排序")
    @PreAuthorize("hasAuthority('sys:menu:sort')")
    @PutMapping("/sys/menu/sort")
    public R<String> sort(Integer menuId, Integer index) {
        return sysMenuService.sort(menuId, index);
    }


    @Log("菜单的权限")
    @ApiOperation("菜单的权限")
    @PreAuthorize("hasAuthority('sys:menu:authority:page')")
    @GetMapping("/sys/menu/authority/page")
    public R<IPage<SysAuthority>> getPage(Integer menuId, Page<SysAuthority> page) {
        return sysAuthorityService.pageByMenu(menuId, page);
    }

    @Log("菜单的权限新增")
    @ApiOperation("菜单的权限新增")
    @PreAuthorize("hasAuthority('sys:menu:authority:add')")
    @PostMapping("/sys/menu/authority")
    public R<String> getPage(@RequestParam("menuId") Integer menuId, @RequestBody List<Integer> ids) {
        return menuAuthorityService.addAuthorityByMenu(menuId, ids);
    }

    @Log("菜单的权限移除")
    @ApiOperation("菜单的权限移除")
    @PreAuthorize("hasAuthority('sys:menu:authority:del')")
    @DeleteMapping("/sys/menu/authority")
    public R<String> deleteMenuAuthority(@RequestParam("menuId") Integer menuId, @RequestBody List<Integer> ids) {
        return menuAuthorityService.removeAuthorityByMenu(menuId, ids);
    }
}
