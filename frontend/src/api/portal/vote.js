import request from '@/utils/request'

// 复用问卷门户接口
export function listVote(query) { return request({ url: '/portal/survey/list', method: 'get', params: query }) }
export function getVote(id) { return request({ url: `/portal/survey/${id}`, method: 'get' }) }
export function submitVote(id, answers) { return request({ url: `/portal/survey/${id}/submit`, method: 'post', data: { answers } }) }
export function myVote(query) { return request({ url: '/portal/survey/my', method: 'get', params: query }) }
