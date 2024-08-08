import request from '@/utils/request'

export function getProblemTagList(params) {
  return request({
    url: process.env.ADMIN_API + '/problemTag/getList',
    method: 'post',
    data: params
  })
}

export function getAllProblemTagList(params) {
  return request({
    url: process.env.WEB_API + '/problemTag/getAllTagList',
    method: 'post',
    data: params
  })
}


export function addProblemTag(params) {
  return request({
    url: process.env.ADMIN_API + '/problemTag/add',
    method: 'post',
    data: params
  })
}

export function editProblemTag(params) {
  return request({
    url: process.env.ADMIN_API + '/problemTag/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchProblemTag(params) {
  return request({
    url: process.env.ADMIN_API + '/problemTag/deleteBatch',
    method: 'post',
    data: params
  })
}
