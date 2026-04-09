<template>
  <div class="log-detail">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>日志详情</span>
          <el-button type="primary" link @click="handleBack">返回列表</el-button>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="请求ID">{{ logDetail.requestId }}</el-descriptions-item>
        <el-descriptions-item label="服务名">{{ logDetail.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ logDetail.methodType }}</el-descriptions-item>
        <el-descriptions-item label="状态码">
          <el-tag :type="getStatusType(logDetail.responseStatus)" effect="dark">
            {{ logDetail.responseStatus }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请求时间">{{ formatDateTime(logDetail.requestTime) }}</el-descriptions-item>
        <el-descriptions-item label="响应时间">{{ formatDateTime(logDetail.responseTime) }}</el-descriptions-item>
        <el-descriptions-item label="耗时">{{ logDetail.durationMs }}ms</el-descriptions-item>
        <el-descriptions-item label="请求URL">{{ logDetail.requestUrl }}</el-descriptions-item>
      </el-descriptions>

      <el-divider />

      <div class="section-title">请求头</div>
      <json-viewer :value="parseJson(logDetail.requestHeaders)" :expand-depth="2" copyable />

      <el-divider />

      <div class="section-title">请求参数</div>
      <json-viewer :value="parseJson(logDetail.requestParams)" :expand-depth="2" copyable />

      <el-divider />

      <div class="section-title">请求体</div>
      <json-viewer :value="parseJson(logDetail.requestBody)" :expand-depth="2" copyable />

      <el-divider />

      <div class="section-title">响应头</div>
      <json-viewer :value="parseJson(logDetail.responseHeaders)" :expand-depth="2" copyable />

      <el-divider />

      <div class="section-title">响应体</div>
      <json-viewer :value="parseJson(logDetail.responseBody)" :expand-depth="2" copyable />

      <el-divider v-if="logDetail.errorMessage" />

      <div class="section-title error-title" v-if="logDetail.errorMessage">错误信息</div>
      <el-alert v-if="logDetail.errorMessage" :title="logDetail.errorMessage" type="error" :closable="false" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getLogDetail, LogDetail } from '@/api/logs'
import dayjs from 'dayjs'
import JsonViewer from 'vue-json-viewer'
import 'vue-json-viewer/style.css'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const logDetail = ref<LogDetail>({
  id: 0,
  requestId: '',
  serviceName: '',
  methodType: '',
  requestUrl: '',
  responseStatus: 0,
  durationMs: 0,
  requestTime: '',
  responseTime: '',
  errorMessage: '',
  requestHeaders: '{}',
  requestParams: '{}',
  requestBody: '{}',
  responseHeaders: '{}',
  responseBody: '{}'
})

const loadLogDetail = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const data = await getLogDetail(id)
    logDetail.value = data
  } catch (error) {
    ElMessage.error('加载日志详情失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleBack = () => {
  router.back()
}

const getStatusType = (status: number) => {
  if (status >= 200 && status < 300) return 'success'
  if (status >= 400 && status < 500) return 'warning'
  if (status >= 500) return 'danger'
  return 'info'
}

const formatDateTime = (datetime: string) => {
  if (!datetime) return '-'
  return dayjs(datetime).format('YYYY-MM-DD HH:mm:ss.SSS')
}

const parseJson = (jsonString: string) => {
  try {
    return jsonString ? JSON.parse(jsonString) : {}
  } catch {
    return jsonString || {}
  }
}

onMounted(() => {
  loadLogDetail()
})
</script>

<style scoped>
.log-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #303133;
}

.error-title {
  color: #f56c6c;
}
</style>