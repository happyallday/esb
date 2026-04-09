<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">今日调用总数</div>
            <div class="stat-value">{{ metrics.todayTotalCalls || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card success">
          <div class="stat-content">
            <div class="stat-label">成功调用</div>
            <div class="stat-value">{{ metrics.todaySuccessCalls || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card error">
          <div class="stat-content">
            <div class="stat-label">失败调用</div>
            <div class="stat-value">{{ metrics.todayFailedCalls || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card info">
          <div class="stat-content">
            <div class="stat-label">成功率</div>
            <div class="stat-value">{{ (metrics.successRate || 0).toFixed(2) }}%</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>调用趋势 (最近7天)</span>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>服务统计</span>
            </div>
          </template>
          <div ref="serviceChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getMetrics, MetricsResponse } from '@/api/metrics'

const metrics = ref<MetricsResponse>({
  todayTotalCalls: 0,
  todaySuccessCalls: 0,
  todayFailedCalls: 0,
  successRate: 0,
  avgResponseTime: 0,
  callTrend: [],
  serviceStats: []
})

const trendChartRef = ref<HTMLDivElement>()
const serviceChartRef = ref<HTMLDivElement>()
let trendChart: echarts.ECharts | null = null
let serviceChart: echarts.ECharts | null = null

const loadMetrics = async () => {
  try {
    const data = await getMetrics()
    metrics.value = data
    renderCharts()
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const renderCharts = () => {
  renderTrendChart()
  renderServiceChart()
}

const renderTrendChart = () => {
  if (!trendChartRef.value) return
  
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: metrics.value.callTrend?.map(item => item.date) || []
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: metrics.value.callTrend?.map(item => item.count) || [],
      type: 'line',
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 1, color: '#188df0' }
        ])
      },
      itemStyle: {
        color: '#188df0'
      }
    }]
  }
  
  trendChart.setOption(option)
}

const renderServiceChart = () => {
  if (!serviceChartRef.value) return
  
  if (!serviceChart) {
    serviceChart = echarts.init(serviceChartRef.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}: {c}'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      data: metrics.value.serviceStats?.map(item => ({
        value: item.count,
        name: item.service
      })) || []
    }]
  }
  
  serviceChart.setOption(option)
}

onMounted(() => {
  loadMetrics()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (trendChart) {
    trendChart.dispose()
    trendChart = null
  }
  if (serviceChart) {
    serviceChart.dispose()
    serviceChart = null
  }
  window.removeEventListener('resize', handleResize)
})

const handleResize = () => {
  trendChart?.resize()
  serviceChart?.resize()
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-card.success .stat-value {
  color: #67c23a;
}

.stat-card.error .stat-value {
  color: #f56c6c;
}

.stat-card.info .stat-value {
  color: #409eff;
}

.stat-content {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.charts-row {
  margin-top: 20px;
}

.chart-container {
  height: 400px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
}
</style>