import { request } from '../utils/request.js'
import { appConfig } from '../config/config.js'

export function getBlogByUid (params) {
  return request.get(appConfig.GATEWAY_API + '/mogu-web/content/getBlogByUid', params)  
}

export function praiseBlogByUid (params) {
  return request.get(appConfig.GATEWAY_API + '/mogu-web/content/praiseBlogByUid', params)  
}
