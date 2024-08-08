'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',

  VUE_MOGU_WEB: '"http://localhost:9527"',
  PICTURE_API: '"http://localhost:8607/mogu-picture"',
  WEB_API: '"http://localhost:8607/mogu-web"',
  WS_API: '"ws://localhost:8608"',
  SEARCH_API: '"http://localhost:8607/mogu-search"',
  SMS_API: '"http://localhost:8607/mogu-sms"',
  PAY_API: '"http://localhost:8607/mogu-pay"',

  // 默认头像
  defaultAvatar: '"https://oos.moguit.cn/image/favicon.png"',
})
