import request from '@/utils/request'

export function getWebNotice(params) {
  return request({
    url: process.env.WEB_API + '/messagePush/getWebNotice',
    method: 'post',
    data: params
  })
}
