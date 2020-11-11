package com.github.taoroot.mapper.sys;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.taoroot.common.data.CustomBaseMapper;
import com.github.taoroot.entity.sys.SysLog;
import com.github.taoroot.entity.sys.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author : zhiyi
 * Date: 2020/2/11
 */
@Mapper
public interface SysLogMapper extends CustomBaseMapper<SysLog> {

    IPage<SysLog> getPage(@Param("page") Page<SysLog> page,
                          @Param(Constants.WRAPPER) QueryWrapper<SysUser> wrapper);
}
