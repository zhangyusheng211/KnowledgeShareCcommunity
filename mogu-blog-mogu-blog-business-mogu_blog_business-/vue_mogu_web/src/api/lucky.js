import request from '@/utils/request'

export function getActivityList(params) {
  return request({
    url: process.env.WEB_API + '/lucky/getActivityList',
    method: 'post',
    data: params
  })
}

export function getLuckyRecordList(params) {
  return request({
    url: process.env.WEB_API + '/lucky/getLuckyRecordList',
    method: 'post',
    data: params
  })
}

export function lucky(params) {
  return request({
    url: process.env.WEB_API + '/lucky/lucky',
    method: 'post',
    data: params
  })
}
