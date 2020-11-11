import request from '@/utils/request'

/**
 * 分页
 * @param {分页参数} params
 */
export function deptPage(params) {
  return request({
    url: '/sys/dept/page',
    method: 'get',
    params
  })
}

/**
 * 批量删除
 * @param {id数组} ids
 */
export function deptDel(ids) {
  return request({
    url: `/sys/dept`,
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
export function deptAdd(data) {
  return request({
    url: '/sys/dept',
    method: 'post',
    data
  })
}

/**
 * 更新
 * @param {数据体} data
 */
export function deptUpdate(data) {
  return request({
    url: `/sys/dept`,
    method: 'put',
    data
  })
}
