import request from '@/utils/request'

export function sponsor(params) {
  return request({
    url: process.env.PAY_API + '/trade/sponsor',
    method: 'post',
    data: params
  })
}

export function getSponsorOrderList(params) {
  return request({
    url: process.env.PAY_API + '/order/getSponsorOrderList',
    method: 'post',
    data: params
  })
}

export function buyNow(params) {
  return request({
    url: process.env.PAY_API + '/trade/buyNow',
    method: 'post',
    data: params
  })
}

export function checkWhetherPay(params) {
  return request({
    url: process.env.PAY_API + '/order/checkWhetherPay',
    method: 'get',
    params
  })
}

export function getUserAmount(params) {
  return request({
    url: process.env.PAY_API + '/orderAmountLog/getUserAmount',
    method: 'post',
    params
  })
}

export function addWithdraw(params) {
  return request({
    url: process.env.PAY_API + '/withdraw/add',
    method: 'post',
    data: params
  })
}

export function getOrderAmountLogList(params) {
  return request({
    url: process.env.PAY_API + '/orderAmountLog/getList',
    method: 'post',
    data: params
  })
}

export function getRecentWithdraw(params) {
  return request({
    url: process.env.PAY_API + '/withdraw/getRecentWithdraw',
    method: 'post',
    data: params
  })
}

export function getMyWithdrawAmount(params) {
  return request({
    url: process.env.PAY_API + '/withdraw/getMyWithdrawAmount',
    method: 'post',
    data: params
  })
}
