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

// 统计：数字图书上传趋势（按日）
export function uploadTrend(days = 30, opts = {}) {
  const headers = { ...(opts.headers || {}), silent: true }
  return request({ url: '/manage/library/stats/uploadTrend', method: 'get', params: { days }, headers, ...opts })
}

// 统计：数字图书下载趋势（按日）
export function downloadTrend(days = 30, opts = {}) {
  const headers = { ...(opts.headers || {}), silent: true }
  return request({ url: '/manage/library/stats/downloadTrend', method: 'get', params: { days }, headers, ...opts }) }

// 排行：数字图书下载榜（TopN）
export function topBooks(limit = 5, opts = {}) {
  const headers = { ...(opts.headers || {}), silent: true }
  return request({ url: '/manage/library/stats/topBooks', method: 'get', params: { limit }, headers, ...opts })
}

// 排行：用户下载榜（TopN）
export function topDownloadUsers(limit = 5, opts = {}) {
  const headers = { ...(opts.headers || {}), silent: true }
  return request({ url: '/manage/library/stats/topDownloadUsers', method: 'get', params: { limit }, headers, ...opts })
}

// 排行：用户上传Top（TopN，按通过数量）
export function topUploadUsers(limit = 5, opts = {}) {
  const headers = { ...(opts.headers || {}), silent: true }
  return request({ url: '/manage/library/stats/topUploadUsers', method: 'get', params: { limit }, headers, ...opts })
}
