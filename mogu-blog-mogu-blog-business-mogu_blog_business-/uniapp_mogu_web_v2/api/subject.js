import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'


// 获取专栏分类列表
export function getSubjectSortList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/subject/getSortList', params)
}

// 获取专栏列表
export function getSubjectList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/subject/getList', params)
}

// 获取专栏项
export function getSubjectItemList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/subject/getItemList', params)
}