import request from '@/utils/request'

export function listMajor(query) {
    return request({url: '/manage/major/list', method: 'get', params: query})
}

export function getMajor(id) {
    return request({url: `/manage/major/${id}`, method: 'get'})
}

export function addMajor(data) {
    return request({url: '/manage/major', method: 'post', data})
}

export function updateMajor(data) {
    return request({url: '/manage/major', method: 'put', data})
}

export function delMajor(ids) {
    return request({url: `/manage/major/${ids}`, method: 'delete'})
}

