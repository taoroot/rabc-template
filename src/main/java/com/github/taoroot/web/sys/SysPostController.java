package com.github.taoroot.web.sys;

import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.security.annotation.Log;
import com.github.taoroot.entity.sys.SysPost;
import com.github.taoroot.service.sys.SysPostService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "岗位管理")
@RestController
@AllArgsConstructor
public class SysPostController {

    private final SysPostService sysPostService;

    @Log("岗位创建")
    @ApiOperation("岗位创建")
    @PreAuthorize("hasAuthority('sys:post:add')")
    @PostMapping("/sys/post")
    public R create(@RequestBody SysPost sysPost) {
        return R.ok(sysPostService.save(sysPost));
    }

    @Log("岗位删除")
    @ApiOperation("岗位删除")
    @PreAuthorize("hasAuthority('sys:post:del')")
    @DeleteMapping("/sys/post")
    public R delete(@RequestParam List<Integer> ids) {
        return R.ok(sysPostService.removeByIds(ids));
    }

    @Log("岗位更新")
    @ApiOperation("岗位更新")
    @PreAuthorize("hasAuthority('sys:post:update')")
    @PutMapping("/sys/post")
    public R updateItem(@RequestBody SysPost sysPost) {
        return R.ok(sysPostService.save(sysPost));
    }

    @Log(value = "岗位分页")
    @ApiOperation("岗位分页")
    @PreAuthorize("hasAuthority('sys:post:page')")
    @GetMapping("/sys/post/page")
    public R getPage(Page<SysPost> page) {
        return R.ok(sysPostService.page(page));
    }
}
