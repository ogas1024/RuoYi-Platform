import request from '@/utils/request'

export function listResource(query) {
  return request({ url: '/manage/courseResource/list', method: 'get', params: query })
}

export function getResource(id) {
  return request({ url: `/manage/courseResource/${id}`, method: 'get' })
}

export function addResource(data) {
  return request({ url: '/manage/courseResource', method: 'post', data })
}

export function updateResource(data) {
  return request({ url: '/manage/courseResource', method: 'put', data })
}

export function delResource(ids) {
  return request({ url: `/manage/courseResource/${ids}`, method: 'delete' })
}

export function approveResource(id) {
  return request({ url: `/manage/courseResource/${id}/approve`, method: 'put' })
}

export function rejectResource(id, reason) {
  return request({ url: `/manage/courseResource/${id}/reject`, method: 'put', data: { reason } })
}

export function offlineResource(id) {
  return request({ url: `/manage/courseResource/${id}/offline`, method: 'put' })
}

export function onlineResource(id) {
  return request({ url: `/manage/courseResource/${id}/online`, method: 'put' })
}

export function topResource(query) {
  return request({ url: '/manage/courseResource/top', method: 'get', params: query })
}

export function setBestResource(id) {
  return request({ url: `/manage/courseResource/${id}/best`, method: 'put' })
}

export function unsetBestResource(id) {
  return request({ url: `/manage/courseResource/${id}/unbest`, method: 'put' })
}
