import request from '@/utils/request'

export function listNotice(query) {
    return request({url: '/portal/notice/list', method: 'get', params: query})
}

export function getNotice(id) {
    return request({url: `/portal/notice/${id}`, method: 'get'})
}

