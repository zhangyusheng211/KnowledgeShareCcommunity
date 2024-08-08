import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'

export function getMe(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/about/getMe', params)
}

// 获取网站配置
export function getWebConfig(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/index/getWebConfig', params)
}

// 根据uid获取用户信息
export function getUserByUid(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/about/getUserByUid', params)
}

// 获取用户问答
export function getQuestionListByUser(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/about/getQuestionListByUser', params)
}

// 根据uid获取个人中心用户信息
export function getUserCenterByUid(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/about/getUserCenterByUid', params)
}

// 根据uid获取个人中心用户信息
export function getBlogListByUser(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/about/getBlogListByUser', params)
}

// 根据uid获取个人中心用户信息
export function getUserWatchList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/about/getUserWatchList', params)
}

// 检查当前用户的绑定状态
export function checkCurrentUserWatch(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/about/checkCurrentUserWatch', params)
}

// 添加用户关注
export function addUserWatch(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/about/addUserWatch', params)
}

// 删除用户关注
export function deleteUserWatch(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/about/deleteUserWatch', params)
}
