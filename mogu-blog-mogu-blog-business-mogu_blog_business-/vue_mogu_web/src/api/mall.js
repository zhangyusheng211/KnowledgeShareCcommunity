import request from '@/utils/request';

export function getProductList(params) {
  return request({
    url: process.env.WEB_API + '/mall/getProductList',
    method: 'post',
    data: params
  });
}

export function getProductDetail(params) {
  return request({
    url: process.env.WEB_API + '/mall/getProductDetail',
    method: 'post',
    data: params
  });
}
export function getCommonCategoryList(params) {
  return request({
    url: process.env.WEB_API + '/mall/getCommonCategoryList',
    method: 'post',
    data: params
  });
}
