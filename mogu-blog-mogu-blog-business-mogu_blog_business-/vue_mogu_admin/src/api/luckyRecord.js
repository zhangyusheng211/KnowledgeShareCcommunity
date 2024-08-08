import request from '@/utils/request'

export function getLuckyRecordList(params) {
  return request({
    url: process.env.ADMIN_API + '/luckyRecord/getList',
    method: 'post',
    data: params
  })
}
