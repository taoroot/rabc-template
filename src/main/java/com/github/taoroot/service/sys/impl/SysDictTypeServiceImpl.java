package com.github.taoroot.service.sys.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysDictData;
import com.github.taoroot.entity.sys.SysDictType;
import com.github.taoroot.mapper.sys.SysDictTypeMapper;
import com.github.taoroot.service.sys.SysDictDataService;
import com.github.taoroot.service.sys.SysDictTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collection;

@DS(DBName.SYS)
@Service
@AllArgsConstructor
public class SysDictTypeServiceImpl extends CustomServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {

    private final SysDictDataService sysDictDataService;

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            int a = sysDictDataService.count(Wrappers.<SysDictData>lambdaQuery().eq(SysDictData::getTypeId, id));
            Assert.isTrue(a == 0, "先删除子集");
        }
        return super.removeByIds(idList);
    }
}
