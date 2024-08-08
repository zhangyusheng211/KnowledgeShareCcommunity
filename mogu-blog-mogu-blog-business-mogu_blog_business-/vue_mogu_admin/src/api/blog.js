import request from '@/utils/request';

export function deleteBlog(params) {
  return request({
    url: process.env.ADMIN_API + '/blog/delete',
    method: 'post',
    data: params
  })
}


export function getBlogList(params) {
  return request({
    url: process.env.ADMIN_API + '/blog/getList',
    method: 'post',
    data: params
  })
}

export function publishBlog(params) {
  return request({
    url: process.env.ADMIN_API + '/blog/publish',
    method: 'post',
    data: params
  })
}

export function addBlog(params) {
  return request({
    url: process.env.ADMIN_API + '/blog/add',
    method: 'post',
    data: params
  })
}

export function uploadLocalBlog(params) {
  return request({
    url: process.env.ADMIN_API + '/blog/uploadLocalBlog',
    method: 'post',
    data: params
  })
}


export function editBlog(params) {
  return request({
    url: process.env.ADMIN_API + '/blog/edit',
    method: 'post',
    data: params
  })
}

export function editBatchBlog(params) {
  return request({
    url: process.env.ADMIN_API + '/blog/editBatch',
    method: 'post',
    data: params
  })
}

export function batchAuditBlog(params) {
  return request({
    url: process.env.ADMIN_API + '/blog/batchAuditBlog',
    method: 'post',
    data: params
  })
}

export function auditBlog(params) {
  return request({
    url: process.env.ADMIN_API + '/blog/audit',
    method: 'post',
    data: params
  })
}

export function deleteBatchBlog(params) {
  return request({
    url: process.env.ADMIN_API + '/blog/deleteBatch',
    method: 'post',
    data: params
  })
}

export function pushBatchBlog(params) {
  return request({
    url: process.env.ADMIN_API + '/blog/pushBatch',
    method: 'post',
    data: params
  })
}

