import request from '@/utils/request'

export function getLoginKey(params) {
  return request({
    url: process.env.WEB_API + '/wechat/getLoginKey',
    method: 'get',
    data: params
  })
}

export function loginCheck(params) {
  return request({
    url: process.env.WEB_API + '/wechat/loginCheck',
    method: 'post',
    params
  })
}

export function getBindKey(params) {
  return request({
    url: process.env.WEB_API + '/wechat/getBindKey',
    method: 'get',
    data: params
  })
}

