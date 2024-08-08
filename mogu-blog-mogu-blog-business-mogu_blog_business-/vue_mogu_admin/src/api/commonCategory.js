import request from '@/utils/request';

export function getCommonCategoryList(params) {
  return request({
    url: process.env.ADMIN_API + '/commonCategory/getList',
    method: 'post',
    data: params
  });
}

export function addCommonCategory(params) {
  return request({
    url: process.env.ADMIN_API + '/commonCategory/add',
    method: 'post',
    data: params
  });
}

export function editCommonCategory(params) {
  return request({
    url: process.env.ADMIN_API + '/commonCategory/edit',
    method: 'post',
    data: params
  });
}

export function deleteBatchCommonCategory(params) {
  return request({
    url: process.env.ADMIN_API + '/commonCategory/deleteBatch',
    method: 'post',
    data: params
  });
}
