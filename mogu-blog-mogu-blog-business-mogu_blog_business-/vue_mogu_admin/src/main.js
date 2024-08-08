import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

// element使用CDN全局引入，因此这里可以注释
// import ElementUI from 'element-ui'
// import 'element-ui/lib/theme-chalk/index.css'
// import locale from 'element-ui/lib/locale/lang/en'

// 全局样式
import '@/styles/index.scss'
// 引入全局工具类
import prototype from './utils/prototype'

import App from './App'
import router from './router'
import store from './store'
import '@/icons' // icon
import '@/permission' // permission control

// 引入自定义指令
import './directive'

//自定义表格工具扩展
import RightToolbar from "@/components/RightToolbar"
// 字典标签组件
import DictTag from '@/components/DictTag'
import Pagination from '@/components/Pagination'
import Editor from '@/components/Editor'
import ImageUpload from '@/components/ImageUpload'
// 全局组件挂载
Vue.component('RightToolbar', RightToolbar)
Vue.component('DictTag', DictTag)
Vue.component('Pagination', Pagination)
Vue.component('Editor', Editor)
Vue.component('ImageUpload', ImageUpload)
// 添加粒子特效
import VueParticles from 'vue-particles'

// 引入vue-mate管理元信息
import VueMeta from 'vue-meta'
Vue.use(VueMeta)

import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
Vue.use(mavonEditor)

// 因引入CDN加速，无需再次引入
// Vue.use(ElementUI, { locale })

import moment from 'moment';
moment.locale('zh-cn');
Vue.prototype.$moment = moment; //时间处理

import xss from 'xss'
// 定义全局XSS解决方法
Object.defineProperty(Vue.prototype, '$xss', {
  value: xss
})

Vue.use(VueParticles)
Vue.use(prototype)
Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
