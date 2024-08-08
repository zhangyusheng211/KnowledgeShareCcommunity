import request from '@/utils/request';

/**
 * 获取媒资播放记录
 * @param params
 * @returns {*}
 */
export function getMediaPlayRecord(params) {
  return request({
    url: process.env.WEB_API + '/media/playMediaRecord/getMediaPlayRecord',
    method: 'post',
    data: params
  });
}

/**
 * 插入或更新媒资记录
 * @param params
 * @returns {*}
 */
export function insertOrUpdate(params) {
  return request({
    url: process.env.WEB_API + '/media/playMediaRecord/insertOrUpdate',
    method: 'post',
    data: params
  });
}
