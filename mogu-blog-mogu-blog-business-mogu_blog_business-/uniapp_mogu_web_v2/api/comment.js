import { request } from '../utils/request.js'
import { appConfig } from '../config/config.js'

export function getCommentList (params) {
  return request.post(appConfig.GATEWAY_API + '/mogu-web/web/comment/getList', params)  
}

export function getCommentListByApp (params) {
  return request.post(appConfig.GATEWAY_API + '/mogu-web/web/comment/getListByApp', params)  
}

export function getCommentListByUser (params) {
  return request.post(appConfig.GATEWAY_API + '/mogu-web/web/comment/getListByUser', params)  
}

export function getPraiseListByUser (params) {
  return request.post(appConfig.GATEWAY_API + '/mogu-web/web/comment/getPraiseListByUser', params)  
}

export function addComment (params) {
  return request.post(appConfig.GATEWAY_API + '/mogu-web/web/comment/add', params)  
}

export function deleteComment (params) {
  return request.post(appConfig.GATEWAY_API + '/mogu-web/web/comment/delete', params)  
}

export function reportComment (params) {
  return request.post(appConfig.GATEWAY_API + '/mogu-web/web/comment/report', params)  
}