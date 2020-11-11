<template>
  <div class="app-container">
    <div class="filter-container">
      <el-form :inline="true" :model="search">
        <el-form-item label="数据源">
          <el-select v-model="currentDb" placeholder="请选择库">
            <el-option
              v-for="item in dbList"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数据表">
          <el-select v-model="currentTable" placeholder="请选择表">
            <el-option
              v-for="item in tableList"
              :key="item.tableName"
              :label="item.tableName"
              :value="item.tableName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="模板源">
          <el-select v-model="currentVm" placeholder="请选择表" @change="currentVmChange">
            <el-option
              v-for="(value, key) in vm"
              :key="key"
              :label="key"
              :value="key"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="表别名(user -> sys_user)">
          <el-input v-model="tableAlias" placeholder="自定义前缀,可空" />
        </el-form-item>
      </el-form>
    </div>
    <div class="editor-container" :loading="loading">
      <markdown-editor v-model="content" height="700px" :options="{hideModeSwitch:true, previewStyle:'tab'}" />
    </div>
  </div>
</template>

<script>
import MarkdownEditor from '@/components/MarkdownEditor'
import { codeGenApi, codeTableApi, codeDbApi } from '@/api/sys/code'
export default {
  name: 'MarkdownDemo',
  components: { MarkdownEditor },
  data() {
    return {
      content: '',
      currentDb: undefined,
      dbList: [],
      currentTable: '',
      tableList: [],
      vm: {},
      currentVm: '',
      search: {},
      tableAlias: undefined,
      loading: false
    }
  },
  watch: {
    currentTable(tableName) {
      this.genCode()
    },
    currentDb(ds) {
      codeTableApi({ ds }).then(res => {
        this.tableList = res.data
        this.currentTable = this.tableList[0].tableName
      })
    },
    tableAlias(val) {
      if (val.split('_').length > 1 && val.split('_')[1]) {
        this.genCode()
      }
    }
  },
  mounted() {
    codeDbApi().then(res => {
      this.dbList = res.data
      this.currentDb = this.dbList[0]
    })
  },
  methods: {
    currentVmChange(key) {
      this.content = this.vm[key]
    },
    genCode() {
      console.log(this.currentDb, this.currentTable, this.tableAlias)
      this.loading = true
      codeGenApi(this.currentDb, this.currentTable, this.tableAlias).then(res => {
        this.vm = res.data
        if (this.currentVm) {
          this.content = this.vm[this.currentVm]
        } else {
          this.currentVm = Object.keys(this.vm)[0]
          this.content = this.vm[ this.currentVm]
        }

        this.loading = false
      })
    }
  }
}
</script>

<style scoped>
.app-container {
  position: absolute;
  top: 50px;
  left: 0px;
  right: 0px;
  bottom: 0px;
}
.editor-container{
  margin-bottom: 30px;
}
.tag-title{
  margin-bottom: 5px;
}
</style>
