import {
	request
} from '../utils/request.js'
import {
	appConfig
} from '../config/config.js'

// 获取我的文章
export function getMeBlogList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/createBlog/getMeBlogList', params)
}
// 获取博客分类
export function getBlogSortList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/createBlog/getBlogSortList', params)
}
// 获取标签分类
export function getBlogTagList(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/createBlog/getBlogTagList', params)
}
// 添加文章
export function addBlog(params) {
	return request.post(appConfig.GATEWAY_API + '/mogu-web/createBlog/add', params)
}