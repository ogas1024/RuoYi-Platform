import request from '@/utils/request'

export function listBook(query) {
    return request({url: '/manage/book/list', method: 'get', params: query})
}

export function getBook(id) {
    return request({url: `/manage/book/${id}`, method: 'get'})
}

export function addBook(data) {
    return request({url: '/manage/book', method: 'post', data})
}

export function updateBook(data) {
    return request({url: '/manage/book', method: 'put', data})
}

export function delBook(ids) {
    return request({url: `/manage/book/${ids}`, method: 'delete'})
}

export function approveBook(id) {
    return request({url: `/manage/book/${id}/approve`, method: 'put'})
}

export function rejectBook(id, reason) {
    return request({url: `/manage/book/${id}/reject`, method: 'put', data: {reason}})
}

export function offlineBook(id) {
    return request({url: `/manage/book/${id}/offline`, method: 'put'})
}

export function onlineBook(id) {
    return request({url: `/manage/book/${id}/online`, method: 'put'})
}
