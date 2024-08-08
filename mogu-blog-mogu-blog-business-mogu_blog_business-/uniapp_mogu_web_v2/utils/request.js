import {
	appConfig
} from '../config/config.js'
import {
	tokenUtil
} from './token'

const errorCode = {
	'401': 'token无效或过期,请重新登录',
	'403': '当前操作没有权限',
	'404': '访问资源不存在',
	'500': '服务器打瞌睡了',
	'501': '服务器打瞌睡了',
	'503': '服务器打瞌睡了',
	'default': '服务器打瞌睡了'
};

const send = (url, data = {}, method = 'POST', showLoading = true) => {
	uni.showLoading({
		title: '加载中'
	})
	return new Promise((resolve) => {
		uni.request({
			method: method,
			url: url,
			data: data,
			header: (() => {
				const tokeValue = tokenUtil.get()
				let config = {
					// 'Content-Type': 'application/x-www-form-urlencoded'
					'Content-Type': 'application/json'
				}
				if (tokeValue) {
					config[appConfig.tokenKey] = tokeValue
				}
				let value = 2000000000000 - Date.parse(new Date())
				config['Pragmas'] = String(value)
				return config
			})(),
			success: (res) => {
				let code = res.data.code
				const msg = errorCode[code] || res.message || res.msg || errorCode['default']
				uni.hideLoading()
				resolve(res.data)

				if (code === 'success' || code === 'error') {
					resolve(res.data)
					return
				}

				if (code === 401) {
					// 清空用户信息
					uni.clearStorage("userInfo")
					tokenUtil.clear()
					setTimeout(() => {
						// 重定向到登录页面
						uni.navigateTo({
							url: '/pages/index?index=4'
						})
					}, 1500)
					return
				}

			},
		})
	})
}

export const request = {
	get: (url, data) => {
		return send(url, data, 'GET')
	},
	post: (url, data) => {
		return send(url, data, 'POST')
	}
}