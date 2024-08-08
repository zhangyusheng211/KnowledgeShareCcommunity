import request from '@/utils/request'

export function generalEdit(params) {
  return request({
    url: process.env.WEB_API + '/generalEdit/generalEdit',
    method: 'post',
    data: params
  })
}
