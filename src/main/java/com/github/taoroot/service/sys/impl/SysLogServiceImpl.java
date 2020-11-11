package com.github.taoroot.service.sys.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysLog;
import com.github.taoroot.mapper.sys.SysLogMapper;
import com.github.taoroot.service.sys.SysLogService;
import org.springframework.stereotype.Service;

@DS(DBName.SYS)
@Service
public class SysLogServiceImpl extends CustomServiceImpl<SysLogMapper, SysLog> implements SysLogService {

}
