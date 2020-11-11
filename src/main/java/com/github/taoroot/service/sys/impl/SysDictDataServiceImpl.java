package com.github.taoroot.service.sys.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysDictData;
import com.github.taoroot.mapper.sys.SysDictDataMapper;
import com.github.taoroot.service.sys.SysDictDataService;
import org.springframework.stereotype.Service;

@DS(DBName.SYS)
@Service
public class SysDictDataServiceImpl extends CustomServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

}
