import request from '@/utils/request'

// 列表/详情/下载
export function listLibraryPortal(query) {
    return request({url: '/portal/library/list', method: 'get', params: query})
}

export function getLibraryPortal(id) {
    return request({url: `/portal/library/${id}`, method: 'get'})
}

export function downloadLibraryPortal(id, assetId) {
    const url = assetId ? `/portal/library/${id}/download?assetId=${assetId}` : `/portal/library/${id}/download`
    return request({url, method: 'get', responseType: 'blob'})
}

// 榜单
export function topLibraryPortal(limit = 10) {
    return request({url: '/portal/library/top', method: 'get', params: {limit}})
}

export function topUsersLibraryPortal(limit = 10) {
    return request({url: '/portal/library/top/users', method: 'get', params: {limit}})
}

// 新增/编辑/删除
export function addLibraryPortal(data) {
    return request({url: '/portal/library', method: 'post', data})
}

export function addLibraryPortalFull(data) {
    return request({url: '/portal/library/full', method: 'post', data})
}

export function updateLibraryPortal(data) {
    return request({url: '/portal/library', method: 'put', data})
}

export function removeLibraryPortal(ids) {
    return request({url: `/portal/library/${ids}`, method: 'delete'})
}

// 资产增删
export function addAssetLibraryPortal(bookId, data) {
    return request({url: `/portal/library/${bookId}/asset`, method: 'post', data})
}

export function removeAssetLibraryPortal(bookId, assetId) {
    return request({url: `/portal/library/${bookId}/asset/${assetId}`, method: 'delete'})
}

// 收藏
export function favoriteLibraryPortal(id, favorite) {
    return request({url: `/portal/library/${id}/favorite`, method: 'post', data: {favorite}})
}

export function listFavoriteLibraryPortal(query) {
    return request({url: '/portal/library/favorite', method: 'get', params: query})
}
