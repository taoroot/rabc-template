<template>
  <div class="login-container">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" auto-complete="on" label-position="left">

      <div class="title-container">
        <h3 class="title tenant">{{ title }} </h3>
        <el-select v-show="false" v-model="loginForm.tenant" class="tenant" placeholder="请选择租户">
          <el-option
            v-for="item in tenant.options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>

      </div>

      <div v-show="false" style="margin-bottom: 20px;" class="tips" @click="loginForm.loginSwitch = !loginForm.loginSwitch">
        <a :class="{'is-unactive':!loginForm.loginSwitch}">用户名登录</a>
        |
        <a :class="{'is-unactive':loginForm.loginSwitch}">手机号登录</a>
      </div>

      <el-form-item v-show="loginForm.loginSwitch" prop="username">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input
          ref="username"
          v-model="loginForm.username"
          placeholder="账号"
          name="username"
          type="text"
          tabindex="1"
          auto-complete="on"
        />
      </el-form-item>

      <el-form-item v-show="loginForm.loginSwitch" prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input
          :key="passwordType"
          ref="password"
          v-model="loginForm.password"
          :type="passwordType"
          placeholder="密码"
          name="password"
          tabindex="2"
          auto-complete="on"
          @keyup.enter.native="handleLogin"
        />
        <span class="show-pwd" @click="showPwd">
          <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>

      <el-form-item v-show="!loginForm.loginSwitch" prop="phone">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input
          ref="phone"
          v-model="loginForm.phone"
          style="width: 69%"
          placeholder="手机号"
          name="phone"
          type="text"
          tabindex="1"
          auto-complete="on"
        />
        <el-button :disabled="!smsEnable" type="info" size="small" @click="sendSms">{{ smsSeconds }}</el-button>
      </el-form-item>

      <el-form-item prop="code">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input
          v-model="loginForm.code"
          placeholder="验证码"
          name="password"
          auto-complete="on"
          @keyup.enter.native="handleLogin"
        />
        <span v-show="loginForm.loginSwitch" class="show-code" @click="refreshCode">
          <el-image style="width: 90px; height: 85%; margin-top: 5px;" :src="codeUrl" />
        </span>
      </el-form-item>

      <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleLogin"> 登 录 </el-button>

      <div v-show="false" class="tips">
        <el-divider>第三方登录</el-divider>
        <ul style="display: flex; align-items:center; flex-wrap: wrap">
          <li v-for="(social, key) in socials" :key="key" style="display: flex; justify-content: center; align-items:center; margin-bottom: 10px">
            <img v-if="social.icon" style="height: 15px; margin-right: 5px" :src="social.icon">
            <span style="line-height: 20px;" @click="oauth2Button(social.type, social.authorizeUri, 540)">{{ social.title }}</span>
            <el-divider direction="vertical" />
          </li>
        </ul>
      </div>
    </el-form>
  </div>
</template>

<script>
import { getSms, getSocials } from '@/api/login'
import openWindow from '@/utils/open-window'
import { title } from '@/settings'
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: 'admin',
        password: '123456',
        imageKey: Math.random().toString(36).substr(2),
        code: '',
        phone: '',
        tenant: 1,
        loginSwitch: true,
        loginType: 'password',
        type: '',
        socialCode: ''
      },
      loginRules: {
        // username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        // password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      title: title,
      codeUrl: '',
      loading: false,
      passwordType: 'password',
      redirect: undefined,
      smsSeconds: '获取验证码',
      smsEnable: true,
      tenant: {
        options: [{
          value: 1,
          label: '租户1 - 不支持修改数据,仅提供预览'
        }, {
          value: 2,
          label: '租户2 - 定时重置数据库,可修改数据'
        }]
      },
      socials: []
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  destroyed() {
    window.removeEventListener('storage', this.afterQRScan)
  },
  created() {
    this.refreshCode()
    // this.getSocialsData()
    window.addEventListener('storage', this.afterQRScan)
  },
  methods: {
    getSocialsData() {
      this.loginForm.socialRedirect = window.location.origin + window.location.pathname + 'auth-redirect.html'
      getSocials({ redirect_uri: this.loginForm.socialRedirect, tenant_id: this.loginForm.tenant }).then(res => {
        this.socials = res.data
      })
    },
    sendSms() {
      getSms({ 'phone': this.loginForm.phone }).then(res => {
        if (res.code !== 0) return
        var seconds = 60
        this.smsEnable = false
        var timer = setInterval(() => {
          this.smsSeconds = seconds-- + ' 秒可重发'
          if (seconds <= 0) {
            clearInterval(timer)
            this.smsSeconds = ' 获取验证码 '
            this.smsEnable = true
          }
        }, 1000)
        this.$notify({ title: '模拟发送', message: res.msg, duration: 0 })
      })
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    refreshCode() {
      this.loginForm.imageKey = Math.random().toString(36).substr(2)
      this.codeUrl = process.env.VUE_APP_BASE_API + 'sys/auth/captcha/image?key=' + this.loginForm.imageKey
    },
    oauth2Button(thirdpart, authorizeUri, wdith) {
      this.loginForm.socialType = thirdpart
      openWindow(authorizeUri, thirdpart, wdith, 540)
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('user/login', this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || '/' })
            this.loading = false
          }).catch(() => {
            this.loading = false
          })
        } else {
          return false
        }
      })
    },
    afterQRScan(e) {
      if (e.key === 'x-admin-oauth-code') {
        this.loginForm.socialCode = e.newValue
        this.loginForm.loginType = 'social'
        this.loading = true
        this.$store.dispatch('user/login', this.loginForm).then(() => {
          this.$router.push({ path: this.redirect || '/' })
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      }
    }
  }
}
</script>

<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

$bg:#2b2f3a;
$light_gray:#fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
  }
}

/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 76%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style lang="scss" scoped>
$bg:#2b2f3a;
$dark_gray:#889aa4;
$light_gray:#eee;

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 160px 35px 0;
    margin: 0 auto;
    overflow: hidden;

  }

  .is-unactive {
     color: #454545;
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .show-code {
    display: block;
    height: 47px;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      text-align: center;
      font-weight: bold;
      margin: 0 auto;
    }

    .tenant {
      display: flex;
      justify-content: center;
      margin: 0px auto 60px auto;
      width: 79%;
    }

  }

  .show-code {
    position: absolute;
    right: 10px;
    top: 1px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}
</style>
