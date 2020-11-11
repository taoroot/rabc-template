package com.github.taoroot.service.sys.impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysRegion;
import com.github.taoroot.mapper.sys.SysRegionMapper;
import com.github.taoroot.service.sys.SysRegionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@DS(DBName.SYS)
@Service
@AllArgsConstructor
public class SysRegionServiceImpl extends CustomServiceImpl<SysRegionMapper, SysRegion> implements SysRegionService {

    @Override
    public List<Tree<Integer>> tree(Integer parentId) {
        List<SysRegion> list = baseMapper.selectList(Wrappers.lambdaQuery());
        return getTrees(parentId, list);
    }

    private List<Tree<Integer>> getTrees(Integer parentId, List<SysRegion> list) {
        return TreeUtil.build(list, parentId, (treeNode, tree) -> {
            tree.setId(treeNode.getId());
            tree.setParentId(treeNode.getParentId());
            tree.setWeight(treeNode.getName());
            tree.setName(treeNode.getName());
            tree.putExtra("code", treeNode.getCode());
            tree.putExtra("type", treeNode.getType());
        });
    }
}
