import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'

// 获取面经列表
export function getProblemList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/problem/getList', params)
}

export function getProblemQueryList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/problem/queryPage', params)
}

// 掌握问题
export function problemDegree(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/problem/problemDegree', params)
}

export function getAllProblemTagList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/problem/getAllTagList', params)
}

export function getProblemTagList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/problem/getTagList', params)
}

// 获取用户刷题记录数
export function getUserProblemRecords(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/problem/getUserProblemRecords', params)
}