import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'

export function getNoticeList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/notice/getList', params)
}

export function deleteNotice(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/notice/delete', params)
}

export function deleteBatchNotice(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/notice/deleteBatch', params)
}

export function getUserReceiveNoticeCount(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/notice/getUserReceiveNoticeCount', params)
}

export function readUserReceiveNoticeCount(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/notice/readUserReceiveNoticeCount', params)
}

// 获取站内信推送
export function getWebNotice(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/messagePush/getWebNotice', params)
}
