import request from '@/utils/request'

export function listBookPortal(query) {
  return request({ url: '/portal/library/list', method: 'get', params: query })
}

export function getBookPortal(id) {
  return request({ url: `/portal/library/${id}`, method: 'get' })
}

export function downloadBookPortal(id, assetId) {
  const url = assetId ? `/portal/library/${id}/download?assetId=${assetId}` : `/portal/library/${id}/download`
  return request({ url, method: 'get', responseType: 'blob' })
}

export function topBookPortal(limit = 10) {
  return request({ url: '/portal/library/top', method: 'get', params: { limit } })
}

export function topUsersPortal(limit = 10) {
  return request({ url: '/portal/library/top/users', method: 'get', params: { limit } })
}

export function addBookPortal(data) {
  return request({ url: '/portal/library', method: 'post', data })
}

export function updateBookPortal(data) {
  return request({ url: '/portal/library', method: 'put', data })
}

export function removeBookPortal(ids) {
  return request({ url: `/portal/library/${ids}`, method: 'delete' })
}

export function addAssetPortal(bookId, data) {
  return request({ url: `/portal/library/${bookId}/asset`, method: 'post', data })
}

export function removeAssetPortal(bookId, assetId) {
  return request({ url: `/portal/library/${bookId}/asset/${assetId}`, method: 'delete' })
}

export function favoritePortal(id, favorite) {
  return request({ url: `/portal/library/${id}/favorite`, method: 'post', data: { favorite } })
}

export function listFavoritePortal(query) {
  return request({ url: '/portal/library/favorite', method: 'get', params: query })
}
