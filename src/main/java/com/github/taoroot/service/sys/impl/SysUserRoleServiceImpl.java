package com.github.taoroot.service.sys.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysUserRole;
import com.github.taoroot.mapper.sys.SysUserRoleMapper;
import com.github.taoroot.service.sys.SysUserRoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@DS(DBName.SYS)
@Service
@AllArgsConstructor
public class SysUserRoleServiceImpl extends CustomServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

}
