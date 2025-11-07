import request from '@/utils/request'

export function listSurvey(query) {
  return request({ url: '/portal/survey/list', method: 'get', params: query })
}

export function getSurvey(id) {
  return request({ url: `/portal/survey/${id}`, method: 'get' })
}

export function submitSurvey(id, answers) {
  return request({ url: `/portal/survey/${id}/submit`, method: 'post', data: { answers } })
}

export function mySurvey() {
  return request({ url: '/portal/survey/my', method: 'get' })
}

