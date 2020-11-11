<template>
  <div class="app-container">
    <div class="filter-container">
      <el-form size="small">
        <el-form-item>
          <el-button type="primary" icon="el-icon-plus" @click="tableCreate">新增</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table ref="table" v-loading="table.loading" border :data="table.data" style="width: 100%">
        <el-table-column label="Value" align="center" header-align="center" prop="value" />
        <el-table-column label="Label" align="center" header-align="center" prop="label" />
        <el-table-column label="排序" align="center" header-align="center" prop="sort" :show-overflow-tooltip="true" />
        <el-table-column label="是否默认" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isDefault">是</el-tag>
            <el-tag v-else type="warning">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.enabled" @change="tableEnabledChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="100">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-edit" @click="tableEdit(scope.row)" />
            <el-button type="text" icon="el-icon-delete" @click="tableDelete([scope.row.id])" />
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

        <el-form-item label="Value" prop="value">
          <el-input v-model="form.data.value" />
        </el-form-item>

        <el-form-item label="Label" prop="label">
          <el-input v-model="form.data.label" />
        </el-form-item>

        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.data.sort" />
        </el-form-item>

        <el-form-item label="是否默认" prop="isDefault">
          <el-radio-group v-model="form.data.isDefault">
            <el-radio :label="true">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
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
  </div>
</template>

<script>
import { deptDataPage, dictDataAdd, dictDataUpdate, dictDataDel } from '@/api/sys/dict'
const _defaultRow = {
  'id': undefined,
  'value': undefined,
  'label': undefined,
  'sort': undefined,
  'isDefault': false,
  'status': 0,
  'remark': undefined
}

export default {
  name: 'DictItem',
  props: {
    type: {
      default: -1,
      type: Number
    }
  },
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
        rules: {
          value: [{ required: true, message: '不能为空', trigger: 'blur' }],
          label: [{ required: true, message: '不能为空', trigger: 'blur' }],
          sort: [{ required: true, message: '不能为空', trigger: 'blur' }]
        },
        dialog: false
      }
    }
  },
  watch: {
    type(val) {
      this.tableGetPage()
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
    tableDelete(ids) {
      this.$confirm('此操作将删除选中数据, 是否继续?', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {
        dictDataDel(ids).then(response => {
          this.tableGetPage()
        })
      })
    },
    tableEdit(row) {
      this.form.dialog = true
      this.form.data = Object.assign({}, row)
      console.log(this.form.data)
    },
    tableGetPage() {
      this.table.loading = true
      const params = {
        current: this.table.current,
        size: this.table.size,
        type: this.type
      }
      deptDataPage(params).then(response => {
        this.table.loading = false
        this.table.data = response.data.records
        this.table.total = response.data.total
      })
    },
    tableSubmit() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.form.data.id === undefined) {
            this.form.data.typeId = this.type
            dictDataAdd(this.form.data).then((res) => {
              this.form.dialog = false
              this.tableGetPage()
              this.$refs['form'].resetFields()
            })
          } else {
            dictDataUpdate(this.form.data).then((res) => {
              this.form.dialog = false
              this.tableGetPage()
            })
          }
        }
      })
    },
    tableEnabledChange(row) {
      const text = row.enabled ? '启用' : '停用'
      this.$confirm(`确认要 ${text} ${row.label} ?`, '警告', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(function() {
        return dictDataUpdate(Object.assign({}, row))
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
.app-container {
  padding: 0;
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
