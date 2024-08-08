import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'


// 第三方登录
export function oauthLogin(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/oauth/render?' + params)
}

// 本地登录
export function localLogin(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/auth/login', params)
}

// 本地注册
export function localRegister(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/auth/register', params)
}

// 获取用户登录状态
export function getUserLoginStatus(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/wechat/getUserLoginStatus', params)
}

// 获取微信登录链接
// export function getWeChatLoginUrl(params) {
// 	return request.post(appConfig.GATEWAY_API + '/mogu-web/oauth/getWeChatLoginUrl?' + params)
// }

// 获取微信登录链接
export function getOauthUrl(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-pay/wechat/getOauthUrl', params)
}

// 获取登录的Key
export function getLoginKey(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/wechat/getLoginKey', params)
}

// 获取绑定的Key
export function getBindKey(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/wechat/getBindKey', params)
}

// 登录校验
export function loginCheck(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/wechat/loginCheck', params)
}