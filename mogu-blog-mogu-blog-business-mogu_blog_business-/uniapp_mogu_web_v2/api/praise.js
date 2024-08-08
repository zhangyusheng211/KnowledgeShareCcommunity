import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'

// 获取点赞数
export function getPraiseCount(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/praise/getPraiseCount', params)
}

// 获得点赞用户数
export function getPraiseUserList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/praise/getPraiseUserList', params)
}

// 添加点赞
export function addPraise(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/praise/praise', params)
}

// 取消点赞
export function cancelPraise(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/praise/cancelPraise', params)
}
