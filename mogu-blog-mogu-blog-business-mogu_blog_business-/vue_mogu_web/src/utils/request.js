import axios from 'axios'
import router from '@/router/index'
import {getCookie, delCookie} from "@/utils/cookieUtils";
import { MessageBox, Message } from 'element-ui'
import commonUtil from '@/utils/commonUtil'

// 创建axios实例
const service = axios.create({
  baseURL: '', // api 的 base_url
  timeout: 20000 // 请求超时时间 10秒
})

service.defaults.headers.common['Authorization'] = getCookie("token")

// request拦截器
service.interceptors.request.use(
  config => {
    if (getCookie("token") != undefined) {
      config.headers.Authorization = getCookie("token") // 让每个请求携带自定义token 请根据实际情况自行修改
    }
    if (getCookie("validCode") != undefined) {
      config.headers.validCode = getCookie("validCode")
    }
    let value =  2000000000000 - Date.parse(new Date())
    config.headers.Pragmas = String(value)
    return config
  },
  error => {
    // Do something with request error
    console.log("请求拦截", error) // for debug
    Promise.reject(error)
  }
)

// response 拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    const code = res.code || 'success';
    // 二进制数据则直接返回
    if(response.request.responseType ===  'blob' || response.request.responseType ===  'arraybuffer'){
      return res;
    }
    const errorCode = commonUtil.errorCode
    // 获取错误信息
    const msg = errorCode[code] || res.message || res.msg || errorCode['default']
    if (code === 'success' || code === 'error') {
      return res
    } else if (code === 400) {
      router.push('404')
      return res
    } else if (code === 401) {
      // 用户未登录
      console.error(res)
      Message({
        message: msg,
        type: 'error',
        duration: 2 * 1000
      })
      return res
    } else if (code === 403) {
      Message({
        message: msg,
        type: 'error'
      })
      router.push({path: '/403'})
      return res
    } else {
      Message({
        message: msg,
        type: 'error'
      })
      return Promise.reject('error')
    }
  },
  error => {
    console.log('err' + error)
    let { message } = error;
    if (message == "Network Error") {
      message = "后端接口连接异常";
    } else if (message.includes("timeout")) {
      message = "系统接口请求超时";
    } else if (message.includes("Request failed with status code")) {
      message = "服务器打瞌睡了";
      // message = "系统接口异常,errorCode[" + message.substr(message.length - 3) + "]";
    }
    Message({
      message: message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
