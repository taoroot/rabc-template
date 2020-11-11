package com.github.taoroot.web.sys;

import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.security.annotation.Log;
import com.github.taoroot.entity.sys.SysDictData;
import com.github.taoroot.entity.sys.SysDictType;
import com.github.taoroot.service.sys.SysDictDataService;
import com.github.taoroot.service.sys.SysDictTypeService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "字典管理")
@RestController
@AllArgsConstructor
public class SysDictController {

    private final SysDictTypeService sysDictTypeService;
    private final SysDictDataService sysDictDataService;

    @Log(value = "字典类型分页")
    @ApiOperation("字典类型分页")
    @PreAuthorize("hasAuthority('sys:dict:type:page')")
    @GetMapping("/sys/dict/type/page")
    public R typePage(Page<SysDictType> page) {
        return R.ok(sysDictTypeService.page(page));
    }

    @Log("字典类型创建")
    @ApiOperation("字典类型创建")
    @PreAuthorize("hasAuthority('sys:dict:type:add')")
    @PostMapping("/sys/dict/type")
    public R typeCreate(@RequestBody SysDictType sysDictType) {
        return R.ok(sysDictTypeService.save(sysDictType));
    }

    @Log("字典类型删除")
    @ApiOperation("字典类型删除")
    @PreAuthorize("hasAuthority('sys:dict:type:del')")
    @DeleteMapping("/sys/dict/type")
    public R typeDelete(@RequestParam List<Integer> ids) {
        return R.ok(sysDictTypeService.removeByIds(ids));
    }

    @Log("字典类型更新")
    @ApiOperation("字典类型更新")
    @PreAuthorize("hasAuthority('sys:dict:type:update')")
    @PutMapping("/sys/dict/type")
    public R typeUpdate(@RequestBody SysDictType sysDictType) {
        return R.ok(sysDictTypeService.updateById(sysDictType));
    }

    @Log(value = "字典数据分页")
    @ApiOperation("字典类型分页")
    @PreAuthorize("hasAuthority('sys:dict:data:page')")
    @GetMapping("/sys/dict/data/page")
    public R dataPage(Page<SysDictData> page, Integer type) {
        return R.ok(sysDictDataService.page(page,
                Wrappers.<SysDictData>lambdaQuery().eq(SysDictData::getTypeId, type)
        ));
    }

    @Log("字典数据创建")
    @ApiOperation("字典类型创建")
    @PreAuthorize("hasAuthority('sys:dict:data:add')")
    @PostMapping("/sys/dict/data")
    public R dataCreate(@RequestBody SysDictData sysDictData) {
        if (sysDictData.getIsDefault()) {
            sysDictDataService.update(Wrappers.<SysDictData>lambdaUpdate()
                    .set(SysDictData::getIsDefault, false)
                    .eq(SysDictData::getTypeId, sysDictData.getTypeId()));
        }
        return R.ok(sysDictDataService.save(sysDictData));
    }

    @Log("字典数据删除")
    @ApiOperation("字典类型删除")
    @PreAuthorize("hasAuthority('sys:dict:data:del')")
    @DeleteMapping("/sys/dict/data")
    public R dataDel(@RequestParam List<Integer> ids) {
        return R.ok(sysDictDataService.removeByIds(ids));
    }

    @Log("字典数据更新")
    @ApiOperation("字典类型更新")
    @PreAuthorize("hasAuthority('sys:dict:data:update')")
    @PutMapping("/sys/dict/data")
    public R dataUpdate(@RequestBody SysDictData sysDictData) {
        if (sysDictData.getIsDefault()) {
            sysDictDataService.update(Wrappers.<SysDictData>lambdaUpdate()
                    .set(SysDictData::getIsDefault, false)
                    .eq(SysDictData::getTypeId, sysDictData.getTypeId()));
        }
        return R.ok(sysDictDataService.updateById(sysDictData));
    }

    @Log("字典类型的数据")
    @ApiOperation("字典类型的数据")
    @PreAuthorize("hasAuthority('sys:dict:type:data')")
    @GetMapping("/sys/dict/type/data")
    public R dataByType(@RequestParam String type,
                        @RequestParam(defaultValue = "true") Boolean keyIsNum,
                        @RequestParam(defaultValue = "value") String valueKey,
                        @RequestParam(defaultValue = "label") String labelKey) {

        SysDictType dictType = sysDictTypeService.getOne(Wrappers.<SysDictType>lambdaQuery()
                .eq(SysDictType::getType, type)
                .eq(SysDictType::getEnabled, true)
        );

        Assert.notNull(dictType, String.format("字典类型 [%s] 不存在!", type));

        List<SysDictData> list = sysDictDataService.list(Wrappers.<SysDictData>lambdaQuery()
                .eq(SysDictData::getTypeId, dictType.getId())
                .eq(SysDictData::getEnabled, true)
        );

        return R.ok(list.stream().map(item -> {
            HashMap<Object, Object> map = new HashMap<>();
            if (keyIsNum) {
                map.put(valueKey, Integer.parseInt(item.getValue()));
            } else {
                map.put(valueKey, item.getValue());
            }
            map.put(labelKey, item.getLabel());
            return map;
        }).collect(Collectors.toList()));
    }
}
