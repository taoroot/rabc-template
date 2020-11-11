package com.github.taoroot.web.sys;

import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.security.annotation.Log;
import com.github.taoroot.entity.sys.SysRole;
import com.github.taoroot.service.sys.SysRoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Api(tags = "角色管理")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Log("角色创建")
    @ApiOperation("角色创建")
    @PreAuthorize("hasAuthority('sys:role:add')")
    @PostMapping("/sys/role")
    public R create(@RequestBody SysRole sysRole) {
        return R.ok(sysRoleService.saveOrUpdateItem(sysRole));
    }


    @Log("角色删除")
    @ApiOperation("角色删除")
    @PreAuthorize("hasAuthority('sys:role:del')")
    @DeleteMapping("/sys/role")
    public R delete(@RequestParam List<Integer> ids) {
        return R.ok(sysRoleService.removeByIds(ids));
    }


    @Log("角色更新")
    @ApiOperation("角色更新")
    @PreAuthorize("hasAuthority('sys:role:update')")
    @PutMapping("/sys/role")
    public R update(@RequestBody SysRole sysRole) {
        return sysRoleService.saveOrUpdateItem(sysRole);
    }


    @Log(value = "角色分页")
    @ApiOperation("角色分页")
    @PreAuthorize("hasAuthority('sys:role:page')")
    @GetMapping("/sys/role/page")
    public R page(Page<SysRole> page) {
        return sysRoleService.getPage(page);
    }
}
