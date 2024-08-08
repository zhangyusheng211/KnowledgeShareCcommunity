import request from '@/utils/request';

export function getVipList(params) {
  return request({
    url: process.env.WEB_API + '/vip/getList',
    method: 'get',
    data: params
  });
}



