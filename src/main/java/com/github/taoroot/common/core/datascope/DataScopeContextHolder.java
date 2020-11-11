package com.github.taoroot.common.core.datascope;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DataScopeContextHolder {

    private final ThreadLocal<DataScope> DATA_SCOPE_THREAD_LOCAL = new ThreadLocal<>();

    public void set(DataScope dataScope) {
        DATA_SCOPE_THREAD_LOCAL.set(dataScope);
    }

    public DataScope get() {
        return DATA_SCOPE_THREAD_LOCAL.get();
    }

    public void clear() {
        DATA_SCOPE_THREAD_LOCAL.remove();
    }
}
