package com.github.taoroot.service.sys;

import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.data.CustomIService;
import com.github.taoroot.entity.sys.SysMenuAuthority;

import java.util.List;

public interface SysMenuAuthorityService extends CustomIService<SysMenuAuthority> {

    R<String> addAuthorityByMenu(Integer menuId, List<Integer> ids);

    R<String> removeAuthorityByMenu(Integer menuId, List<Integer> ids);
}
