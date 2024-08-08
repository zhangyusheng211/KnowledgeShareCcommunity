import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'

export function getBlogByLevel(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/index/getBlogByLevel', params)
}

export function getNewBlog(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/index/getNewBlog', params)
}

export function getLink(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/index/getLink', params)
}

export function getHotBlogSort(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/index/getHotBlogSort', params)
}

export function getWebConfig(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/index/getWebConfig', params)
}