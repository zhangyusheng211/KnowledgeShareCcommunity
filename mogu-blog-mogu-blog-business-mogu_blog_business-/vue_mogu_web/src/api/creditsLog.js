import request from '@/utils/request'

export function getCreditsListByUser (params) {
  return request({
    url: process.env.WEB_API + '/web/credits/list',
    method: 'post',
    data: params
  })
}

export function leaderBoardAll (params) {
  return request({
    url: process.env.WEB_API + '/about/getLeaderAll?refresh=' + params,
    method: 'get'
  })
}

export function leaderBoardMonth (params) {
  return request({
    url: process.env.WEB_API + '/web/credits/getLeaderMonth?refresh=' + params,
    method: 'get'
  })
}

export function leaderBoardWeek (params) {
  return request({
    url: process.env.WEB_API + '/web/credits/getLeaderWeek?refresh=' + params,
    method: 'get'
  })
}

export function getLeaderMoment (params) {
  return request({
    url: process.env.WEB_API + '/userMoment/getLeaderMoment?refresh=' + params,
    method: 'get'
  })
}

export function getLeaderBlog (params) {
  return request({
    url: process.env.WEB_API + '/createBlog/getLeaderBlog?refresh=' + params,
    method: 'get'
  })
}
