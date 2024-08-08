import request from '@/utils/request'

export function getLuckyAwardItemList(params) {
  return request({
    url: process.env.ADMIN_API + '/luckyAwardItem/getList',
    method: 'post',
    data: params
  })
}

export function addLuckyAwardItem(params) {
  return request({
    url: process.env.ADMIN_API + '/luckyAwardItem/add',
    method: 'post',
    data: params
  })
}

export function editLuckyAwardItem(params) {
  return request({
    url: process.env.ADMIN_API + '/luckyAwardItem/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchLuckyAwardItem(params) {
  return request({
    url: process.env.ADMIN_API + '/luckyAwardItem/deleteBatch',
    method: 'post',
    data: params
  })
}
