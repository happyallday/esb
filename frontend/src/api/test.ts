import request from '@/utils/request'

export interface ProxyRequest {
  service: string
  method: string
  path: string
  params?: string
  headers?: string
  body?: string
}

export interface ProxyResponse {
  statusCode: number
  headers: string
  body: string
  error?: string
}

export interface ServiceConfig {
  id: number
  serviceName: string
  serviceType: string
  targetUrl: string
  timeout: number
  rateLimit: number
  circuitOpenThreshold: number
  status: number
}

export const testInterface = (data: ProxyRequest) => {
  return request.post<ProxyResponse>('/proxy/request', data)
}

export const getServices = () => {
  return request.get<ServiceConfig[]>('/services/active')
}

export const getServiceDetail = (serviceName: string) => {
  return request.get<ServiceConfig>(`/services/${serviceName}`)
}