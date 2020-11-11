package com.github.taoroot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.data.CustomIService;
import com.github.taoroot.entity.sys.SysRole;

public interface SysRoleService extends CustomIService<SysRole> {

    R getPage(Page<SysRole> page);

    R saveOrUpdateItem(SysRole sysRole);
}
