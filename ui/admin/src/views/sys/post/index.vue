<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-button type="text" icon="el-icon-plus" @click="tableCreate">新增</el-button>
      </div>

      <div class="table-container">
        <el-table ref="table" v-loading="table.loading" border :data="table.data" style="width: 100%">
          <el-table-column label="名称" align="center" header-align="center" prop="name" :show-overflow-tooltip="true" />
          <el-table-column label="角色" align="center" header-align="center" prop="role" :show-overflow-tooltip="true" />
          <el-table-column label="状态" align="center">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.enabled" @change="tableEnabledChange(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="排序" align="center" header-align="center" prop="sort" :show-overflow-tooltip="true" />
          <el-table-column label="操作" align="center" width="285">
            <template slot-scope="scope">
              <el-button type="text" icon="el-icon-edit" @click="tableEdit(scope.row)">编辑</el-button>
              <el-button type="text" icon="el-icon-delete" @click="tableDelete(scope.row)">删除</el-button>
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

          <el-form-item label="名称" prop="name">
            <el-input v-model="form.data.name" />
          </el-form-item>

          <el-form-item label="标识符" prop="role">
            <el-input v-model="form.data.post" />
          </el-form-item>

          <el-form-item label="排序" prop="desc">
            <el-input v-model="form.data.sort" />
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
import { postPage, postDel, postAdd, postUpdate } from '@/api/sys/post'
const _defaultRow = {
  id: 0,
  name: '',
  post: '',
  sort: '',
  enabled: true,
  description: ''
}

export default {
  name: 'Post',
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
      authority: {
        checkAll: false,
        data: []
      },
      dept: {
        checkAll: false,
        data: []
      },
      dict: {
        scopeType: []
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
        postDel({ id: row.id }).then(response => {
          this.tableGetPage()
        })
      })
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
      postPage(params).then(response => {
        this.table.loading = false
        this.table.data = response.data.records
        this.table.total = response.data.total
      })
    },
    tableSubmit() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.form.data.id === undefined) {
            postAdd(this.form.data).then((res) => {
              this.form.dialog = false
              this.tableGetPage()
              this.$refs['form'].resetFields()
            })
          } else {
            postUpdate(this.form.data).then((res) => {
              this.form.dialog = false
              this.tableGetPage()
            })
          }
        }
      })
    },
    tableEnabledChange(row) {
      const text = row.enabled ? '启用' : '停用'
      this.$confirm(`确认要 ${text} ${row.username} 用户吗?`, '警告', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(function() {
        return postAdd(row.id, row.enabled)
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
