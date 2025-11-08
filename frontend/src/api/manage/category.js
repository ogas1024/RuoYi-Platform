import request from '@/utils/request'

// 查询图书类别列表
export function listCategory(query) {
    return request({
        url: '/manage/category/list',
        method: 'get',
        params: query
    })
}

// 查询图书类别详细
export function getCategory(id) {
    return request({
        url: '/manage/category/' + id,
        method: 'get'
    })
}

// 新增图书类别
export function addCategory(data) {
    return request({
        url: '/manage/category',
        method: 'post',
        data: data
    })
}

// 修改图书类别
export function updateCategory(data) {
    return request({
        url: '/manage/category',
        method: 'put',
        data: data
    })
}

// 删除图书类别
export function delCategory(id) {
    return request({
        url: '/manage/category/' + id,
        method: 'delete'
    })
}
