package com.github.taoroot.web.sys;

import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.security.annotation.Log;
import com.github.taoroot.entity.sys.SysAuthority;
import com.github.taoroot.service.sys.SysAuthorityService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "权限管理")
@RestController
@AllArgsConstructor
public class SysAuthorityController {

    private final SysAuthorityService sysAuthorityService;

    @Log(value = "权限分页")
    @ApiOperation("权限分页")
    @PreAuthorize("hasAuthority('sys:authority:page')")
    @GetMapping("/sys/authority/page")
    public R getPage(Page<SysAuthority> page) {
        return R.ok(sysAuthorityService.page(page));
    }
}
