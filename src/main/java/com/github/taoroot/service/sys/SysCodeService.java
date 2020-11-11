package com.github.taoroot.service.sys;

import com.github.taoroot.common.core.utils.R;

public interface SysCodeService {

    /**
     * 生成代码
     *
     * @param tableName 表名称
     * @param tableName 表别名
     * @return
     */
    R generatorCode(String tableName, String tableAlias);

    /**
     * 数据表接口
     * @return
     */
    R tableList();
}
