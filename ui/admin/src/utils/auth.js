import Cookies from 'js-cookie'

const AccessTokenKey = 'access_token'

export function getToken() {
  return Cookies.get(AccessTokenKey)
}

export function setToken(token) {
  return Cookies.set(AccessTokenKey, token)
}

export function removeToken() {
  return Cookies.remove(AccessTokenKey)
}

const RefreshTokenKey = 'refresh_token'

export function getRefreshToken() {
  return Cookies.get(RefreshTokenKey)
}

export function setRefreshToken(token) {
  return Cookies.set(RefreshTokenKey, token)
}

export function removeRefreshToken() {
  return Cookies.remove(RefreshTokenKey)
}
