import request from '@/utils/request'

export function login(data, params) {
  return request({
    url: '/sys/auth/token',
    method: 'post',
    data: data,
    params: params
  })
}

export function getInfo() {
  return request({
    url: '/sys/auth/user_info',
    method: 'get'
  })
}

export function getSocials(params) {
  return request({
    url: '/sys/auth/socials',
    method: 'get',
    params
  })
}

export function getUserProfile() {
  return request({
    url: '/sys/auth/user_info',
    method: 'get'
  })
}

export function unbindUserSocial(id) {
  return request({
    url: `/sys/unbind/social/${id}`,
    method: 'delete'
  })
}

export function bindUserSocial(params) {
  return request({
    url: `/sys/bind/social`,
    method: 'post',
    params
  })
}

export function getBindSocialUri(params) {
  return request({
    url: `/sys/bind/social/uri`,
    method: 'get',
    params
  })
}

export function updateUserPwd(params) {
  return request({
    url: `/sys/auth/password`,
    method: 'put',
    params
  })
}

/**
 * 更新用户头像
 * @param {用户ID} id
 * @param {头像地址} enabled
 */
export function userAvatarChange(avatar) {
  const data = {
    avatar
  }
  return request({
    url: `/sys/user`,
    method: 'put',
    data: data
  })
}

export function getSms(data) {
  return request({
    url: `/user_password`,
    method: 'put',
    data
  })
}

export function updateUser(data) {
  return request({
    url: `/user_info`,
    method: 'put',
    data
  })
}
