import request from '@/utils/request'

/**
 * 初始化ElasticSearch索引
 * @param params
 */
export function cleanIndex(params) {
  return request({
    url: process.env.ADMIN_API + '/search/cleanIndex',
    method: 'post',
    params
  })
}

/**
 * 初始化solr索引
 * @param params
 */
export function initSolrIndex(params) {
  return request({
    url: process.env.ADMIN_API + '/search/initSolrIndex',
    method: 'post',
    params
  })
}

/**
 * 批量初始化ElasticSearch索引
 * @param params
 */
export function batchInitElasticIndex(params) {
    return request({
        url: process.env.ADMIN_API + '/search/batchInitElasticIndex',
        method: 'get',
        params
    })
}
