import request from '@/utils/request'

// 查询商铺信息列表
export function listShops(query) {
    return request({
        url: '/manage/shops/list',
        method: 'get',
        params: query
    })
}

// 查询商铺信息详细
export function getShops(id) {
    return request({
        url: '/manage/shops/' + id,
        method: 'get'
    })
}

// 新增商铺信息
export function addShops(data) {
    return request({
        url: '/manage/shops',
        method: 'post',
        data: data
    })
}

// 修改商铺信息
export function updateShops(data) {
    return request({
        url: '/manage/shops',
        method: 'put',
        data: data
    })
}

// 删除商铺信息
export function delShops(id) {
    return request({
        url: '/manage/shops/' + id,
        method: 'delete'
    })
}

