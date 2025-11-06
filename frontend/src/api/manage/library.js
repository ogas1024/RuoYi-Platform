import request from '@/utils/request'

export function listLibrary(query) {
  return request({ url: '/manage/library/list', method: 'get', params: query })
}

export function getLibrary(id) {
  return request({ url: `/manage/library/${id}`, method: 'get' })
}

export function addLibrary(data) {
  return request({ url: '/manage/library', method: 'post', data })
}

export function updateLibrary(data) {
  return request({ url: '/manage/library', method: 'put', data })
}

export function delLibrary(ids) {
  return request({ url: `/manage/library/${ids}`, method: 'delete' })
}

export function approveLibrary(id) {
  return request({ url: `/manage/library/${id}/approve`, method: 'put' })
}

export function rejectLibrary(id, reason) {
  return request({ url: `/manage/library/${id}/reject`, method: 'put', data: { reason } })
}

export function offlineLibrary(id, reason) {
  return request({ url: `/manage/library/${id}/offline`, method: 'put', data: { reason } })
}

export function onlineLibrary(id) {
  return request({ url: `/manage/library/${id}/online`, method: 'put' })
}

export function listLibraryAssets(id) {
  return request({ url: `/manage/library/${id}/assets`, method: 'get' })
}
