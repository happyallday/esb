import request from '@/utils/request'

export interface RequestLog {
  id: number
  requestId: string
  serviceName: string
  methodType: string
  requestUrl: string
  responseStatus: number
  durationMs: number
  requestTime: string
  responseTime?: string
  errorMessage?: string
}

export interface LogQueryRequest {
  serviceName?: string
  startTime?: string
  endTime?: string
  statusCd?: number
  keyword?: string
  page: number
  size: number
}

export interface LogDetail extends RequestLog {
  requestHeaders: string
  requestParams: string
  requestBody: string
  responseHeaders: string
  responseBody: string
}

export const getLogList = (params: LogQueryRequest) => {
  return request.get<{ records: RequestLog[], total: number }>('/logs', { params })
}

export const getLogDetail = (id: number) => {
  return request.get<LogDetail>(`/logs/${id}`)
}