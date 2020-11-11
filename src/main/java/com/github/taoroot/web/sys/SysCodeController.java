package com.github.taoroot.web.sys;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.security.annotation.Log;
import com.github.taoroot.common.security.annotation.PermitAll;
import com.github.taoroot.service.sys.SysCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 代码生成器
 */
@Api(tags = "代码模板")
@RestController
@AllArgsConstructor
public class SysCodeController {
    private final SysCodeService sysCodeService;
    private final DynamicDataSourceProperties properties;

    /**
     * 生成代码文件
     */
    @PermitAll
    @SneakyThrows
    @GetMapping("/sys/code/gen")
    public R get(@RequestParam(required = false) String ds,
                 @RequestParam String name,
                 @RequestParam(required = false) String alias) {
        if (!StringUtils.isEmpty(ds)) {
            DynamicDataSourceContextHolder.push(ds);
            R r = sysCodeService.generatorCode(name, alias);
            DynamicDataSourceContextHolder.clear();
            return r;
        }
        return sysCodeService.generatorCode(name, alias);
    }

    @PermitAll
    @Log(value = "库名分页")
    @ApiOperation("库名分页")
    @GetMapping("/sys/code/db")
    public R db() {
        return R.ok(properties.getDatasource().keySet());
    }

    @PermitAll
    @Log(value = "表名列表")
    @ApiOperation("表名列表")
    @GetMapping("/sys/code/table")
    public R table(@RequestParam(required = false) String ds) {
        if (!StringUtils.isEmpty(ds)) {
            DynamicDataSourceContextHolder.push(ds);
            R r = sysCodeService.tableList();
            DynamicDataSourceContextHolder.clear();
            return r;
        }
        return sysCodeService.tableList();
    }
}
