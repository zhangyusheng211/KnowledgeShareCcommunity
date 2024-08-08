import request from '@/utils/request'

// 查询媒资标签列表
export function listActor(query) {
  return request({
    url: process.env.ADMIN_API + '/media/actor/list',
    method: 'post',
    data: query
  })
}

// 查询没被选中的演员列表
export function notSelectedActorList(query) {
  return request({
    url: process.env.ADMIN_API + '/media/actor/notSelectedList',
    method: 'post',
    data: query
  })
}

// 查询媒资标签详细
export function getActor(data) {
  return request({
    url: process.env.ADMIN_API + '/media/actor/info',
    method: 'post',
    data: data
  })
}

// 新增媒资标签
export function addActor(data) {
  return request({
    url: process.env.ADMIN_API + '/media/actor/add',
    method: 'post',
    data: data
  })
}

// 修改媒资标签
export function updateActor(data) {
  return request({
    url: process.env.ADMIN_API + '/media/actor/edit',
    method: 'post',
    data: data
  })
}

// 删除媒资标签
export function delActor(uids) {
  return request({
    url:  process.env.ADMIN_API +'/media/actor/deleteBatch',
    method: 'post',
    data: uids
  })
}
