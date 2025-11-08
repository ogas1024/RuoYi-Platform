import request from '@/utils/request'

export function listCourse(query) {
    return request({url: '/manage/course/list', method: 'get', params: query})
}

export function getCourse(id) {
    return request({url: `/manage/course/${id}`, method: 'get'})
}

export function addCourse(data) {
    return request({url: '/manage/course', method: 'post', data})
}

export function updateCourse(data) {
    return request({url: '/manage/course', method: 'put', data})
}

export function delCourse(ids) {
    return request({url: `/manage/course/${ids}`, method: 'delete'})
}

