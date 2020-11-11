package com.github.taoroot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.data.CustomIService;
import com.github.taoroot.entity.sys.SysUser;

public interface SysUserService extends CustomIService<SysUser> {


    R getPage(Page<SysUser> page, String username, String phone, Integer deptId, Boolean enabled);

    R saveOrUpdateItem(SysUser sysUser);
}
