import request from '@/utils/request'

export function listSurvey(query) {
    return request({url: '/manage/survey/list', method: 'get', params: query})
}

export function getSurvey(id) {
    return request({url: `/manage/survey/${id}`, method: 'get'})
}

export function addSurvey(data) {
    return request({url: '/manage/survey', method: 'post', data})
}

export function archiveSurvey(id) {
    return request({url: `/manage/survey/${id}/archive`, method: 'put'})
}

export function extendSurvey(id, deadline) {
    return request({url: `/manage/survey/${id}/extend`, method: 'put', data: {deadline}})
}

export function listSubmittedUsers(id) {
    return request({url: `/manage/survey/${id}/submits`, method: 'get'})
}

export function getUserAnswers(id, userId) {
    return request({url: `/manage/survey/${id}/answers/${userId}`, method: 'get'})
}

export function pinSurvey(id, pinned) {
    return request({url: `/manage/survey/${id}/pin`, method: 'put', data: {pinned}})
}

export function updateSurvey(data) {
    return request({url: '/manage/survey', method: 'put', data})
}

export function publishSurvey(id) {
    return request({url: `/manage/survey/${id}/publish`, method: 'put'})
}

export function aiSummary(id, extraPrompt) {
    return request({url: `/manage/survey/${id}/ai-summary`, method: 'post', data: {extraPrompt}})
}
