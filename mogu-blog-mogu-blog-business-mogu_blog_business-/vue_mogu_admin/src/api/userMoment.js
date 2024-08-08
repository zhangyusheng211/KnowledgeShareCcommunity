import request from '@/utils/request'

export function getUserMomentList(params) {
  return request({
    url: process.env.ADMIN_API + '/userMoment/getList',
    method: 'post',
    data: params
  })
}

export function addUserMoment(params) {
  return request({
    url: process.env.ADMIN_API + '/userMoment/add',
    method: 'post',
    data: params
  })
}

export function editUserMoment(params) {
  return request({
    url: process.env.ADMIN_API + '/userMoment/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchUserMoment(params) {
  return request({
    url: process.env.ADMIN_API + '/userMoment/deleteBatch',
    method: 'post',
    data: params
  })
}

// 审批动态
export function auditMoment(params) {
  return request({
    url: process.env.ADMIN_API + '/userMoment/audit',
    method: 'post',
    data: params
  })
}
