import request from '@/utils/request'

/**
 * 字典类型分页
 * @param {分页参数} params
 */
export function deptTypePage(params) {
  return request({
    url: '/sys/dict/type/page',
    method: 'get',
    params
  })
}

/**
 * 字典类型新增
 * @param {数据体} data
 */
export function dictTypeAdd(data) {
  return request({
    url: '/sys/dict/type',
    method: 'post',
    data
  })
}

/**
 * 字典类型更新
 * @param {数据体} data
 */
export function dictTypeUpdate(data) {
  return request({
    url: '/sys/dict/type',
    method: 'put',
    data
  })
}

/**
 * 字典类型删除
 * @param {id数组} ids
 */
export function dictTypeDel(ids) {
  return request({
    url: `/sys/dict/type`,
    method: 'delete',
    params: {
      ids: ids
    }
  })
}

/**
 * 字典数据分页
 * @param {分页参数} params
 */
export function deptDataPage(params) {
  return request({
    url: '/sys/dict/data/page',
    method: 'get',
    params
  })
}

/**
 * 字典类型新增
 * @param {数据体} data
 */
export function dictDataAdd(data) {
  return request({
    url: '/sys/dict/data',
    method: 'post',
    data
  })
}

/**
 * 字典类型更新
 * @param {数据体} data
 */
export function dictDataUpdate(data) {
  return request({
    url: '/sys/dict/data',
    method: 'put',
    data
  })
}

/**
 * 字典类型删除
 * @param {id数组} ids
 */
export function dictDataDel(ids) {
  return request({
    url: `/sys/dict/data`,
    method: 'delete',
    params: {
      ids: ids
    }
  })
}

/**
 * 字典类型的数据
 * @param {*} params
 */
export function getDataByType(params) {
  return request({
    url: '/sys/dict/type/data',
    method: 'get',
    params
  })
}
