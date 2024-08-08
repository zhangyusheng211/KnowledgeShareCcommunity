import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'


// 上传图片
export function uploadPicsByUrl(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-picture/file/uploadPicsByUrl', params)
}
