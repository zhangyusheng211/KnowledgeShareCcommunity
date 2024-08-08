import request from '@/utils/request'

export function getSecretConfigList(params) {
  return request({
    url: process.env.ADMIN_API + '/secretConfig/getList',
    method: 'post',
    data: params
  })
}

export function addSecretConfig(params) {
  return request({
    url: process.env.ADMIN_API + '/secretConfig/add',
    method: 'post',
    data: params
  })
}

export function editSecretConfig(params) {
  return request({
    url: process.env.ADMIN_API + '/secretConfig/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchSecretConfig(params) {
  return request({
    url: process.env.ADMIN_API + '/secretConfig/deleteBatch',
    method: 'post',
    data: params
  })
}
