<template>
  <div class="interface-test">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>接口测试</span>
              <el-tag type="success" v-if="lastResponseTime" size="small">
                耗时: {{ lastResponseTime }}ms
              </el-tag>
            </div>
          </template>

          <el-form :model="testForm" label-width="100px" :rules="rules" ref="formRef">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="服务名称" prop="service">
                  <el-select 
                    v-model="testForm.service" 
                    placeholder="请选择服务"
                    @change="handleServiceChange"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="service in availableServices"
                      :key="service.serviceName"
                      :label="service.serviceName"
                      :value="service.serviceName"
                    >
                      <span>{{ service.serviceName }}</span>
                      <span style="float: right; color: #8492a6; font-size: 13px">
                        {{ service.serviceType }} - {{ service.targetUrl }}
                      </span>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="请求方法" prop="method">
                  <el-select v-model="testForm.method" placeholder="请选择方法" style="width: 100%">
                    <el-option label="GET" value="GET" />
                    <el-option label="POST" value="POST" />
                    <el-option label="PUT" value="PUT" />
                    <el-option label="DELETE" value="DELETE" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="请求路径" prop="path">
              <el-input 
                v-model="testForm.path" 
                placeholder="例如: /api/users"
                @keyup.enter="handleTest"
              >
                <template #prepend>/</template>
              </el-input>
            </el-form-item>

            <el-form-item label="请求参数" v-if="testForm.method === 'GET'">
              <el-input
                v-model="testForm.params"
                type="textarea"
                :rows="3"
                placeholder='请输入JSON格式的请求参数，例如: {"page": 1, "size": 10}'
              />
            </el-form-item>

            <el-form-item label="请求头" prop="headers">
              <el-input
                v-model="testForm.headers"
                type="textarea"
                :rows="3"
                placeholder='请输入JSON格式的请求头，例如: {"Authorization": "Bearer token"}'
              />
            </el-form-item>

            <el-form-item 
              label="请求体" 
              v-if="testForm.method === 'POST' || testForm.method === 'PUT'"
              prop="body"
            >
              <el-input
                v-model="testForm.body"
                type="textarea"
                :rows="6"
                placeholder='请输入JSON格式的请求体，例如: {"username": "admin", "password": "123456"}'
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleTest" :loading="loading">
                发送请求
              </el-button>
              <el-button @click="handleReset">重置</el-button>
              <el-button @click="handleExample">加载示例</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card v-if="response" class="response-card">
          <template #header>
            <div class="card-header">
              <span>响应结果</span>
              <el-tag 
                :type="response.statusCode >= 200 && response.statusCode < 300 ? 'success' : 'danger'" 
                effect="dark"
              >
                HTTP {{ response.statusCode }}
              </el-tag>
            </div>
          </template>

          <el-tabs v-model="activeTab">
            <el-tab-pane label="响应体" name="body">
              <div class="response-content">
                <json-viewer :value="parseJson(response.body)" :expand-depth="3" copyable />
              </div>
            </el-tab-pane>

            <el-tab-pane label="响应头" name="headers">
              <div class="response-content">
                <json-viewer :value="parseJson(response.headers)" :expand-depth="1" copyable />
              </div>
            </el-tab-pane>

            <el-tab-pane label="错误信息" name="error" v-if="response.error">
              <el-alert :title="response.error" type="error" :closable="false" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { testInterface, getServices } from '@/api/test'
import JsonViewer from 'vue-json-viewer'
import 'vue-json-viewer/style.css'
import type { ProxyResponse, ServiceConfig } from '@/api/test'

const formRef = ref<FormInstance>()
const loading = ref(false)
const response = ref<ProxyResponse | null>(null)
const lastResponseTime = ref<number>()
const activeTab = ref('body')
const availableServices = ref<ServiceConfig[]>([])

const testForm = reactive({
  service: '',
  method: 'GET',
  path: '',
  params: '',
  headers: '{}',
  body: '{}'
})

const rules: FormRules = {
  service: [{ required: true, message: '请选择服务', trigger: 'change' }],
  method: [{ required: true, message: '请选择请求方法', trigger: 'change' }],
  path: [{ required: true, message: '请输入请求路径', trigger: 'blur' }]
}

const loadServices = async () => {
  try {
    const data = await getServices()
    availableServices.value = data
    if (data.length > 0) {
      testForm.service = data[0].serviceName
    }
  } catch (error) {
    ElMessage.error('加载服务列表失败')
  }
}

const handleTest = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    response.value = null
    lastResponseTime.value = undefined
    
    try {
      const startTime = Date.now()
      const result = await testInterface(testForm)
      const duration = Date.now() - startTime
      
      response.value = result
      lastResponseTime.value = duration
      
      if (result.statusCode >= 200 && result.statusCode < 300) {
        ElMessage.success(`请求成功，耗时 ${duration}ms`)
      } else {
        ElMessage.warning(`请求完成，状态码: ${result.statusCode}`)
      }
    } catch (error: any) {
      ElMessage.error('请求失败: ' + (error.message || '未知错误'))
    } finally {
      loading.value = false
    }
  })
}

const handleReset = () => {
  formRef.value?.resetFields()
  testForm.params = ''
  testForm.body = '{}'
  testForm.headers = '{}'
  response.value = null
  lastResponseTime.value = undefined
}

const handleExample = () => {
  testForm.path = 'api/users'
  testForm.params = JSON.stringify({ page: 1, size: 10 }, null, 2)
  testForm.headers = JSON.stringify({ Authorization: 'Bearer demo-token' }, null, 2)
  testForm.body = JSON.stringify({}, null, 2)
  ElMessage.info('已加载示例数据')
}

const handleServiceChange = () => {
  response.value = null
}

const parseJson = (jsonString: string) => {
  try {
    return jsonString ? JSON.parse(jsonString) : {}
  } catch {
    return jsonString || {}
  }
}

onMounted(() => {
  loadServices()
})
</script>

<style scoped>
.interface-test {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.response-card {
  margin-top: 20px;
}

.response-content {
  max-height: 500px;
  overflow-y: auto;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}
</style>