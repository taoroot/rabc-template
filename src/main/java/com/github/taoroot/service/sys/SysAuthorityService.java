package com.github.taoroot.service.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.data.CustomIService;
import com.github.taoroot.entity.sys.SysAuthority;

public interface SysAuthorityService extends CustomIService<SysAuthority> {

    R<IPage<SysAuthority>> pageByMenu(Integer menuId, Page<SysAuthority> page);
}
