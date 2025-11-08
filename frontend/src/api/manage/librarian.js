import request from '@/utils/request'

export function listLibrarian(query) {
    return request({url: '/manage/library/librarian/list', method: 'get', params: query})
}

export function addLibrarian(userId) {
    return request({url: '/manage/library/librarian', method: 'post', data: {userId}})
}

export function removeLibrarian(userIds) {
    return request({url: `/manage/library/librarian/${userIds}`, method: 'delete'})
}
