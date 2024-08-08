import request from '@/utils/request'

export function getMessagePushList(params) {
  return request({
    url: process.env.ADMIN_API + '/messagePush/getList',
    method: 'post',
    data: params
  })
}

export function addMessagePush(params) {
  return request({
    url: process.env.ADMIN_API + '/messagePush/add',
    method: 'post',
    data: params
  })
}

export function pushMessagePush(params) {
    return request({
        url: process.env.ADMIN_API + '/messagePush/push',
        method: 'post',
        data: params
    })
}

export function editMessagePush(params) {
  return request({
    url: process.env.ADMIN_API + '/messagePush/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchMessagePush(params) {
  return request({
    url: process.env.ADMIN_API + '/messagePush/deleteBatch',
    method: 'post',
    data: params
  })
}
