import request from '@/utils/request'

// Building
export function buildingList(params) {
    return request({url: '/manage/facility/building/list', method: 'get', params})
}

export function buildingGet(id) {
    return request({url: `/manage/facility/building/${id}`, method: 'get'})
}

export function buildingAdd(data) {
    return request({url: '/manage/facility/building', method: 'post', data})
}

export function buildingUpdate(data) {
    return request({url: '/manage/facility/building', method: 'put', data})
}

export function buildingRemove(ids) {
    return request({url: `/manage/facility/building/${ids}`, method: 'delete'})
}

export function buildingGantt(id, params) {
    return request({url: `/manage/facility/building/${id}/gantt`, method: 'get', params})
}

// Room
export function roomList(params) {
    return request({url: '/manage/facility/room/list', method: 'get', params})
}

export function roomGet(id) {
    return request({url: `/manage/facility/room/${id}`, method: 'get'})
}

export function roomTimeline(id, params) {
    return request({url: `/manage/facility/room/${id}/timeline`, method: 'get', params})
}

export function roomAdd(data) {
    return request({url: '/manage/facility/room', method: 'post', data})
}

export function roomUpdate(data) {
    return request({url: '/manage/facility/room', method: 'put', data})
}

export function roomEnable(id, enable) {
    return request({url: `/manage/facility/room/${id}/enable`, method: 'put', params: {enable}})
}

export function roomRemove(ids) {
    return request({url: `/manage/facility/room/${ids}`, method: 'delete'})
}

// Audit
export function auditList(params) {
    return request({url: '/manage/facility/booking/audit/list', method: 'get', params})
}

export function approveBooking(id) {
    return request({url: `/manage/facility/booking/${id}/approve`, method: 'put'})
}

export function rejectBooking(id, reason) {
    return request({url: `/manage/facility/booking/${id}/reject`, method: 'put', data: {reason}})
}

export function getBooking(id) {
    return request({url: `/manage/facility/booking/${id}`, method: 'get'})
}

export function cancelBookingManage(id) {
    return request({url: `/manage/facility/booking/${id}`, method: 'delete'})
}

// Setting
export function getSetting() {
    return request({url: '/manage/facility/setting/audit-required', method: 'get'})
}

export function saveSetting(data) {
    return request({url: '/manage/facility/setting/audit-required', method: 'put', data})
}

// Ban
export function banList(params) {
    return request({url: '/manage/facility/ban/list', method: 'get', params})
}

export function banAdd(data) {
    return request({url: '/manage/facility/ban', method: 'post', data})
}

export function banRemove(id) {
    return request({url: `/manage/facility/ban/${id}`, method: 'delete'})
}

// Top
export function topRooms(params) {
    return request({url: '/manage/facility/top/room', method: 'get', params})
}

export function topUsers(params) {
    return request({url: '/manage/facility/top/user', method: 'get', params})
}
