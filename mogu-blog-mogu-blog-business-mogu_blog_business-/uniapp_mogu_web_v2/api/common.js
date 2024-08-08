import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'


// 校验资源权限
export function checkResourceVisitAuth(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/common/checkResourceVisitAuth', params)
}