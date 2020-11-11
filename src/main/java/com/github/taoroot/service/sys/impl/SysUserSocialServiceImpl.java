package com.github.taoroot.service.sys.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysUserSocial;
import com.github.taoroot.mapper.sys.SysUserSocialMapper;
import com.github.taoroot.service.sys.SysUserSocialService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@DS(DBName.SYS)
@Service
@AllArgsConstructor
public class SysUserSocialServiceImpl extends CustomServiceImpl<SysUserSocialMapper, SysUserSocial> implements SysUserSocialService {

}


