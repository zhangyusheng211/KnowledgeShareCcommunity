import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'

// 获取活动列表
export function getActivityList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/lucky/getActivityList', params)
}

// 获取抽奖记录
export function getLuckyRecordList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/lucky/getLuckyRecordList', params)
}

// 开始抽奖
export function lucky(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/lucky/lucky', params)
}
