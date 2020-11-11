<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-button type="text" icon="el-icon-refresh" @click="tableGetPage">刷新</el-button>
      </div>

      <div class="table-container">
        <el-table ref="table" v-loading="table.loading" size="mini" border :data="table.data" style="width: 100%">
          <el-table-column label="操作模块" align="center" header-align="center" prop="title" />
          <el-table-column label="请求方法" align="center" header-align="center" prop="method" :show-overflow-tooltip="true" />
          <el-table-column label="请求方式" align="center" header-align="center" prop="requestMethod" :show-overflow-tooltip="true" />
          <el-table-column label="操作类别" align="center" header-align="center" prop="operatorType" :show-overflow-tooltip="true" />
          <el-table-column label="操作人员" align="center" header-align="center" prop="userName" :show-overflow-tooltip="true" />
          <el-table-column label="部门" align="center" header-align="center" prop="deptName" :show-overflow-tooltip="true" />
          <el-table-column label="URL" align="center" header-align="center" prop="url" :show-overflow-tooltip="true" />
          <el-table-column label="IP" align="center" header-align="center" prop="ip" :show-overflow-tooltip="true" />
          <el-table-column label="请求参数" align="center" header-align="center" prop="param" :show-overflow-tooltip="true" />
          <el-table-column label="返回参数" align="center" header-align="center" prop="result" :show-overflow-tooltip="true" />
          <el-table-column label="操作状态" align="center" header-align="center" prop="status" :show-overflow-tooltip="true" />
          <el-table-column label="错误消息" align="center" header-align="center" prop="error" :show-overflow-tooltip="true" />
          <el-table-column label="操作时间" align="center" header-align="center" prop="time" :show-overflow-tooltip="true" />
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <!-- <el-button type="text" icon="el-icon-edit" @click="tableEdit(scope.row)" /> -->
              <el-button type="text" icon="el-icon-delete" @click="tableDelete(scope.row)" />
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-container">
          <el-pagination
            :current-page.sync="table.current"
            :page-size="table.size"
            :total="table.total"
            layout="total, prev, pager, next"
            @current-change="tableGetPage()"
          />
        </div>
      </div>

      <el-dialog :append-to-body="true" :visible.sync="form.dialog" :title="form.data.id === undefined ? '新增' : '编辑'" width="500px">

        <el-form ref="form" :model="form.data" :rules="form.rules" size="small" label-width="100px">

          <el-form-item label="名称" prop="dictName">
            <el-input v-model="form.data.dictName" />
          </el-form-item>

          <el-form-item label="标识符" prop="dictType">
            <el-input v-model="form.data.dictType" />
          </el-form-item>

          <el-form-item label="描述" prop="remark">
            <el-input v-model="form.data.remark" />
          </el-form-item>
        </el-form>

        <div slot="footer" class="dialog-footer">
          <el-button type="text" @click="form.dialog = false">取消</el-button>
          <el-button :loading="table.loading" type="primary" @click="tableSubmit">确认</el-button>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { getPage } from '@/api/sys/log'
const _defaultRow = {
  'dictId': -1,
  'dictName': null,
  'dictType': null,
  'status': '0',
  'createBy': 'admin',
  'createTime': '2018-03-16T11:33:00',
  'updateBy': 'ry',
  'updateTime': '2018-03-16T11:33:00',
  'remark': null
}

export default {
  name: 'Dict',
  props: {},
  data() {
    return {
      table: {
        data: [],
        current: 1,
        size: 10,
        total: 0,
        loading: false
      },
      form: {
        data: Object.assign({}, _defaultRow),
        rules: {},
        dialog: false
      },
      item: {
        dialog: false,
        type: -1
      }
    }
  },
  mounted() {
    this.tableGetPage()
  },
  methods: {
    tableCreate() {
      this.form.dialog = true
      this.form.data = Object.assign({}, _defaultRow)
    },
    tableDelete(row) {
      this.$confirm('此操作将删除选中数据, 是否继续?', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {
        // delItem({ id: row.id }).then(response => {
        //   this.tableGetPage()
        // })
      })
    },
    tableData(row) {
      this.item.type = row.dictType
      this.item.dialog = true
    },
    tableEdit(row) {
      this.form.dialog = true
      this.form.data = Object.assign({}, row)
      this.authority.checkAll = false
      this.dept.checkAll = false
    },
    tableGetPage() {
      this.table.loading = true
      const params = {
        current: this.table.current,
        size: this.table.size
      }
      getPage(params).then(response => {
        this.table.loading = false
        this.table.data = response.data.records
        this.table.total = response.data.total
      })
    },
    tableSubmit() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          console.log(valid)
        }
      })
    }
  }
}
</script>

<style scoped>
.filter-container {
  margin-top: 6px;
}
.tools-container {
  margin-top: 5px;
  margin-bottom: 15px;
}
.pagination-container {
  position: relative;
  float: right;
  margin-top: 10px;
}
</style>
