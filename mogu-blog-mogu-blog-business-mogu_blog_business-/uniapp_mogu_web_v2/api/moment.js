import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'

// 获取用户动态
export function getUserMomentList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/userMoment/getUserMomentList', params)
}

// 获取动态分类
export function getUserMomentTopicList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/userMoment/getUserMomentTopicList', params)
}

// 发表动态
export function addUserMoment(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/userMoment/addUserMoment', params)
}
