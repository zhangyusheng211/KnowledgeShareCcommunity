import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'

// 新增收藏
export function addCollect(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/collect/addCollect', params)
}

// 删除收藏
export function deleteCollect(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/collect/deleteCollect', params)
}

// 获取收藏数
export function getCollectCount(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/collect/getCollectCount', params)
}

// 获取收藏列表
export function getCollectList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/collect/getList', params)
}
