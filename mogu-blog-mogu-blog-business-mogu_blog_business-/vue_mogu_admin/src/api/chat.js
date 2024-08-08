import request from '@/utils/request'

export function getImMessageList(params) {
  return request({
    url: process.env.ADMIN_API + '/imMessage/getList',
    method: 'post',
    data: params
  })
}

export function deleteBatchImMessage(params) {
  return request({
    url: process.env.ADMIN_API + '/imMessage/deleteBatch',
    method: 'post',
    data: params
  })
}

export function addRoom(params) {
    return request({
        url: process.env.ADMIN_API + '/room/add',
        method: 'post',
        data: params
    })
}

export function editRoom(params) {
    return request({
        url: process.env.ADMIN_API + '/room/edit',
        method: 'post',
        data: params
    })
}

export function getRoomList(params) {
  return request({
    url: process.env.ADMIN_API + '/room/getList',
    method: 'post',
    data: params
  })
}

export function deleteBatchRoom(params) {
  return request({
    url: process.env.ADMIN_API + '/room/deleteBatch',
    method: 'post',
    data: params
  })
}
