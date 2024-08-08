import request from '@/utils/request'

// 获取 通用修改列表
export function getGeneralEditList(params) {
  return request({
    url: process.env.ADMIN_API + '/generalEdit/getGeneralEditList',
    method: 'post',
    data: params
  })
}

// 批量删除
export function deleteBatchGeneralEdit(params) {
  return request({
    url: process.env.ADMIN_API + '/generalEdit/deleteBatch',
    method: 'post',
    data: params
  })
}

// 审批
export function auditGeneralEdit(params) {
  return request({
    url: process.env.ADMIN_API + '/generalEdit/audit',
    method: 'post',
    data: params
  })
}
