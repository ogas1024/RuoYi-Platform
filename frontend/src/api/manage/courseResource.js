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

export function offlineResource(id, reason) {
  return request({ url: `/manage/courseResource/${id}/offline`, method: 'put', data: { reason } })
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

// 统计：课程资源上传趋势（按日），返回 [{ day: 'yyyy-MM-dd', count: number }]
export function uploadTrend(days = 30, opts = {}) {
  const headers = { ...(opts.headers || {}), silent: true }
  return request({ url: '/manage/courseResource/stats/uploadTrend', method: 'get', params: { days }, headers, ...opts })
}

// 排行：课程资源下载Top（默认Top5）
export function topResources(limit = 5, days = 30, opts = {}) {
  const headers = { ...(opts.headers || {}), silent: true }
  return request({ url: '/manage/courseResource/stats/topResources', method: 'get', params: { limit, days }, headers, ...opts })
}

// 排行：课程资源 用户积分Top（默认Top5）
export function topScoreUsers(limit = 5, opts = {}) {
  const headers = { ...(opts.headers || {}), silent: true }
  return request({ url: '/manage/courseResource/stats/topScoreUsers', method: 'get', params: { limit }, headers, ...opts })
}

// 排行：课程资源 用户下载Top（默认Top5）
export function topDownloadUsers(limit = 5, opts = {}) {
  const headers = { ...(opts.headers || {}), silent: true }
  return request({ url: '/manage/courseResource/stats/topDownloadUsers', method: 'get', params: { limit }, headers, ...opts })
}

// 占比：课程资源 专业占比（默认近30天）
export function majorShare(days = 30, opts = {}) {
  const headers = { ...(opts.headers || {}), silent: true }
  return request({ url: '/manage/courseResource/stats/majorShare', method: 'get', params: { days }, headers, ...opts })
}

// 占比：课程资源 课程占比（默认近30天）
export function courseShare(days = 30, opts = {}) {
  const headers = { ...(opts.headers || {}), silent: true }
  return request({ url: '/manage/courseResource/stats/courseShare', method: 'get', params: { days }, headers, ...opts })
}

// 统计：课程资源下载趋势（按日），返回 [{ day: 'yyyy-MM-dd', count: number }]
export function downloadTrend(days = 30, opts = {}) {
  const headers = { ...(opts.headers || {}), silent: true }
  return request({ url: '/manage/courseResource/stats/downloadTrend', method: 'get', params: { days }, headers, ...opts })
}
