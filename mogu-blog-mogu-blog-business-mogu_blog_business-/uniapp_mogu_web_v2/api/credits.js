import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'

// 积分排行总榜
export function getLeaderAll(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/about/getLeaderAll', params)
}
export function getCreditsListByUser(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/web/credits/list', params)
}
