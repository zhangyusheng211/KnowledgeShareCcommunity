import request from '@/utils/request'

export function getProblemList(params) {
  return request({
    url: process.env.ADMIN_API + '/problem/getList',
    method: 'post',
    data: params
  })
}

export function addProblem(params) {
  return request({
    url: process.env.ADMIN_API + '/problem/add',
    method: 'post',
    data: params
  })
}

export function editProblem(params) {
  return request({
    url: process.env.ADMIN_API + '/problem/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchProblem(params) {
  return request({
    url: process.env.ADMIN_API + '/problem/deleteBatch',
    method: 'post',
    data: params
  })
}

// 审批问题
export function auditProblem(params) {
  return request({
    url: process.env.ADMIN_API + '/problem/audit',
    method: 'post',
    data: params
  })
}


