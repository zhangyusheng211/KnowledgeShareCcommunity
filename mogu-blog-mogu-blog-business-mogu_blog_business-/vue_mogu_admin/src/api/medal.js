import request from '@/utils/request'

export function getMedalList(params) {
  return request({
    url: process.env.ADMIN_API + '/medal/getList',
    method: 'post',
    data: params
  })
}

export function addMedal(params) {
  return request({
    url: process.env.ADMIN_API + '/medal/add',
    method: 'post',
    data: params
  })
}

export function editMedal(params) {
  return request({
    url: process.env.ADMIN_API + '/medal/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchMedal(params) {
  return request({
    url: process.env.ADMIN_API + '/medal/deleteBatch',
    method: 'post',
    data: params
  })
}
