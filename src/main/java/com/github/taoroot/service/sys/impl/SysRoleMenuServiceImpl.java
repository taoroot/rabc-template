package com.github.taoroot.service.sys.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysRoleMenu;
import com.github.taoroot.mapper.sys.SysRoleMenuMapper;
import com.github.taoroot.service.sys.SysRoleMenuService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@DS(DBName.SYS)
@Service
@AllArgsConstructor
public class SysRoleMenuServiceImpl extends CustomServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {


}
