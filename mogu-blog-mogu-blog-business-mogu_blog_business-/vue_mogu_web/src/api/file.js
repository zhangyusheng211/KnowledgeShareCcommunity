import request from '@/utils/request'

// 查询用户最近上传的文件
export function getUserRecentlyUploadFile(query) {
  return request({
    url: process.env.PICTURE_API + '/file/getUserRecentlyUploadFile',
    method: 'post',
    data: query
  })
}
