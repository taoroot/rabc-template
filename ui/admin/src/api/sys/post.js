import request from '@/utils/request'

/**
 * 分页
 * @param {分页参数} params
 */
export function postPage(params) {
  return request({
    url: '/sys/post/page',
    method: 'get',
    params
  })
}

/**
 * 批量删除
 * @param {id数组} ids
 */
export function postDel(ids) {
  return request({
    url: `/sys/post`,
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
export function postAdd(data) {
  return request({
    url: '/sys/post',
    method: 'post',
    data
  })
}

/**
 * 更新
 * @param {数据体} data
 */
export function postUpdate(data) {
  return request({
    url: `/sys/post`,
    method: 'put',
    data
  })
}
