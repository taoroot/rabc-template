package com.github.taoroot.mapper.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.taoroot.common.data.CustomBaseMapper;
import com.github.taoroot.entity.sys.SysMenu;
import com.github.taoroot.entity.sys.SysRole;
import com.github.taoroot.entity.sys.SysUser;
import com.github.taoroot.entity.sys.SysUserSocial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : zhiyi
 * Date: 2020/2/11
 */
@Mapper
public interface SysUserMapper extends CustomBaseMapper<SysUser> {

    List<Integer> roleIds(@Param("userId") Integer userId);

    List<SysRole> roles(@Param("userId") Integer userId);

    List<String> roleNames(@Param("userId") Integer userId);

    List<SysMenu> menus(@Param("userId") Integer userId, @Param("type") Integer type);

    List<SysUserSocial> socials(@Param("userId") Integer userId);

    List<String> authorityNames(@Param("userId") Integer userId);

    IPage<SysUser> getPage(@Param("page") Page<SysUser> page,
                           @Param("username") String username,
                           @Param("phone") String phone,
                           @Param("deptId") Integer deptId,
                           @Param("enabled") Boolean enabled);
}
