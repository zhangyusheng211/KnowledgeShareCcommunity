import request from '@/utils/request'

// 查询媒资标签列表
export function listActor(query) {
  return request({
    url: process.env.ADMIN_API + '/media/actor/list',
    method: 'get',
    params: query
  })
}

// 查询媒资标签详细
export function getActor(uid) {
  return request({
    url: process.env.WEB_API + '/media/actor/' + uid,
    method: 'get'
  })
}

// 新增媒资标签
export function addActor(data) {
  return request({
    url: process.env.WEB_API + '/media/actor',
    method: 'post',
    data: data
  })
}
