import request from '@/utils/request'

export function getResourceList(params) {
  return request({
    url: process.env.ADMIN_API + '/resource/getList',
    method: 'post',
    data: params
  })
}

export function addResource(params) {
  return request({
    url: process.env.ADMIN_API + '/resource/add',
    method: 'post',
    data: params
  })
}

export function editResource(params) {
  return request({
    url: process.env.ADMIN_API + '/resource/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchResource(params) {
  return request({
    url: process.env.ADMIN_API + '/resource/deleteBatch',
    method: 'post',
    data: params
  })
}
