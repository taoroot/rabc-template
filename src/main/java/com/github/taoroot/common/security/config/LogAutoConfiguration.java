package com.github.taoroot.common.security.config;

import com.github.taoroot.common.security.log.LogAspect;
import com.github.taoroot.common.security.log.LogListener;
import com.github.taoroot.common.security.log.LogSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class LogAutoConfiguration {

    @Bean
    public LogListener logListener(@Autowired(required = false) LogSaveService logSaveService) {
        return new LogListener(logSaveService);
    }

    @Bean
    public LogAspect logAspect(ApplicationEventPublisher publisher) {
        return new LogAspect(publisher);
    }

}
