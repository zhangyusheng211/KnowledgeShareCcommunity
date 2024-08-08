import request from '@/utils/request'

// 查询媒资标签列表
export function listCategory(query) {
  return request({
    url: process.env.WEB_API + '/media/category/list',
    method: 'get',
    params: query
  })
}

// 查询媒资标签详细
export function getCategory(uid) {
  return request({
    url: process.env.WEB_API + '/media/category/' + uid,
    method: 'get'
  })
}
