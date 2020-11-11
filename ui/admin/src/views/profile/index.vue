<template>
  <div class="app-container">

    <el-row :gutter="10">
      <el-col :span="10" :xs="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>个人信息</span>
          </div>
          <div>
            <div style="text-align: center; margin-bottom: 20px">
              <userAvatar :user="user" />
            </div>
            <el-row class="row-height">
              <el-col :span="12"><svg-icon icon-class="user" />用户名称</el-col>
              <el-col :span="12" style="text-align: right"> {{ user.username }} </el-col>
            </el-row>
            <el-row class="row-height">
              <el-col :span="12"><svg-icon icon-class="user" />用户昵称</el-col>
              <el-col :span="12" style="text-align: right"> {{ user.nickname }} </el-col>
            </el-row>
            <el-row class="row-height">
              <el-col :span="12"><svg-icon icon-class="phone" />手机号码</el-col>
              <el-col :span="12" style="text-align: right"> {{ user.phone }} </el-col>
            </el-row>
            <el-row class="row-height">
              <el-col :span="12"><svg-icon icon-class="tree" />所属部门</el-col>
              <el-col :span="12" style="text-align: right"> {{ deptName }} </el-col>
            </el-row>
            <el-row class="row-height">
              <el-col :span="12"><svg-icon icon-class="role" />所属角色</el-col>
              <el-col :span="12" style="text-align: right">
                <el-tag v-for="role in roles" :key="role.id" size="mini">{{ role.name }}</el-tag>
              </el-col>
            </el-row>

            <el-divider>第三方账号</el-divider>

            <el-table ref="table" :show-header="false" size="mini" :data="socials" style="width: 100%">

              <el-table-column align="left">
                <template slot-scope="scope">
                  <img v-if="scope.row.icon" style="height: 15px; margin-right: 5px" :src="scope.row.icon">
                  <span style="font-size: 16px;">{{ scope.row.socialName }}</span>
                </template>
              </el-table-column>

              <el-table-column align="center">
                <template slot-scope="scope">
                  <img v-if="scope.row.socialAvatar" style="height: 15px; margin-right: 5px" :src="scope.row.socialAvatar">
                  <span style="font-size: 16px;">{{ scope.row.socialNickname }}</span>
                </template>
              </el-table-column>

              <el-table-column align="right">
                <template slot-scope="scope">
                  <el-button v-if="!scope.row.id" type="text" size="mini" @click="bind(scope.row.socialType)">绑定</el-button>
                  <el-button v-else type="text" style="color: #FF6666" size="mini" @click="unbind(scope.row.id)">解绑</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>

      <el-col :span="14" :xs="24">
        <el-card>
          <div slot="header" class="clearfix">
            <span>基本资料</span>
          </div>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本资料" name="userinfo">
              <userInfo :user="user" />
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="resetPwd">
              <resetPwd :user="user" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import userAvatar from './userAvatar'
import userInfo from './userInfo'
import resetPwd from './resetPwd'
import openWindow from '@/utils/open-window'
import { getUserProfile, unbindUserSocial, bindUserSocial, getBindSocialUri } from '@/api/login'
export default {
  name: 'Profile',
  components: { userAvatar, userInfo, resetPwd },
  data() {
    return {
      socials: [],
      activeTab: 'userinfo',
      user: {},
      deptName: '',
      roles: []
    }
  },
  destroyed() {
    window.removeEventListener('storage', this.afterQRScan)
  },
  created() {
    this.getUser()
    window.addEventListener('storage', this.afterQRScan)
  },
  methods: {
    getUser() {
      getUserProfile().then(response => {
        this.user = response.data.info
        this.deptName = response.data.dept
        this.roles = response.data.roles
        this.socials = response.data.socials
      })
    },
    bind(thirdpart) {
      this.thirdpart = thirdpart
      this.socialRedirect = window.location.origin + window.location.pathname + 'auth-redirect.html'
      getBindSocialUri({ redirect_uri: this.socialRedirect, type: thirdpart }).then(res => {
        if (res.code === 0) {
          openWindow(res.data, thirdpart, 800, 540)
        }
      })
    },
    unbind(id) {
      this.$confirm('此操作将解除当前第三方账号绑定, 是否继续?', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {
        unbindUserSocial(id).then(res => {
          this.getUser()
        })
      })
    },
    afterQRScan(e) {
      if (e.key === 'x-admin-oauth-code') {
        var params = {
          type: this.thirdpart,
          code: e.newValue,
          redirect_uri: this.socialRedirect
        }
        bindUserSocial(params).then(res => {
          if (res.code === 0) {
            this.getUser()
          }
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
  .svg-icon{
    margin-right: 5px;
  }
  .row-height {
    margin: 30px 0;
  }
  .iconclass {
    height: 15px;
    width: 15px;
  }
</style>
