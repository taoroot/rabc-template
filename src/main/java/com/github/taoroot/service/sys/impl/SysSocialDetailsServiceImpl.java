package com.github.taoroot.service.sys.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysSocialDetails;
import com.github.taoroot.mapper.sys.SysSocialDetailsMapper;
import com.github.taoroot.service.sys.SysSocialDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@DS(DBName.SYS)
@Service
@AllArgsConstructor
public class SysSocialDetailsServiceImpl extends CustomServiceImpl<SysSocialDetailsMapper, SysSocialDetails> implements SysSocialDetailsService {

}
