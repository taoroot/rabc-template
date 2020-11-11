package com.github.taoroot.common.data;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.taoroot.common.core.datascope.DataScope;
import com.github.taoroot.common.core.datascope.DataScopeContextHolder;
import com.github.taoroot.common.core.datascope.DataScopeTypeEnum;
import com.github.taoroot.common.core.datascope.EnableScope;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

@Log4j2
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataScopeInterceptor extends AbstractSqlParserHandler implements Interceptor {

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        // SQL 解析
        this.sqlParser(metaObject);

        // 先判断是不是SELECT操作, 跳过存储过程
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (SqlCommandType.SELECT != mappedStatement.getSqlCommandType()
                || StatementType.CALLABLE == mappedStatement.getStatementType()) {
            return invocation.proceed();
        }

        // 针对定义了rowBounds，做为mapper接口方法的参数
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Object paramObj = boundSql.getParameterObject();

        // 判断参数里是否有 DataScope 对象
        DataScope dataScope = findDataScopeByParams(paramObj);

        // 不需要场合
        if (dataScope == null) {
            // 增强功能通过注解 @EnableScope 获取
            dataScope = findDataScopeByAnnotation(mappedStatement);
            if (dataScope == null) {
                return invocation.proceed();
            }
        }

        String originalSql = boundSql.getSql();
        Connection connection = (Connection) invocation.getArgs()[0];

        // 全部查询
        if (DataScopeTypeEnum.ALL == dataScope.getDataScopeTypeEnum()) {
            return invocation.proceed();
        }

        // 分两类:
        if (DataScopeTypeEnum.OWN == dataScope.getDataScopeTypeEnum()) {
            // 利用 userId
            Integer userId = dataScope.getUserId();
            String scopeName = dataScope.getScopeOwnName();
            originalSql = "select * from (" + originalSql + ") sys_data_scope where sys_data_scope." + scopeName + "=" + userId;
        } else {
            // 利用 deptId
            if (dataScope.getDeptIds() == null || dataScope.getDeptIds().size() == 0) {
                // 根据登陆者信息获取最高访问权限
                queryDataScope(dataScope, dataScope.getRoleIds(), connection);
            }
            String join = CollectionUtil.join(dataScope.getDeptIds(), ",");
            String scopeName = dataScope.getScopeName();
            originalSql = "select * from (" + originalSql + ") sys_data_scope where sys_data_scope." + scopeName + " in (" + join + ")";

        }
        metaObject.setValue("delegate.boundSql.sql", originalSql);

        return invocation.proceed();
    }

    protected void queryDataScope(DataScope dataScope, List<Integer> roles, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(DataScope.getSql())) {
            statement.setString(1, CollectionUtil.join(roles, ","));
            int type;
            String scope;
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    type = resultSet.getInt(1);
                    scope = resultSet.getString(2);
                    dataScope.setDataScopeTypeEnum(DataScopeTypeEnum.valueOf(type));
                    List<Integer> list = new ObjectMapper().readValue(scope, new TypeReference<List<Integer>>() {
                    });
                    dataScope.setDeptIds(list);
                }
            }
        } catch (Exception e) {
            throw ExceptionUtils.mpe("Error: Method queryDataScope execution error of sql : \n %s \n", e, DataScope.getSql());
        }
    }

    private DataScope findDataScopeByParams(Object parameterObj) {
        if (parameterObj instanceof DataScope) {
            return (DataScope) parameterObj;
        } else if (parameterObj instanceof Map) {
            for (Object val : ((Map<?, ?>) parameterObj).values()) {
                if (val instanceof DataScope) {
                    return (DataScope) val;
                }
            }
        }
        return null;
    }

    private DataScope findDataScopeByAnnotation(MappedStatement mappedStatement) {
        DataScope dataScope = DataScopeContextHolder.get();
        if (dataScope == null) {
            return null;
        }

        try {
            Class<?> classType = Class.forName(mappedStatement.getId().substring(0, mappedStatement.getId().lastIndexOf(".")));
            String mName = mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".") + 1);
            for (Method method : classType.getDeclaredMethods()) {
                if (method.isAnnotationPresent(EnableScope.class) && mName.equals(method.getName())) {
                    EnableScope annotation = method.getAnnotation(EnableScope.class);
                    dataScope.setScopeOwnName(annotation.scopeOwnName());
                    return dataScope;
                }
            }
            // 全局情况
            if (dataScope.getGlobal()) {
                return dataScope;
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
        return null;
    }
}
