import request from '@/utils/request'

export function getAwardProductList(params) {
  return request({
    url: process.env.ADMIN_API + '/awardProduct/getList',
    method: 'post',
    data: params
  })
}

export function addAwardProduct(params) {
  return request({
    url: process.env.ADMIN_API + '/awardProduct/add',
    method: 'post',
    data: params
  })
}

export function editAwardProduct(params) {
  return request({
    url: process.env.ADMIN_API + '/awardProduct/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchAwardProduct(params) {
  return request({
    url: process.env.ADMIN_API + '/awardProduct/deleteBatch',
    method: 'post',
    data: params
  })
}
