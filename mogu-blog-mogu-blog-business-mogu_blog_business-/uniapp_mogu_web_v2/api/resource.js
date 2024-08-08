import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'


// 获取资源列表
export function getResourceList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/resource/getResourceList', params)
}

// 获取资源详情
export function getResourceDetail(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/resource/getResourceDetail', params)
}

// 下载资源
export function downloadResource(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/resource/downloadResource', params)
}