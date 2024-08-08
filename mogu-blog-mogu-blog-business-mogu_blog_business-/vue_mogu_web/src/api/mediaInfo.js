import request from '@/utils/request'

export function getMediaInfoList(params) {
  return request({
    url: process.env.WEB_API + '/media/info/list',
    method: 'post',
    data: params
  })
}

export function getMediaInfo(uid) {
  return request({
    url: process.env.WEB_API + '/media/info/' + uid,
    method: 'get'
  })
}

export function getMediaVideo(params) {
  return request({
    url: process.env.WEB_API + '/media/info/video',
    method: 'post',
    data: params
  })
}
