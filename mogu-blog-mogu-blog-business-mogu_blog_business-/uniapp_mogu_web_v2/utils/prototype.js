import commonUtil from './commonUtil'

export default {
	install(Vue, options) {
		Vue.prototype.$ECode = commonUtil.ECode
		Vue.prototype.$commonUtil = commonUtil.FUNCTIONS
		Vue.prototype.$message = commonUtil.FUNCTIONS.message
	}
}
