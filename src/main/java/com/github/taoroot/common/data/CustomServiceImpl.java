package com.github.taoroot.common.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.taoroot.common.core.datascope.*;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;

public class CustomServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements CustomIService<T>, IService<T> {

    @Override
    public boolean save(T entity) {
        dataScope(entity);
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        entityList.forEach(this::dataScope);
        return super.saveBatch(entityList, batchSize);
    }

    @SneakyThrows
    @Override
    public boolean updateById(T entity) {
        if (isDs()) {
            QueryWrapper<T> query = Wrappers.query();
            dataScope(entity, query);
            TableInfo tableInfo = TableInfoHelper.getTableInfo(currentModelClass());
            for (Field field : currentModelClass().getDeclaredFields()) {
                // 主键
                if (field.getName().equals(tableInfo.getKeyProperty())) {
                    field.setAccessible(true);
                    Integer id = (Integer) field.get(entity);
                    query.eq(tableInfo.getKeyColumn(), id);
                }
            }
            return update(entity, query);
        }
        return super.updateById(entity);
    }

    @SneakyThrows
    @Override
    public boolean removeById(Serializable id) {
        if (isDs()) {
            QueryWrapper<T> query = Wrappers.query();
            dataScope(null, query);
            TableInfo tableInfo = TableInfoHelper.getTableInfo(currentModelClass());
            return remove(query.eq(tableInfo.getKeyColumn(), id));
        }
        return super.removeById(id);
    }

    @SneakyThrows
    @Override
    @Transactional
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        if (isDs()) {
            for (Serializable id : idList) {
                removeById(id);
            }
            return true;
        }
        return super.removeByIds(idList);
    }

    private Boolean isDs(){
        DataScope dataScope = DataScopeContextHolder.get();
        return dataScope != null && dataScope.getGlobal();
    }

    @SneakyThrows
    private void dataScope(T entity) {
        dataScope(entity, Wrappers.query());
    }

    @SneakyThrows
    private void dataScope(T entity, QueryWrapper<T> query) {
        DataScope dataScope = DataScopeContextHolder.get();
        if (dataScope != null && dataScope.getGlobal()) {
            Class<T> classType = currentModelClass();
            if (DataScopeTypeEnum.OWN.equals(dataScope.getDataScopeTypeEnum())) {
                for (Field field : classType.getDeclaredFields()) {
                    ScopeId annotation = field.getAnnotation(ScopeId.class);
                    if (annotation != null && annotation.value().equals(ScopeType.OWN)) {
                        field.setAccessible(true);
                        if (entity != null) {
                            field.set(entity, dataScope.getUserId());
                        }
                        query.eq(dataScope.getScopeOwnName(), dataScope.getUserId());
                        break;
                    }
                }
            } else {
                for (Field field : classType.getFields()) {
                    ScopeId annotation = field.getAnnotation(ScopeId.class);
                    if (annotation != null && annotation.value().equals(ScopeType.DEPT)) {
                        if (entity != null) {
                            field.setAccessible(true);
                            field.set(entity, dataScope.getDeptIds());
                        }
                        query.in(dataScope.getScopeName(), dataScope.getDeptIds());
                        break;
                    }
                }
            }
        }
    }
}
