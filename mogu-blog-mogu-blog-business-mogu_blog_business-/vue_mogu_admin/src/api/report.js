import request from '@/utils/request'

export function getReportList(params) {
    return request({
        url: process.env.ADMIN_API + '/report/getList',
        method: 'post',
        data: params
    })
}

export function reportHandle(params) {
    return request({
        url: process.env.ADMIN_API + '/report/handle',
        method: 'post',
        data: params
    })
}

export function deleteBatch(params) {
    return request({
        url: process.env.ADMIN_API + '/report/deleteBatch',
        method: 'post',
        data: params
    })
}
