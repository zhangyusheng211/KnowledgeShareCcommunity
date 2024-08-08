import request from '@/utils/request'

export function report(params) {
  return request({
    url: process.env.WEB_API + '/report/submit',
    method: 'post',
    data: params
  })
}

