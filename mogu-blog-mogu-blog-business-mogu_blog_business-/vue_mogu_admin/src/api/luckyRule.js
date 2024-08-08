import request from '@/utils/request'

export function getLuckyRuleList(params) {
  return request({
    url: process.env.ADMIN_API + '/luckyRule/getList',
    method: 'post',
    data: params
  })
}

export function addLuckyRule(params) {
  return request({
    url: process.env.ADMIN_API + '/luckyRule/add',
    method: 'post',
    data: params
  })
}

export function editLuckyRule(params) {
  return request({
    url: process.env.ADMIN_API + '/luckyRule/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchLuckyRule(params) {
  return request({
    url: process.env.ADMIN_API + '/luckyRule/deleteBatch',
    method: 'post',
    data: params
  })
}
