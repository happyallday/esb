import request from '@/utils/request'

export interface MetricsResponse {
  todayTotalCalls: number
  todaySuccessCalls: number
  todayFailedCalls: number
  successRate: number
  avgResponseTime: number
  callTrend: Array<{ date: string, count: number }>
  serviceStats: Array<{ service: string, count: number }>
}

export const getMetrics = () => {
  return request.get<MetricsResponse>('/metrics')
}