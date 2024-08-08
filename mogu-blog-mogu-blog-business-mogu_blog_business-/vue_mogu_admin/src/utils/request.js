import axios from 'axios'
import {Message, MessageBox, Loading} from 'element-ui'
import store from '../store'
import {getToken} from '@/utils/auth'
import {getCookie} from "./cookieUtils";
import commonUtil from '@/utils/commonUtil'

// 创建axios实例
const service = axios.create({
    baseURL: '', // api 的 base_url
    withCredentials: true, //允许后台的cookie传递到前端
    timeout: 100000 // 请求超时时间
})

// 传递token
service.defaults.headers.common['Authorization'] = getToken()
service.defaults.headers.common['AppId'] = getCookie('appid')

// 请求计数器
var requestNum = 0;
var loading = null;

// request拦截器
service.interceptors.request.use(
    config => {
        if (store.getters.token) {
            // 让每个请求携带自定义token 请根据实际情况自行修改
            config.headers.Authorization = getToken()
        }

        // 请求加1
        requestNum++;

        if (loading == null) {
            loading = Loading.service({fullscreen: true, text: '正在努力加载中~'});
        } else if (loading != null && requestNum > 0) {
            loading = Loading.service({fullscreen: true, text: '正在努力加载中~'});
        }

        return config
    },
    error => {
        // 出错了直接关闭loading
        requestNum = 0
        if (loading) {
            loading.close();
        }
        // Do something with request error
        console.log(error)
        Promise.reject(error)
    }
)

// response 拦截器
service.interceptors.response.use(
    response => {
        /**
         * code为非success和error是抛错 可结合自己业务进行修改
         */
        const res = response.data
        // 请求数减1
        requestNum--;
        if (loading == null || requestNum <= 0) {
            loading.close()
        }
        const code = res.code || 'success';
        // 二进制数据则直接返回
        if(response.request.responseType ===  'blob' || response.request.responseType ===  'arraybuffer'){
            return res;
        }
        // 获取错误信息
        const errorCode = commonUtil.errorCode
        const msg = errorCode[code] || res.message || res.msg || errorCode['default']

        if (code === 'success') {
            // 请求完毕
            return res
        } else if (code === 'error') {
            Message({
                message: msg,
                type: 'error'
            })
            return Promise.reject(new Error(msg))
        } else {
            // 出错了直接关闭loading
            requestNum = 0
            loading.close();
            if (code === 401) {
                MessageBox.confirm(
                    'token已过期，可以取消继续留在该页面，或者重新登录',
                    '确定登出',
                    {
                        confirmButtonText: '重新登录',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                ).then(() => {
                    store.dispatch('FedLogOut').then(() => {
                        location.reload() // 为了重新实例化vue-router对象 避免bug
                    })
                })
                return Promise.reject('error')
            } else if (code === 402 || code === 403) {
                // 接口没有权限访问时
                Message({
                    message: msg,
                    type: 'error',
                    duration: 5 * 1000
                })
                return Promise.reject('error')
            } else {
                console.log("错误信息", res)
                Message({
                    message: msg,
                    type: 'error',
                    duration: 5 * 1000
                })
                return Promise.reject(msg)
            }
        }
    },
    error => {
        console.log('err' + error.message)

        // 出错了直接关闭loading
        requestNum = 0
        if (loading) {
            loading.close();
        }

        let {message} = error;
        if (message == "Network Error") {
            message = "后端接口连接异常";
        } else if (message.includes("timeout")) {
            message = "系统接口请求超时";
        } else if (message.includes("Request failed with status code")) {
            message = "系统接口异常,errorCode[" + message.substr(message.length - 3) + "]";
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
