import request from '@/utils/request'

// 查询媒资标签列表
export function listTag(query) {
  return request({
    url: process.env.ADMIN_API + '/media/tag/list',
    method: 'post',
    data: query
  })
}

// 查询媒资标签详细
export function getTag(data) {
  return request({
    url: process.env.ADMIN_API + '/media/tag/info',
    method: 'post',
    data: data
  })
}

// 新增媒资标签
export function addTag(data) {
  return request({
    url: process.env.ADMIN_API + '/media/tag/add',
    method: 'post',
    data: data
  })
}

// 修改媒资标签
export function updateTag(data) {
  return request({
    url: process.env.ADMIN_API + '/media/tag/edit',
    method: 'post',
    data: data
  })
}

// 删除媒资标签
export function delTag(uids) {
  return request({
    url:  process.env.ADMIN_API +'/media/tag/deleteBatch',
    method: 'post',
    data: uids
  })
}
