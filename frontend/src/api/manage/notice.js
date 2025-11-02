import request from '@/utils/request'

export function listNotice(query) {
  return request({ url: '/manage/notice/list', method: 'get', params: query })
}

export function getNotice(id) {
  return request({ url: `/manage/notice/${id}`, method: 'get' })
}

export function addNotice(data) {
  return request({ url: '/manage/notice', method: 'post', data })
}

export function updateNotice(data) {
  return request({ url: '/manage/notice', method: 'put', data })
}

export function delNotice(ids) {
  return request({ url: `/manage/notice/${ids}`, method: 'delete' })
}

export function publishNotice(id) {
  return request({ url: `/manage/notice/${id}/publish`, method: 'put' })
}

export function retractNotice(id) {
  return request({ url: `/manage/notice/${id}/retract`, method: 'put' })
}

export function pinNotice(id, pinned) {
  return request({ url: `/manage/notice/${id}/pin`, method: 'put', data: { pinned } })
}

