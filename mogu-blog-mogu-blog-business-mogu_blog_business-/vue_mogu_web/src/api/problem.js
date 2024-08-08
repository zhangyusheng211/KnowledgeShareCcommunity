import request from '@/utils/request'

export function getProblemList(params) {
  return request({
    url: process.env.WEB_API + '/problem/getList',
    method: 'post',
    data: params
  })
}

export function getProblemQueryList(params) {
  return request({
    url: process.env.WEB_API + '/problem/queryPage',
    method: 'post',
    data: params
  })
}


export function getProblemTagList(params) {
  return request({
    url: process.env.WEB_API + '/problem/getTagList',
    method: 'post',
    data: params
  })
}


export function getAllProblemTagList(params) {
  return request({
    url: process.env.WEB_API + '/problem/getAllTagList',
    method: 'post',
    data: params
  })
}


export function getProblem(params) {
  return request({
    url: process.env.WEB_API + '/problem/getProblem',
    method: 'post',
    data: params
  })
}

export function addProblem(params) {
  return request({
    url: process.env.WEB_API + '/problem/add',
    method: 'post',
    data: params
  })
}

export function editProblem(params) {
  return request({
    url: process.env.WEB_API + '/problem/edit',
    method: 'post',
    data: params
  })
}

export function deleteProblem(params) {
  return request({
    url: process.env.WEB_API + '/problem/delete',
    method: 'post',
    data: params
  })
}

export function deleteBatchProblem(params) {
  return request({
    url: process.env.WEB_API + '/problem/deleteBatch',
    method: 'post',
    data: params
  })
}

// 问题掌握
export function problemDegree(params) {
  return request({
    url: process.env.WEB_API + '/problem/problemDegree',
    method: 'post',
    data: params
  })
}

// 修改答案
export function editAnswer(params) {
  return request({
    url: process.env.WEB_API + '/problem/editAnswer',
    method: 'post',
    data: params
  })
}

// 生成试卷(题目分类)
export function builderExam(params) {
  return request({
    url: process.env.WEB_API + '/problem/builderExam',
    method: 'post',
    data: params
  })
}
