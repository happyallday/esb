<template>
  <div class="log-list">
    <el-card>
      <el-form :inline="true" :model="queryForm" @submit.prevent="handleSearch">
        <el-form-item label="服务名">
          <el-input v-model="queryForm.serviceName" placeholder="请输入服务名" clearable />
        </el-form-item>
        <el-form-item label="状态码">
          <el-select v-model="queryForm.statusCd" placeholder="请选择状态码" clearable>
            <el-option label="全部" :value="null" />
            <el-option label="2xx" :value="200" />
            <el-option label="4xx" :value="400" />
            <el-option label="5xx" :value="500" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="请输入关键词" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table 
        :data="tableData" 
        v-loading="loading"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="requestId" label="请求ID" width="180" />
        <el-table-column prop="serviceName" label="服务名" width="150" />
        <el-table-column prop="methodType" label="方法" width="80" />
        <el-table-column prop="requestUrl" label="请求URL" width="200" show-overflow-tooltip />
        <el-table-column prop="responseStatus" label="状态码" width="100">
          <template #default="{ row }">
            <el-tag 
              :type="getStatusType(row.responseStatus)"
              effect="dark"
            >
              {{ row.responseStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="durationMs" label="耗时(ms)" width="120" />
        <el-table-column prop="requestTime" label="请求时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row.id)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getLogList, LogQueryRequest, RequestLog } from '@/api/logs'

const router = useRouter()
const loading = ref(false)
const tableData = ref<RequestLog[]>([])
const total = ref(0)
const dateRange = ref<[string, string] | null>(null)

const queryForm = reactive<LogQueryRequest>({
  serviceName: '',
  startTime: '',
  endTime: '',
  statusCd: null,
  keyword: '',
  page: 1,
  size: 10
})

const loadLogs = async () => {
  loading.value = true
  try {
    const response = await getLogList(queryForm)
    tableData.value = response.records
    total.value = response.total
  } catch (error) {
    ElMessage.error('加载日志失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryForm.page = 1
  loadLogs()
}

const handleReset = () => {
  queryForm.serviceName = ''
  queryForm.statusCd = null
  queryForm.keyword = ''
  dateRange.value = null
  queryForm.startTime = ''
  queryForm.endTime = ''
  handleSearch()
}

const handleDateChange = (value: [string, string] | null) => {
  if (value && value.length === 2) {
    queryForm.startTime = value[0]
    queryForm.endTime = value[1]
  } else {
    queryForm.startTime = ''
    queryForm.endTime = ''
  }
}

const handleViewDetail = (id: number) => {
  router.push(`/logs/${id}`)
}

const getStatusType = (status: number) => {
  if (status >= 200 && status < 300) return 'success'
  if (status >= 400 && status < 500) return 'warning'
  if (status >= 500) return 'danger'
  return 'info'
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.log-list {
  padding: 20px;
}

.table-card {
  margin-top: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>