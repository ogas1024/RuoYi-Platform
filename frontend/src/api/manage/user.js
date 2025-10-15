import request from '@/utils/request'

// 查询用户列表
export function listUser(query) {
    return request({
        url: '/manage/user/list',
        method: 'get',
        params: query
    })
}

export function addUser(data) {
    return request({
        url: '/manage/user',
        method: 'post',
        data: data
    })
}