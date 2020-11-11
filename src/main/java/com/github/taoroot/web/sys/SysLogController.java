package com.github.taoroot.web.sys;

import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.security.annotation.Log;
import com.github.taoroot.entity.sys.SysLog;
import com.github.taoroot.mapper.sys.SysLogMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "日志管理")
@RestController
@AllArgsConstructor
public class SysLogController {

    private final SysLogMapper sysLogMapper;

    @Log(value = "日志分页")
    @ApiOperation("日志分页")
    @PreAuthorize("hasAuthority('sys:log:page')")
    @GetMapping("/sys/log/page")
    public R getPage(Page<SysLog> page) {
        return R.ok(sysLogMapper.getPage(page, Wrappers.emptyWrapper()));
    }
}
