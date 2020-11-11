package com.github.taoroot.web.sys;

import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.core.utils.TreeUtils;
import com.github.taoroot.common.security.annotation.Log;
import com.github.taoroot.service.sys.SysRegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "地区管理")
@RestController
@AllArgsConstructor
public class SysRegionController {

    private final SysRegionService sysRegionService;

    @Log(value = "地区树")
    @ApiOperation("地区树")
    @PreAuthorize("hasAuthority('sys:region:tree')")
    @GetMapping(value = "/sys/region/tree")
    public R<List<?>> tree(@RequestParam(defaultValue = "" + TreeUtils.ROOT_PARENT_ID) Integer parentId) {
        return R.ok(sysRegionService.tree(parentId));
    }
}
