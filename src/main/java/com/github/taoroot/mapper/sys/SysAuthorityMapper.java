package com.github.taoroot.mapper.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.taoroot.common.data.CustomBaseMapper;
import com.github.taoroot.entity.sys.SysAuthority;
import com.github.taoroot.entity.sys.SysMenuAuthority;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysAuthorityMapper extends CustomBaseMapper<SysAuthority> {

    IPage<SysAuthority> selectByMenu(@Param("page") Page<SysAuthority> page,
                                     @Param(Constants.WRAPPER) LambdaQueryWrapper<SysMenuAuthority> wrapper);

}
