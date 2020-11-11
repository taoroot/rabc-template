package com.github.taoroot.web.sys;

import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.security.annotation.Log;
import com.github.taoroot.entity.sys.SysUser;
import com.github.taoroot.service.sys.SysUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "系统用户")
@RestController
@AllArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @Log("系统用户创建")
    @ApiOperation("系统用户创建")
    @PreAuthorize("hasAuthority('sys:user:add')")
    @PostMapping("/sys/user")
    public R saveItem(@RequestBody SysUser sysUser) {
        return sysUserService.saveOrUpdateItem(sysUser);
    }

    @Log("系统用户删除")
    @ApiOperation("系统用户删除")
    @PreAuthorize("hasAuthority('sys:user:del')")
    @DeleteMapping("/sys/user")
    public R delItem(@RequestParam List<Integer> ids) {
        return R.ok(sysUserService.removeByIds(ids));
    }

    @Log("系统用户更新")
    @ApiOperation("系统用户更新")
    @PreAuthorize("hasAuthority('sys:user:update')")
    @PutMapping("/sys/user")
    public R updateItem(@RequestBody SysUser sysUser) {
        return sysUserService.saveOrUpdateItem(sysUser);
    }

    @Log(value = "系统用户分页")
    @ApiOperation("系统用户分页")
    @PreAuthorize("hasAuthority('sys:user:page')")
    @GetMapping("/sys/user/page")
    public R getPage(Page<SysUser> page,
                     @RequestParam(required = false) String username,
                     @RequestParam(required = false) String phone,
                     @RequestParam(required = false) Integer deptId,
                     @RequestParam(required = false) Boolean enabled) {
        return sysUserService.getPage(page, username, phone, deptId, enabled);
    }
}

