/**
 * 全局常量
 * @type
 */
const ECode = {
	// 成功
	SUCCESS: "success",
	// 失败
	ERROR: "error",
}

/** **********************************************************/
const FUNCTIONS = {
	// 消息
	message: {
		success(message) {
			uni.showToast({
				title: message || '成功',
				icon: "none",
				mask: true,
			});
		},
		warning(message) {
			uni.showToast({
				title: message || '警告',
				icon: "none",
				mask: true,
			});
		},
		info(message) {
			uni.showToast({
				title: message || '提示',
				icon: "none",
				mask: true,
			});
		},
		error(message) {
			uni.showToast({
				title: message || '异常',
				icon: "none",
				mask: true,
			});
		}
	},

	// 可选链
	variableJudge(obj, keyName) {
		if (!obj) return null
		let keys = (keyName + '').split('.')
		let tempObj = obj
		for (let i = 0; i < keys.length; i++) {
			if (!tempObj) return
			if (keys[i] !== '') tempObj = tempObj?.[keys[i]]
		}
		return tempObj
	}
}

export default {
	ECode,
	FUNCTIONS
}