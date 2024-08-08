import { request } from '../utils/request.js'
import { appConfig } from '../config/config.js'

export function getTagList (params) {
  return request.get(appConfig.GATEWAY_API + '/mogu-web/tag/getTagList', params)  
}

export function getArticleByTagUid (params) {
  return request.get(appConfig.GATEWAY_API + '/mogu-web/tag/getArticleByTagUid', params)  
}