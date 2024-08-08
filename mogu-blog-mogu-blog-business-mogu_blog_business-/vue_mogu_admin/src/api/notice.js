import request from '@/utils/request'

export function searchAllNotice(params) {
  return request({
    url: process.env.ADMIN_API + '/notice/getBlackNoticeList',
    method: 'post',
    data: params
  })
}

export function deleteNotice(params) {
  return request({
    url: process.env.ADMIN_API + '/notice/delete',
    method: 'post',
    data: params
  })
}

export function deleteBatchNotice(params) {
  return request({
    url: process.env.ADMIN_API + '/notice/deleteBatchBlackNotice',
    method: 'post',
    data: params
  })
}

export function deleteRedisBatchNotice(params) {
  return request({
    url: process.env.ADMIN_API + '/notice/deleteRedisBatchNotice',
    method: 'post',
    data: params
  })
}

export function searchAllNoticeCount(params) {
  return request({
    url: process.env.ADMIN_API + '/notice/searchAllNoticeCount',
    method: 'post',
    data: params
  })
}
