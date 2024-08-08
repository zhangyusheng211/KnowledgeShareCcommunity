import request from '@/utils/request'

/**
 * 获取搜索模式
 * @param params
 * @returns {*}
 */
export function getSearchModel(params) {
  return request({
    url: process.env.WEB_API + '/search/getSearchModel',
    method: 'get',
    params
  })
}

/**
 * 通过SQL搜索博客
 * @param params
 */
export function searchBlogBySQL(params) {
  return request({
    url: process.env.WEB_API + '/search/sqlSearchBlog',
    method: 'get',
    params
  })
}

/**
 * 通过SQL搜索问答
 * @param params
 */
export function searchQuestion(params) {
  return request({
    url: process.env.WEB_API + '/search/sqlSearchQuestion',
    method: 'get',
    params
  })
}

/**
 * 通过ElasticSearch搜索博客
 * @param params
 */
export function searchAggByES(params) {
  return request({
    url: process.env.SEARCH_API + '/search/elasticSearchAgg',
    method: 'get',
    params
  })
}

/**
 * 通过solr搜索博客
 * @param params
 */
export function searchBlogBySolr(params) {
  return request({
    url: process.env.SEARCH_API + '/search/solrSearchBlog',
    method: 'get',
    params
  })
}

/**
 * 根据用户搜索Blog
 * @Author Andy
 * @param params
 * @returns {*}
 */
export function searchMomentByUser(params) {
  return request({
    url: process.env.WEB_API + '/search/searchMomentByUser',
    method: 'get',
    params
  })
}

/**
 * 搜索面经
 * @Author Andy
 * @param params
 * @returns {*}
 */
export function searchProblem(params) {
  return request({
    url: process.env.WEB_API + '/search/searchProblem',
    method: 'get',
    params
  })
}

/**
 * 搜索资源
 * @Author Andy
 * @param params
 * @returns {*}
 */
export function searchResource(params) {
  return request({
    url: process.env.WEB_API + '/search/searchResource',
    method: 'get',
    params
  })
}

/**
 * 根据用户名搜索相关用户信息
 * @param params
 * @returns {*}
 */
export function searchByUser(params) {
  return request({
    url: process.env.WEB_API + '/search/searchByUser',
    method: 'get',
    params
  })
}

export function searchBlogByTag(params) {
  return request({
    url: process.env.WEB_API + '/search/searchBlogByTag',
    method: 'get',
    params
  })
}

export function searchBlogBySort(params) {
  return request({
    url: process.env.WEB_API + '/search/searchBlogBySort',
    method: 'get',
    params
  })
}

export function searchBlogByAuthor(params) {
  return request({
    url: process.env.WEB_API + '/search/searchBlogByAuthor',
    method: 'get',
    params
  })
}
