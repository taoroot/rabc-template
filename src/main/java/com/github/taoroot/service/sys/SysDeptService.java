package com.github.taoroot.service.sys;

import cn.hutool.core.lang.tree.Tree;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.data.CustomIService;
import com.github.taoroot.entity.sys.SysDept;

import java.util.List;


public interface SysDeptService extends CustomIService<SysDept> {

    List<Tree<Integer>> tree(Integer parentId);

    R tree(Integer parentId, String name);
}
