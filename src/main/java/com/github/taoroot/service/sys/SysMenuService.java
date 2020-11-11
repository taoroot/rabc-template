package com.github.taoroot.service.sys;

import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.data.CustomIService;
import com.github.taoroot.entity.sys.SysMenu;

import java.util.List;

public interface SysMenuService extends CustomIService<SysMenu> {

    R<List<?>> getTree(String title, Boolean hidden);

    R<String> update(SysMenu sysMenu);

    R<String> create(SysMenu sysMenu);

    R<String> remove(List<Integer> ids);

    R<String> sort(Integer menuId, Integer offset);

    R<String> removeForce(Integer id);
}
