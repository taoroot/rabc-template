package com.github.taoroot.mapper.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.taoroot.common.data.CustomBaseMapper;
import com.github.taoroot.entity.sys.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : zhiyi
 * Date: 2020/2/11
 */
@Mapper
public interface SysRoleMapper extends CustomBaseMapper<SysRole> {

    IPage<SysRole> getPage(Page<SysRole> page);

    List<Integer> selectAuthoritiesByRole(Integer roleId);
}
