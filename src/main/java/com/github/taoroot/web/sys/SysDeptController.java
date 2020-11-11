package com.github.taoroot.web.sys;

import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.core.utils.TreeUtils;
import com.github.taoroot.common.security.annotation.Log;
import com.github.taoroot.entity.sys.SysDept;
import com.github.taoroot.service.sys.SysDeptService;
import cn.hutool.core.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "部门管理")
@RestController
@AllArgsConstructor
public class SysDeptController {

    private final SysDeptService sysDeptService;

    @Log("部门创建")
    @ApiOperation("部门创建")
    @PreAuthorize("hasAuthority('sys:dept:add')")
    @PostMapping("/sys/dept")
    public R create(@RequestBody SysDept sysDept) {
        return R.ok(sysDeptService.save(sysDept));
    }

    @Log("部门删除")
    @ApiOperation("部门删除")
    @PreAuthorize("hasAuthority('sys:dept:del')")
    @DeleteMapping("/sys/dept")
    public R delete(@RequestParam List<Integer> ids) {
        return R.ok(sysDeptService.removeByIds(ids));
    }

    @Log("部门详情")
    @ApiOperation("部门详情")
    @PreAuthorize("hasAuthority('sys:dept:get')")
    @GetMapping("/sys/dept/{id}")
    public R get(@PathVariable Integer id) {
        return R.ok(sysDeptService.getById(id));
    }

    @Log("部门更新")
    @ApiOperation("部门更新")
    @PreAuthorize("hasAuthority('sys:dept:update')")
    @PutMapping("/sys/dept")
    public R updateItem(@RequestBody SysDept sysDept) {
        boolean flag = sysDept.getParentId().equals(sysDept.getId());
        Assert.isFalse(flag, "参数有误, 不能设置自己为上一级");
        return R.ok(sysDeptService.updateById(sysDept));
    }

    @Log(value = "部门所有")
    @ApiOperation("部门属性")
    @PreAuthorize("hasAuthority('sys:dept:page')")
    @GetMapping("/sys/dept/page")
    public R getPage(@RequestParam(defaultValue = "" + TreeUtils.ROOT_PARENT_ID) Integer parentId,
                     @RequestParam(required = false) String name) {
        return sysDeptService.tree(parentId, name);
    }
}

