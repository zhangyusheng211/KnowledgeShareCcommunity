import request from '@/utils/request'

export function getUserMomentList(params) {
  return request({
    url: process.env.WEB_API + '/userMoment/getUserMomentList',
    method: 'post',
    data: params
  })
}

export function getUserMomentTopicList(params) {
  return request({
    url: process.env.WEB_API + '/userMoment/getUserMomentTopicList',
    method: 'post',
    data: params
  })
}

export function addUserMoment(params) {
  return request({
    url: process.env.WEB_API + '/userMoment/addUserMoment',
    method: 'post',
    data: params
  })
}

export function deleteBatchUserMoment(params) {
  return request({
    url: process.env.WEB_API + '/userMoment/deleteBatch',
    method: 'post',
    data: params
  })
}

export function parseUrl(params) {
  return request({
    url: process.env.WEB_API + '/userMoment/parseUrl',
    method: 'get',
    params
  })
}
