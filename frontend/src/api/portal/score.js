import request from '@/utils/request'

// 用户积分排行榜（门户）
export function listScoreRankPortal(query) {
  return request({ url: '/portal/score/rank', method: 'get', params: query })
}

