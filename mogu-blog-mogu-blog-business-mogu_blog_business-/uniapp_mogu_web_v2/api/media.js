import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'


export function getMediaList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/media/info/list', params)
}

// {mediaInfoUid: "12e0ee39f04966befec4aa29a0177796"}
export function getMediaDetail(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/media/info/video', params)
}

// 查询课程分类
export function listCategory(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/media/category/list', params)
}

// 查询课程标签
export function listTag(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/media/tag/list', params)
}