import request from '@/utils/request'

export function getTaskConfigList(params) {
  return request({
    url: process.env.SMS_API + '/task/getPageList',
    method: 'post',
    data: params
  })
}

export function addTaskConfig(params) {
  return request({
    url: process.env.SMS_API + '/task/add',
    method: 'post',
    data: params
  })
}

export function editTaskConfig(params) {
  return request({
    url: process.env.SMS_API + '/task/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchTaskConfig(params) {
  return request({
    url: process.env.SMS_API + '/task/deleteBatch',
    method: 'post',
    data: params
  })
}


