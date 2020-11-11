package com.github.taoroot.service.sys.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysAuthority;
import com.github.taoroot.entity.sys.SysMenuAuthority;
import com.github.taoroot.mapper.sys.SysAuthorityMapper;
import com.github.taoroot.service.sys.SysAuthorityService;
import org.springframework.stereotype.Service;

@DS(DBName.SYS)
@Service
public class SysAuthorityServiceImpl extends CustomServiceImpl<SysAuthorityMapper, SysAuthority> implements SysAuthorityService {

    @Override
    public R<IPage<SysAuthority>> pageByMenu(Integer menuId, Page<SysAuthority> page) {
        return R.ok(baseMapper.selectByMenu(
                page,
                Wrappers.<SysMenuAuthority>lambdaQuery()
                        .eq(SysMenuAuthority::getMenuId, menuId)
        ));
    }

}
