import request from '@/utils/request'

export function checkResourceVisitAuth(params) {
  return request({
    url: process.env.WEB_API + '/common/checkResourceVisitAuth',
    method: 'post',
    data: params
  })
}
