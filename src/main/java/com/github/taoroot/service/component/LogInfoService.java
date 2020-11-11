package com.github.taoroot.service.component;

import com.github.taoroot.common.core.vo.LogInfo;
import com.github.taoroot.common.security.log.LogSaveService;
import com.github.taoroot.entity.sys.SysLog;
import com.github.taoroot.mapper.sys.SysLogMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@AllArgsConstructor
public class LogInfoService implements LogSaveService {

    private final SysLogMapper sysLogMapper;

    @Override
    public void save(LogInfo logInfo) {
        log.info(logInfo);
        SysLog sysLog = new SysLog();
        BeanUtils.copyProperties(logInfo, sysLog);
        sysLogMapper.insert(sysLog);
    }
}
