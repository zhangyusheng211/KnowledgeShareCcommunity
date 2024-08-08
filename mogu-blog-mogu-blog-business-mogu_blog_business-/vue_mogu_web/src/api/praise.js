import request from '@/utils/request'

export function checkPraise(params) {
  return request({
    url: process.env.WEB_API + '/praise/checkPraise',
    method: 'post',
    data: params
  })
}

export function getPraiseCount(params) {
  return request({
    url: process.env.WEB_API + '/praise/getPraiseCount',
    method: 'post',
    data: params
  })
}

export function getPraiseUserList(params) {
  return request({
    url: process.env.WEB_API + '/praise/getPraiseUserList',
    method: 'post',
    data: params
  })
}


export function addPraise(params) {
  return request({
    url: process.env.WEB_API + '/praise/praise',
    method: 'post',
    data: params
  })
}

export function cancelPraise(params) {
  return request({
    url: process.env.WEB_API + '/praise/cancelPraise',
    method: 'post',
    data: params
  })
}

export function addTread(params) {
  return request({
    url: process.env.WEB_API + '/praise/tread',
    method: 'post',
    data: params
  })
}

export function cancelTread(params) {
  return request({
    url: process.env.WEB_API + '/praise/cancelTread',
    method: 'post',
    data: params
  })
}

