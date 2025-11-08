import request from '@/utils/request'

// 复用问卷接口：命名为 vote 以便页面解耦
export function listVote(query) {
    return request({url: '/manage/survey/list', method: 'get', params: query})
}

export function getVote(id) {
    return request({url: `/manage/survey/${id}`, method: 'get'})
}

export function addVote(data) {
    return request({url: '/manage/survey', method: 'post', data})
}

export function updateVote(data) {
    return request({url: '/manage/survey', method: 'put', data})
}

export function publishVote(id) {
    return request({url: `/manage/survey/${id}/publish`, method: 'put'})
}

export function archiveVote(id) {
    return request({url: `/manage/survey/${id}/archive`, method: 'put'})
}

export function extendVote(id, deadline) {
    return request({url: `/manage/survey/${id}/extend`, method: 'put', data: {deadline}})
}

export function pinVote(id, pinned) {
    return request({url: `/manage/survey/${id}/pin`, method: 'put', data: {pinned}})
}

export function listVoteSubmittedUsers(id) {
    return request({url: `/manage/survey/${id}/submits`, method: 'get'})
}

export function getVoteUserAnswers(id, userId) {
    return request({url: `/manage/survey/${id}/answers/${userId}`, method: 'get'})
}

