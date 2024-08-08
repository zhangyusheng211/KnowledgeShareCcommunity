import request from '@/utils/request'

export function getCollectCount(params) {
  return request({
    url: process.env.WEB_API + '/collect/getCollectCount',
    method: 'post',
    data: params
  })
}

export function getUserCollectList(params) {
  return request({
    url: process.env.WEB_API + '/collect/getList',
    method: 'post',
    data: params
  })
}


export function addCollect(params) {
  return request({
    url: process.env.WEB_API + '/collect/addCollect',
    method: 'post',
    data: params
  })
}

export function deleteCollect(params) {
  return request({
    url: process.env.WEB_API + '/collect/deleteCollect',
    method: 'post',
    data: params
  })
}
