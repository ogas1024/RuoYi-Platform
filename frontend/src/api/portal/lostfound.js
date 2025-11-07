import request from '@/utils/request'

export function listLostFoundPortal(query) {
  return request({ url: '/portal/lostfound/list', method: 'get', params: query })
}

export function getLostFoundPortal(id) {
  return request({ url: `/portal/lostfound/${id}`, method: 'get' })
}

export function addLostFoundPortal(data) {
  return request({ url: '/portal/lostfound', method: 'post', data })
}

export function updateLostFoundPortal(id, data) {
  return request({ url: `/portal/lostfound/${id}`, method: 'put', data })
}

export function removeLostFoundPortal(id) {
  return request({ url: `/portal/lostfound/${id}`, method: 'delete' })
}

export function solveLostFoundPortal(id) {
  return request({ url: `/portal/lostfound/${id}/solve`, method: 'put' })
}

export function myLostFoundPortal(query) {
  return request({ url: '/portal/lostfound/my/list', method: 'get', params: query })
}

