import request from '@/utils/request'

export function getNoticeList(params) {
  return request({
    url: process.env.WEB_API + '/notice/getList',
    method: 'post',
    data: params
  })
}


export function deleteNotice(params) {
  return request({
    url: process.env.WEB_API + '/notice/delete',
    method: 'post',
    data: params
  })
}

export function deleteBatchNotice(params) {
  return request({
    url: process.env.WEB_API + '/notice/deleteBatch',
    method: 'post',
    data: params
  })
}

export function getUserReceiveNoticeCount(params) {
  return request({
    url: process.env.WEB_API + '/notice/getUserReceiveNoticeCount',
    method: 'get',
    params
  })
}

export function readUserReceiveNoticeCount(params) {
  return request({
    url: process.env.WEB_API + '/notice/readUserReceiveNoticeCount',
    method: 'post',
    data: params
  })
}



