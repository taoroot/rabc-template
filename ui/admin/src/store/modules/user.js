import { login, getInfo } from '@/api/login'
import { getToken, setToken, removeToken, setRefreshToken, removeRefreshToken } from '@/utils/auth'
import { resetRouter } from '@/router'

const getDefaultState = () => {
  return {
    token: getToken(),
    name: '',
    permissions: [],
    avatar: ''
  }
}

const state = getDefaultState()

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_PERMISSION: (state, permissions) => {
    state.permissions = permissions
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  }
}

const actions = {
  // user login
  login({ commit }, userInfo) {
    const { username, password, imageKey, code, tenant, loginType, socialType, socialCode, socialRedirect } = userInfo
    console.log(userInfo)
    return new Promise((resolve, reject) => {
      var data = new FormData()
      data.append('grant_type', loginType)
      var params = {
        tenant_id: tenant
      }
      if (loginType === 'password') {
        // 账号登录
        data.append('username', username.trim())
        data.append('password', password.trim())
        params.imageKey = imageKey // 验证码
        params.imageCode = code
      }
      if (loginType === 'social') {
        // 第三方授权登录
        data.append('type', socialType)
        data.append('code', socialCode)
        data.append('redirect_uri', socialRedirect)
      }
      login(data, params).then(response => {
        const { access_token, refresh_token } = response
        commit('SET_TOKEN', access_token)
        setRefreshToken(refresh_token)
        setToken(access_token)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo().then(response => {
        const { data } = response

        if (!data) {
          return reject('Verification failed, please Login again.')
        }

        const { username, nickname, avatar } = data.info

        commit('SET_NAME', nickname || username)
        commit('SET_AVATAR', avatar)
        commit('SET_PERMISSION', data.functions)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // user logout
  logout({ commit }) {
    return new Promise((resolve, reject) => {
      // logout().then(() => {
      removeToken() // must remove  token  first
      removeRefreshToken()
      resetRouter()
      commit('RESET_STATE')
      resolve()
      // }).catch(error => {
      //   reject(error)
      // })
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      removeToken() // must remove  token  first
      commit('RESET_STATE')
      resolve()
    })
  },

  // save token
  saveToken({ commit }, token) {
    return new Promise(resolve => {
      commit('SET_TOKEN', token)
      setToken(token)
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

