package com.github.taoroot.service.sys.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysMenuAuthority;
import com.github.taoroot.mapper.sys.SysMenuAuthorityMapper;
import com.github.taoroot.service.sys.SysMenuAuthorityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;


@DS(DBName.SYS)
@Service
@AllArgsConstructor
public class SysMenuAuthorityServiceImpl extends CustomServiceImpl<SysMenuAuthorityMapper, SysMenuAuthority> implements SysMenuAuthorityService {


    @Override
    @Transactional
    public R<String> addAuthorityByMenu(Integer menuId, List<Integer> ids) {
        List<SysMenuAuthority> collect = ids.stream().distinct()
                .map(authorityId -> new SysMenuAuthority(null, menuId, authorityId))
                .collect(Collectors.toList());

        Assert.isTrue(saveBatch(collect), "保存失败");

        return R.okMsg("添加成功");
    }

    @Override
    @Transactional
    public R<String> removeAuthorityByMenu(Integer menuId, List<Integer> ids) {
        Assert.isTrue(removeByIds(ids), "移除失败");
        return R.okMsg("移除成功");
    }
}
