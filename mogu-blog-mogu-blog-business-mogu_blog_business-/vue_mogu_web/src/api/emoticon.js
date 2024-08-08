import request from '@/utils/request'

export function getEmoticonList(params) {
  return request({
    url: process.env.WEB_API + '/emoticon/getList',
    method: 'post',
    data: params
  })
}

export function addEmoticon(params) {
  return request({
    url: process.env.WEB_API + '/emoticon/add',
    method: 'post',
    data: params
  })
}

export function stickyEmoticon(params) {
  return request({
    url: process.env.WEB_API + '/emoticon/sticky',
    method: 'post',
    data: params
  })
}

export function deleteEmoticon(params) {
  return request({
    url: process.env.WEB_API + '/emoticon/batchDelete',
    method: 'post',
    data: params
  })
}
