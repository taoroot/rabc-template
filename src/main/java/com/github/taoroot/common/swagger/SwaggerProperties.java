package com.github.taoroot.common.swagger;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhiyi
 * Date: 2020/2/17
 */
@Log4j2
@Data
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties  {

    /**
     * 名称
     */
    private String name = "swagger";

    /**
     * 版本号
     */
    private String version = "0.1";

    /**
     * 前缀
     */
    private String prefix = "/";
}
