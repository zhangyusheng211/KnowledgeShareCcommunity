import request from '@/utils/request'

// 查询媒资标签列表
export function listTag(query) {
  return request({
    url: process.env.WEB_API + '/media/tag/list',
    method: 'get',
    params: query
  })
}

// 查询媒资标签详细
export function getTag(uid) {
  return request({
    url: process.env.WEB_API + '/media/tag/' + uid,
    method: 'get'
  })
}
