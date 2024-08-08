import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'

/**
 * 通过SQL搜索博客
 * @param params
 */
export function searchBlog(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/search/sqlSearchBlog', params)
}

/**
 * 通过ES聚合搜索
 * @param params
 */
export function elasticSearchAgg(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-search/search/elasticSearchAgg', params)
}

/**
 * 获取搜索模式
 * @param params
 */
export function getSearchModel(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/search/getSearchModel', params)
}

/**
 * 获取热搜列表
 * @param params
 */
export function getHotSearchList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/hotSearch/getHotSearchList', params)
}

/**
 * 添加热搜关键词
 * @param {Object} params
 */
export function addHotSearch(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/hotSearch/addHotSearch', params)
}
