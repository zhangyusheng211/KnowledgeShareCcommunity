import request from '@/utils/request';

export function getProductList(params) {
  return request({
    url: process.env.ADMIN_API + '/product/getList',
    method: 'post',
    data: params
  });
}

export function addProduct(params) {
  return request({
    url: process.env.ADMIN_API + '/product/add',
    method: 'post',
    data: params
  });
}

export function editProduct(params) {
  return request({
    url: process.env.ADMIN_API + '/product/edit',
    method: 'post',
    data: params
  });
}

export function deleteBatchProduct(params) {
  return request({
    url: process.env.ADMIN_API + '/product/deleteBatch',
    method: 'post',
    data: params
  });
}
