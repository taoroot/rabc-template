<template>
  <div class="app-container">
    <el-card>
      <div class="table-container">
        <el-row :gutter="20">
          <el-col :span="10">
            <el-divider>数据类型</el-divider>
            <div class="filter-container">
              <el-form :inline="true" :model="search" size="small">
                <el-form-item label="类型">
                  <el-input v-model="search.name" placeholder="请输入类型" clearable />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" icon="el-icon-search" @click="tableGetPage">查询</el-button>
                  <el-button type="primary" icon="el-icon-plus" @click="tableCreate">新增</el-button>
                </el-form-item>
              </el-form>
            </div>
            <el-table ref="table" v-loading="table.loading" :row-class-name="tableRowClassName" border :data="table.data" style="width: 100%" current-change="tableData" @row-dblclick="tableData">
              <el-table-column label="名称" align="center" header-align="center" prop="name" :show-overflow-tooltip="true" />
              <el-table-column label="类型" align="center" header-align="center" prop="type" />
              <el-table-column label="状态" align="center">
                <template slot-scope="scope">
                  <el-switch v-model="scope.row.enabled" @change="tableEnabledChange(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="120">
                <template slot-scope="scope">
                  <el-button type="text" icon="el-icon-edit" @click="tableEdit(scope.row)" />
                  <el-button type="text" icon="el-icon-delete" @click="tableDelete([scope.row.id])" />
                  <el-button type="text" icon="el-icon-s-unfold" @click="tableData(scope.row)" />
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
          </el-col>
          <el-col :span="14">
            <el-divider>数据字典</el-divider>
            <item :type="item.type" />
          </el-col>
        </el-row>
      </div>

      <el-dialog :append-to-body="true" :visible.sync="form.dialog" :title="form.data.id === undefined ? '新增' : '编辑'" width="500px">

        <el-form ref="form" :model="form.data" :rules="form.rules" size="small" label-width="100px">

          <el-form-item label="名称" prop="name">
            <el-input v-model="form.data.name" />
          </el-form-item>

          <el-form-item label="标识符" prop="type">
            <el-input v-model="form.data.type" />
          </el-form-item>

          <el-form-item label="状态" prop="enabled">
            <el-radio-group v-model="form.data.enabled">
              <el-radio :label="true">启用</el-radio>
              <el-radio :label="false">停用</el-radio>
            </el-radio-group>
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
import { deptTypePage, dictTypeAdd, dictTypeUpdate, dictTypeDel } from '@/api/sys/dict'
import item from './item'
const _defaultRow = {
  'id': undefined,
  'name': undefined,
  'type': undefined,
  'status': 0,
  'createBy': 'admin',
  'createTime': '2018-03-16T11:33:00',
  'updateBy': 'ry',
  'updateTime': '2018-03-16T11:33:00',
  'remark': undefined
}

export default {
  name: 'Dict',
  components: {
    item
  },
  props: {},
  data() {
    return {
      search: {
        name: undefined
      },
      table: {
        data: [],
        current: 1,
        size: 10,
        total: 0,
        loading: false
      },
      form: {
        data: Object.assign({}, _defaultRow),
        rules: {
          name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
          type: [{ required: true, message: '标识符不能为空', trigger: 'blur' }]
        },
        dialog: false
      },
      item: {
        type: -1
      }
    }
  },
  mounted() {
    this.tableGetPage()
  },
  methods: {
    tableRowClassName({ row, rowIndex }) {
      if (this.item.type === row.id) {
        return 'success-row'
      } else {
        return ''
      }
    },
    tableCreate() {
      this.form.dialog = true
      this.form.data = Object.assign({}, _defaultRow)
    },
    tableDelete(ids) {
      this.tableData({ id: ids[0] })
      this.$confirm('此操作将删除选中数据, 是否继续?', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {
        dictTypeDel(ids).then(response => {
          this.tableGetPage()
        })
      })
    },
    tableData(row) {
      this.item.type = row.id
    },
    tableEdit(row) {
      this.tableData(row)
      this.form.dialog = true
      this.form.data = Object.assign({}, row)
    },
    tableGetPage() {
      this.table.loading = true
      const params = {
        current: this.table.current,
        size: this.table.size
      }
      deptTypePage(params).then(response => {
        this.table.loading = false
        this.table.data = response.data.records
        this.table.total = response.data.total
        // 默认显示第一个
        this.item.type = this.table.data[0].id
      })
    },
    tableSubmit() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.form.data.id === undefined) {
            dictTypeAdd(this.form.data).then((res) => {
              this.form.dialog = false
              this.tableGetPage()
              this.$refs['form'].resetFields()
            })
          } else {
            dictTypeUpdate(this.form.data).then((res) => {
              this.form.dialog = false
              this.tableGetPage()
            })
          }
        }
      })
    },
    tableEnabledChange(row) {
      this.tableData(row)
      const text = row.enabled ? '启用' : '停用'
      this.$confirm(`确认要 ${text} ${row.name} 吗?`, '警告', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(function() {
        return dictTypeUpdate(Object.assign({}, row))
      }).then(() => {
        this.$message({ message: text + '成功', type: 'success' })
      }).catch(() => {
        row.enabled = !row.enabled
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
