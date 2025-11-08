import request from '@/utils/request'

export function listCoursePortal(query) {
    return request({url: '/portal/course/list', method: 'get', params: query})
}

