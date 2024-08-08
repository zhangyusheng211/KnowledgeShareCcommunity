import request from '@/utils/request'

export function getResourceList(params) {
  return request({
    url: process.env.WEB_API + '/resource/getResourceList',
    method: 'post',
    data: params
  })
}

export function getResourceDetail(params) {
  return request({
    url: process.env.WEB_API + '/resource/getResourceDetail',
    method: 'post',
    data: params
  })
}


export function downloadResource(params) {
  return request({
    url: process.env.WEB_API + '/resource/downloadResource',
    method: 'post',
    data: params
  })
}
