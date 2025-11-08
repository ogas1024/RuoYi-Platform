import request from '@/utils/request'

export function listBuildings(params) {
    return request({url: '/portal/facility/building/list', method: 'get', params})
}

export function listRooms(params) {
    return request({url: '/portal/facility/room/list', method: 'get', params})
}

export function getRoom(id) {
    return request({url: `/portal/facility/room/${id}`, method: 'get'})
}

export function getTimeline(id, params) {
    return request({url: `/portal/facility/room/${id}/timeline`, method: 'get', params})
}

export function createBooking(data) {
    return request({url: '/portal/facility/booking', method: 'post', data})
}

export function myBookings(params) {
    return request({url: '/portal/facility/booking/my/list', method: 'get', params})
}

export function updateBooking(id, data) {
    return request({url: `/portal/facility/booking/${id}`, method: 'put', data})
}

export function endEarly(id, data) {
    return request({url: `/portal/facility/booking/${id}/end-early`, method: 'put', data})
}

export function cancelBooking(id) {
    return request({url: `/portal/facility/booking/${id}`, method: 'delete'})
}

export function getBooking(id) {
    return request({url: `/portal/facility/booking/${id}`, method: 'get'})
}
