import request from '@/utils/request'

export function getMediaInfoList(params) {
  return request({
    url: process.env.ADMIN_API + '/media/info/list',
    method: 'post',
    data: params
  });
}

export function getMediaInfo(data) {
  return request({
    url: process.env.ADMIN_API + '/media/info/info',
    method: 'post',
    data: data
  });
}

export function addMediaInfo(params) {
  return request({
    url: process.env.ADMIN_API + '/media/info/add',
    method: 'post',
    data: params
  });
}

export function editMediaInfo(params) {
  return request({
    url: process.env.ADMIN_API + '/media/info/edit',
    method: 'post',
    data: params
  });
}

/**
 * 转化媒资和视频
 * @param params
 * @returns {*}
 */
export function transformMediaInfo(params) {
    return request({
        url: process.env.ADMIN_API + '/media/info/transform',
        method: 'post',
        data: params
    });
}



export function delMovieInfo(params) {
  return request({
    url: process.env.ADMIN_API + '/media/info/deleteBatch',
    method: 'post',
    data: params
  });
}
