import request from '@/utils/request'

// 查询用户最近获取的勋章
export function getMedalByRecent(query) {
  return request({
    url: process.env.WEB_API + '/medal/getMedalByRecent',
    method: 'post',
    data: query
  })
}

// 获取用户勋章
export function getUserMedalList(query) {
  return request({
    url: process.env.WEB_API + '/medal/getUserMedalList',
    method: 'post',
    data: query
  })
}
