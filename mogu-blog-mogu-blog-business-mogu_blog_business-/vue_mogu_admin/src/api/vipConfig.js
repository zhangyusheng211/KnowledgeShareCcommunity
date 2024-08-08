import request from '@/utils/request';

export function getVipConfigList(params) {
  return request({
    url: process.env.ADMIN_API + '/vipConfig/getList',
    method: 'post',
    data: params
  });
}

export function addVipConfig(params) {
  return request({
    url: process.env.ADMIN_API + '/vipConfig/add',
    method: 'post',
    data: params
  });
}

export function editVipConfig(params) {
  return request({
    url: process.env.ADMIN_API + '/vipConfig/edit',
    method: 'post',
    data: params
  });
}

export function deleteBatchVipConfig(params) {
  return request({
    url: process.env.ADMIN_API + '/vipConfig/deleteBatch',
    method: 'post',
    data: params
  });
}
