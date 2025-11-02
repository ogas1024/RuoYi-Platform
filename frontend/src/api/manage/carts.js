import request from '@/utils/request'

// 查询购物车管理列表
export function listCarts(query) {
  return request({
    url: '/manage/carts/list',
    method: 'get',
    params: query
  })
}

// 查询购物车管理详细
export function getCarts(id) {
  return request({
    url: '/manage/carts/' + id,
    method: 'get'
  })
}

// 新增购物车管理
export function addCarts(data) {
  return request({
    url: '/manage/carts',
    method: 'post',
    data: data
  })
}

// 修改购物车管理
export function updateCarts(data) {
  return request({
    url: '/manage/carts',
    method: 'put',
    data: data
  })
}

// 删除购物车管理
export function delCarts(id) {
  return request({
    url: '/manage/carts/' + id,
    method: 'delete'
  })
}
