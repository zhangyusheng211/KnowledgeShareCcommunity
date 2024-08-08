import request from '@/utils/request'

export function getHotSearchList () {
  return request({
    url: process.env.WEB_API + '/hotSearch/getHotSearchList',
    method: 'post'
  })
}

export function addHotSearch (params) {
  return request({
    url: process.env.WEB_API + '/hotSearch/addHotSearch?keyword=' + params,
    method: 'get'
  })
}

export function getOutsideHotSearch (params) {
  return request({
    url: process.env.WEB_API + '/hotSearch/getOutsideHotSearch',
    method: 'get',
    params
  })
}
