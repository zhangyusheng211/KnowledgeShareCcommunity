import request from '@/utils/request'

export function newRoom(params) {
  return request({
    url: process.env.WEB_API + '/chat/newRoom',
    method: 'post',
    data: params
  })
}

export function deleteRoom(params) {
  return request({
    url: process.env.WEB_API + '/chat/deleteRoom',
    method: 'post',
    data: params
  })
}

export function withdrawMsg(params) {
  return request({
    url: process.env.WEB_API + '/chat/withdrawMsg',
    method: 'post',
    data: params
  })
}

export function history(params) {
  return request({
    url: process.env.WEB_API + '/chat/history',
    method: 'post',
    data: params
  })
}

export function roomList(params) {
  return request({
    url: process.env.WEB_API + '/chat/roomList',
    method: 'get',
    params
  })
}

export function uploadFile(params) {
  return request({
    url: process.env.PICTURE_API + "/file/pictures",
    method: 'post',
    data: params
  })
}
