import request from '@/utils/request'

export function getMedalClassifyList(params) {
  return request({
    url: process.env.ADMIN_API + '/medalClassify/getList',
    method: 'post',
    data: params
  })
}

export function addMedalClassify(params) {
  return request({
    url: process.env.ADMIN_API + '/medalClassify/add',
    method: 'post',
    data: params
  })
}

export function editMedalClassify(params) {
  return request({
    url: process.env.ADMIN_API + '/medalClassify/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchMedalClassify(params) {
  return request({
    url: process.env.ADMIN_API + '/medalClassify/deleteBatch',
    method: 'post',
    data: params
  })
}
