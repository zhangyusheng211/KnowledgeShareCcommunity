import request from '@/utils/request'

export function getSubjectSortList(params) {
  return request({
    url: process.env.ADMIN_API + '/subjectSort/getList',
    method: 'post',
    data: params
  })
}

export function addSubjectSort(params) {
  return request({
    url: process.env.ADMIN_API + '/subjectSort/add',
    method: 'post',
    data: params
  })
}

export function editSubjectSort(params) {
  return request({
    url: process.env.ADMIN_API + '/subjectSort/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchSubjectSort(params) {
  return request({
    url: process.env.ADMIN_API + '/subjectSort/deleteBatch',
    method: 'post',
    data: params
  })
}
