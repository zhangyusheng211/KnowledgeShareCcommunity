import request from '@/utils/request'

/**
 * 新增订阅
 * @param params
 * @returns {*}
 */
export function addUserSubscribe(params) {
  return request({
    url: process.env.WEB_API + '/subscribe/addUserSubscribe',
    method: 'post',
    data: params
  });
}

/**
 * 删除订阅
 * @param params
 * @returns {*}
 */
export function deleteUserSubscribe(params) {
  return request({
    url: process.env.WEB_API + '/subscribe/deleteUserSubscribe',
    method: 'post',
    data: params
  });
}

/**
 * 校验用户是否订阅
 * @param params
 * @returns {*}
 */
export function checkUserSubscribe(params) {
  return request({
    url: process.env.WEB_API + '/subscribe/checkUserSubscribe',
    method: 'post',
    data: params
  });
}
