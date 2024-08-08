import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'


// 申请友链
export function replyBlogLink(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/oauth/replyBlogLink', params)
}

// 新增反馈
export function addFeedback(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/oauth/addFeedback', params)
}

// 小程序登录
export function appLogin(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/oauth/appLogin', params)
}

// 本地登录
export function localLogin(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/auth/login', params)
}

// 本地注册
export function localRegister(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/auth/register', params)
}

// 通过token获取信息
export function authVerify(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/oauth/verify/' + params, {})
}

// 通过token获取信息
export function editUser(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/oauth/editUser', params)
}

// 解析移动端数据
export function decryptData(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/oauth/decryptData', params)
}

// 解析移动端数据
export function code2Session(params) {
	return request.get('https://api.q.qq.com/sns/jscode2session', params)
}
// 获取当前用户的积分
export function getCurrentUserCredits(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/about/getCurrentUserCredits', params)
}
// 获取用户签到数据
export function getSignDataByUserUid(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/about/signDataByUserUid', params)
}
// 用户签到
export function userSignIn(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/about/userSignIn', params)
}

// 小程序绑定
export function bindCode(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/oauth/bindCode', params)
}

// 根据绑定Code获取用户信息
export function getUserInfoByBindCode(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/oauth/getUserInfoByBindCode', params)
}

// 校验加载校验码
export function checkValidCode(params) {
	return request.get(appConfig.GATEWAY_API + '/mogu-web/wechat/checkValidCode', params)
}