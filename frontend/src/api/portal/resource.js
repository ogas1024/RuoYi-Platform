import request from '@/utils/request'

export function listResourcePortal(query) {
  return request({ url: '/portal/resource/list', method: 'get', params: query })
}

export function getResourcePortal(id) {
  return request({ url: `/portal/resource/${id}`, method: 'get' })
}

export function topResourcePortal(query) {
  return request({ url: '/portal/resource/top', method: 'get', params: query })
}
