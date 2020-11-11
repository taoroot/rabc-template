import request from '@/utils/request'

/**
 * 生成代码
 */
export function codeGenApi(ds, name, alias) {
  return request({
    url: '/sys/code/gen',
    method: 'get',
    params: {
      name, ds, alias
    }
  })
}

/**
 * 分页
 * @param {分页参数} params
 */
export function codeTableApi(params) {
  return request({
    url: '/sys/code/table',
    method: 'get',
    params
  })
}
/**
 * 分页
 * @param {分页参数} params
 */
export function codeDbApi() {
  return request({
    url: '/sys/code/db',
    method: 'get'
  })
}
