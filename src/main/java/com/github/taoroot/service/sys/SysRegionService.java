package com.github.taoroot.service.sys;

import cn.hutool.core.lang.tree.Tree;
import com.github.taoroot.common.data.CustomIService;
import com.github.taoroot.entity.sys.SysRegion;

import java.util.List;


public interface SysRegionService extends CustomIService<SysRegion> {

    List<Tree<Integer>> tree(Integer parentId);
}
