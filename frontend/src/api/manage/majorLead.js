import request from '@/utils/request'

export function listMajorLead(query) {
  return request({ url: '/manage/majorLead/list', method: 'get', params: query })
}

export function addMajorLead(data) {
  return request({ url: '/manage/majorLead', method: 'post', data })
}

export function delMajorLead(ids) {
  return request({ url: `/manage/majorLead/${ids}`, method: 'delete' })
}

export function delMajorLeadByPair(majorId, userId) {
  return request({ url: '/manage/majorLead', method: 'delete', params: { majorId, userId } })
}

export function listMajorLeadRoleUsers(query) {
  return request({ url: '/manage/majorLead/roleUsers', method: 'get', params: query })
}

export function retireMajorLeadUser(userId) {
  return request({ url: `/manage/majorLead/retire/${userId}` , method: 'delete' })
}

export function listMyMajors() {
  return request({ url: '/manage/majorLead/myMajors', method: 'get' })
}
