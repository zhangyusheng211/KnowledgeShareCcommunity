import request from '@/utils/request'

export function getUserMomentTopicList(params) {
  return request({
    url: process.env.ADMIN_API + '/userMomentTopic/getList',
    method: 'post',
    data: params
  })
}

export function addUserMomentTopic(params) {
  return request({
    url: process.env.ADMIN_API + '/userMomentTopic/add',
    method: 'post',
    data: params
  })
}

export function editUserMomentTopic(params) {
  return request({
    url: process.env.ADMIN_API + '/userMomentTopic/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchUserMomentTopic(params) {
  return request({
    url: process.env.ADMIN_API + '/userMomentTopic/deleteBatch',
    method: 'post',
    data: params
  })
}
