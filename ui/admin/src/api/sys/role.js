import request from '@/utils/request'

/**
 * 分页
 * @param {分页参数} params
 */
export function rolePage(params) {
  return request({
    url: '/sys/role/page',
    method: 'get',
    params
  })
}

/**
 * 批量删除
 * @param {id数组} ids
 */
export function roleDel(ids) {
  return request({
    url: `/sys/role`,
    method: 'delete',
    params: {
      ids: ids
    }
  })
}

/**
 * 新增
 * @param {数据体} data
 */
export function roleAdd(data) {
  return request({
    url: '/sys/role',
    method: 'post',
    data
  })
}

/**
 * 更新
 * @param {数据体} data
 */
export function roleUpdate(data) {
  return request({
    url: `/sys/role`,
    method: 'put',
    data
  })
}
