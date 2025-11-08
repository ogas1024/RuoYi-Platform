import request from '@/utils/request'

export function listMajorPortal(query) {
    return request({url: '/portal/major/list', method: 'get', params: query})
}

