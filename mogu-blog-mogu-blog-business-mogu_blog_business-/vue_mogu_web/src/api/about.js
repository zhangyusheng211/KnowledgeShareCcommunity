import request from '@/utils/request'

export function getMe(params) {
  return request({
    url: process.env.WEB_API + '/about/getMe',
    method: 'get',
    params
  })
}

export function getContact(params) {
  return request({
    url: process.env.WEB_API + '/about/getContact',
    method: 'get',
    params
  })
}

export function getBlogListByUser(params) {
  return request({
    url: process.env.WEB_API + '/about/getBlogListByUser',
    method: 'post',
    data: params
  })
}

export function getQuestionListByUser(params) {
  return request({
    url: process.env.WEB_API + '/about/getQuestionListByUser',
    method: 'post',
    data: params
  })
}

export function getQuestionList(params) {
  return request({
    url: process.env.WEB_API + '/about/getQuestionList',
    method: 'post',
    data: params
  })
}

export function getUserByUid(params) {
  return request({
    url: process.env.WEB_API + '/about/getUserByUid',
    method: 'get',
    params
  })
}

export function getUserCenterByUid(params) {
  return request({
    url: process.env.WEB_API + '/about/getUserCenterByUid',
    method: 'get',
    params
  })
}

export function getUserWatchList(params) {
  return request({
    url: process.env.WEB_API + '/about/getUserWatchList',
    method: 'post',
    data: params
  })
}

export function addUserWatch(params) {
  return request({
    url: process.env.WEB_API + '/about/addUserWatch',
    method: 'post',
    data: params
  })
}

export function deleteUserWatch(params) {
  return request({
    url: process.env.WEB_API + '/about/deleteUserWatch',
    method: 'post',
    data: params
  })
}

export function userSignIn(params) {
  return request({
    url: process.env.WEB_API + '/about/userSignIn',
    method: 'post',
    data: params
  })
}


export function retroactive(params) {
  return request({
    url: process.env.WEB_API + '/about/retroactive',
    method: 'post',
    data: params
  })
}

export function getSignDataByUserUid(params) {
  return request({
    url: process.env.WEB_API + '/about/signDataByUserUid',
    method: 'get',
    params
  })
}

export function getCurrentUserCredits(params) {
  return request({
    url: process.env.WEB_API + '/about/getCurrentUserCredits',
    method: 'post',
    params
  })
}

export function checkCurrentUserWatch(params) {
  return request({
    url: process.env.WEB_API + '/about/checkCurrentUserWatch',
    method: 'post',
    data: params
  })
}


export function updateCurrentUserBackgroundImage(params) {
  return request({
    url: process.env.WEB_API + '/about/updateCurrentUserBackgroundImage',
    method: 'post',
    data: params
  })
}

export function getUserList(params) {
  return request({
    url: process.env.WEB_API + "/about/getUserList",
    method: 'post',
    data: params
  })
}

// 获取当前用户钱包金额
export function getCurrentUserAmount(params) {
  return request({
    url: process.env.WEB_API + "/about/getCurrentUserAmount",
    method: 'post',
    data: params
  })
}
