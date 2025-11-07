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

// ========= 门户管理：我的/编辑/删除/上下架 =========
export function myListResourcePortal(query) {
  return request({ url: '/portal/resource/my/list', method: 'get', params: query })
}

export function addResourcePortal(data) {
  return request({ url: '/portal/resource', method: 'post', data })
}

export function updateResourcePortal(data) {
  return request({ url: '/portal/resource', method: 'put', data })
}

export function removeResourcePortal(id) {
  return request({ url: `/portal/resource/${id}`, method: 'delete' })
}

export function onlineResourcePortal(id) {
  return request({ url: `/portal/resource/${id}/online`, method: 'put' })
}

export function offlineResourcePortal(id, reason) {
  return request({ url: `/portal/resource/${id}/offline`, method: 'put', data: { reason } })
}
