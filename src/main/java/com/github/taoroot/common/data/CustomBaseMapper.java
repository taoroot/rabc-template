package com.github.taoroot.common.data;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

public interface CustomBaseMapper<T> extends BaseMapper<T> {

	/**
	 * 根据 entity 条件 ，查询部门作用域下全部记录（并翻页）
	 *
	 * @param page         分页查询条件（可以为 RowBounds.DEFAULT）
	 * @param queryWrapper 实体对象封装操作类（可以为 null）
	 */
	<E extends IPage<T>> E selectPageByDept(E page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

	/**
	 * 根据 entity 条件 ，查询个人作用域全部记录（并翻页）
	 *
	 * @param page         分页查询条件（可以为 RowBounds.DEFAULT）
	 * @param queryWrapper 实体对象封装操作类（可以为 null）
	 */
	<E extends IPage<T>> E selectPageByOwner(E page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

}
