import request from '@/utils/request'

// 查询媒资标签列表
export function getMyTaskList(query) {
  return request({
    url: process.env.SMS_API + '/task/getMyTaskList',
    method: 'post',
    params: query
  })
}

export function taskClick(query) {
  return request({
    url: process.env.SMS_API + '/task/click',
    method: 'get',
    params: query
  })
}
