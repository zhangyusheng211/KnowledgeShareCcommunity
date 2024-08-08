import request from '@/utils/request'

// 查询媒资标签列表
export function listCategory(query) {
  return request({
    url: process.env.ADMIN_API + '/media/category/list',
    method: 'post',
    data: query
  })
}

// 查询媒资标签详细
export function getCategory(data) {
  return request({
    url: process.env.ADMIN_API + '/media/category/info',
      method: 'post',
      data: data
  })
}

// 新增媒资标签
export function addCategory(data) {
  return request({
    url: process.env.ADMIN_API + '/media/category/add',
    method: 'post',
    data: data
  })
}

// 修改媒资标签
export function updateCategory(data) {
  return request({
    url: process.env.ADMIN_API + '/media/category/edit',
    method: 'post',
    data: data
  })
}

// 删除媒资标签
export function delCategory(uids) {
  return request({
    url:  process.env.ADMIN_API +'/media/category/deleteBatch',
    method: 'post',
    data: uids
  })
}
