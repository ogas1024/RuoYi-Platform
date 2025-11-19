import request from '@/utils/request'

// 查询教学记录列表
export function listTeach(query) {
  return request({
    url: '/manage/teach/list',
    method: 'get',
    params: query
  })
}

// 删除教学记录
export function delTeach(id) {
  return request({
    url: `/manage/teach/${id}`,
    method: 'delete'
  })
}

