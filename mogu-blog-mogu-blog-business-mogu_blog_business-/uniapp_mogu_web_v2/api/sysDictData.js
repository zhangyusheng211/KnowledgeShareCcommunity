import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'

export function getListByDictTypeList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/sysDictData/getListByDictTypeList', params)
}
