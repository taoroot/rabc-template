import request from '@/utils/request'

/**
 * 分页
 * @param {分页参数} params
 */
export function getPage(params) {
  return request({
    url: '/sys/log/page',
    method: 'get',
    params
  })
}
