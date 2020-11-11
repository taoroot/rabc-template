package com.github.taoroot.service.sys.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.taoroot.common.data.CustomServiceImpl;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysPost;
import com.github.taoroot.mapper.sys.SysPostMapper;
import com.github.taoroot.service.sys.SysPostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@DS(DBName.SYS)
@Service
@AllArgsConstructor
public class SysPostServiceImpl extends CustomServiceImpl<SysPostMapper, SysPost> implements SysPostService {

}
