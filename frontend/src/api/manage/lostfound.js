import request from '@/utils/request'

export function listLostFound(query) {
  return request({ url: '/manage/lostfound/list', method: 'get', params: query })
}

export function listAuditLostFound(query) {
  return request({ url: '/manage/lostfound/audit/list', method: 'get', params: query })
}

export function listRecycleLostFound(query) {
  return request({ url: '/manage/lostfound/recycle/list', method: 'get', params: query })
}

export function getLostFound(id) {
  return request({ url: `/manage/lostfound/${id}`, method: 'get' })
}

export function addLostFound(data) {
  return request({ url: '/manage/lostfound', method: 'post', data })
}

export function updateLostFound(data) {
  return request({ url: '/manage/lostfound', method: 'put', data })
}

export function removeLostFound(ids) {
  return request({ url: `/manage/lostfound/${ids}`, method: 'delete' })
}

export function approveLostFound(id) {
  return request({ url: `/manage/lostfound/${id}/approve`, method: 'put' })
}

export function rejectLostFound(id, reason) {
  return request({ url: `/manage/lostfound/${id}/reject`, method: 'put', data: { reason } })
}

export function offlineLostFound(id, reason) {
  return request({ url: `/manage/lostfound/${id}/offline`, method: 'put', data: { reason } })
}

export function restoreLostFound(id) {
  return request({ url: `/manage/lostfound/${id}/restore`, method: 'put' })
}

export function setSolvedLostFound(id, solved) {
  return request({ url: `/manage/lostfound/${id}/solve`, method: 'put', data: { solved } })
}
