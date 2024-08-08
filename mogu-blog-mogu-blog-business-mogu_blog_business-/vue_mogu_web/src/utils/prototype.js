import commonUtil from './commonUtil'

export default {
  install(Vue, options) {
    Vue.prototype.$commonUtil = commonUtil.FUNCTIONS
    Vue.prototype.$ECode = commonUtil.ECode
    Vue.prototype.$EAction = commonUtil.EAction
    Vue.prototype.$SysConf = commonUtil.SysConf
  }
}
