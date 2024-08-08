import request from '@/utils/request'

export function getLuckyActivityList(params) {
  return request({
    url: process.env.ADMIN_API + '/luckyActivity/getList',
    method: 'post',
    data: params
  })
}

export function addLuckyActivity(params) {
  return request({
    url: process.env.ADMIN_API + '/luckyActivity/add',
    method: 'post',
    data: params
  })
}

export function editLuckyActivity(params) {
  return request({
    url: process.env.ADMIN_API + '/luckyActivity/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchLuckyActivity(params) {
  return request({
    url: process.env.ADMIN_API + '/luckyActivity/deleteBatch',
    method: 'post',
    data: params
  })
}
