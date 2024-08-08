import request from '@/utils/request'

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: process.env.PAY_API + '/order/getPageList',
    method: 'post',
    data: params
  })
}

// 获取提现列表
export function getWithdrawList(params) {
    return request({
        url: process.env.PAY_API + '/withdraw/getList',
        method: 'post',
        data: params
    })
}

// 提现审核
export function auditWithdraw(params) {
    return request({
        url: process.env.PAY_API + '/withdraw/audit',
        method: 'post',
        data: params
    })
}

// 获取订单金额流水
export function getOrderAmountLogList(params) {
    return request({
        url: process.env.PAY_API + '/orderAmountLog/getList',
        method: 'post',
        data: params
    })
}
