import request from '@/utils/request'

export function getCreditsLogList(params) {
    return request({
        url: process.env.ADMIN_API + '/credits/getList',
        method: 'post',
        data: params
    })
}


export function updateCredits(params) {
    return request({
        url: process.env.ADMIN_API + '/credits/updateCredits',
        method: 'post',
        data: params
    })
}
