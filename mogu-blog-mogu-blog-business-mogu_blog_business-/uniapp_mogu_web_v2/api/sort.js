import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'

export function getSortList(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/sort/getSortList', params)
}

export function getArticleByMonth(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/sort/getArticleByMonth', params)
}
