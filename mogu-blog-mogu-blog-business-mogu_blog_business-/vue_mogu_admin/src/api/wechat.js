import request from '@/utils/request'

////////////// 账号相关 /////////////////////
export function getWxAccountList(params) {
  return request({
    url: process.env.WECHAT_API + '/manage/wxAccount/list',
    method: 'get',
    params
  })
}

export function addWxAccount(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxAccount/save',
        method: 'post',
        data: params
    })
}


export function deleteWxAccount(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxAccount/delete',
        method: 'post',
        data: params
    })
}


////////////// 获取粉丝相关 /////////////////////
export function getWxUserList(params) {
  return request({
    url: process.env.WECHAT_API + '/manage/wxUser/list',
    method: 'get',
    params
  })
}

export function deleteWxUser(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxUser/delete',
        method: 'post',
        data: params
    })
}

export function syncWxUsers(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxUser/syncWxUsers',
        method: 'post',
        data: params
    });
}

export function listByIdsWxUsers(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxUser/listByIds',
        method: 'post',
        data: params
    });
}


////////////// 获取标签相关 //////////////
export function getWxUserTags(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxUserTags/list',
        method: 'get',
        params
    });
}

export function deleteWxUserTags(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxUserTags/delete/' + params,
        method: 'post'
    });
}

export function saveWxUserTags(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxUserTags/save',
        method: 'post',
        data: params
    });
}

export function taggingWxUserTags(options,params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxUserTags/' + options,
        method: 'post',
        data: params
    });
}


////////////// 获取消息相关 //////////////
export function getWxMsgList(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxMsg/list',
        method: 'get',
        params
    });
}

export function deleteWxMsg(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxMsg/delete/' + params,
        method: 'post'
    });
}

export function saveWxMsg(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxMsg/save',
        method: 'post',
        data: params
    });
}

export function replyWxMsg(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxMsg/reply',
        method: 'post',
        data: params
    });
}



////////////// 自动规则回复相关 //////////////
export function getWxMsgReplyRules(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/msgReplyRule/list',
        method: 'get',
        params
    })
}

export function getWxMsgReplyRuleInfo(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/msgReplyRule/info/' + params,
        method: 'get'
    });
}

export function insertOrUpdateMsgReplyRule(option, params) {
    return request({
        url: process.env.WECHAT_API + '/manage/msgReplyRule/' + option,
        method: 'post',
        data: params
    });
}

export function deleteWxMsgReplyRules(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/msgReplyRule/delete',
        method: 'post',
        data: params
    });
}


////////////// 菜单相关 //////////////
export function getWxMenu(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxMenu/getMenu',
        method: 'get',
        params
    });
}

export function editWxMenu(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxMenu/updateMenu',
        method: 'post',
        data: params
    });
}

////////////// 素材相关 //////////////
export function getWxAssets(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxAssets/materialFileBatchGet',
        method: 'get',
        params
    });
}

/**
 * 添加图文永久素材
 * @param params
 * @returns {*}
 */
export function materialFileUpload(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxAssets/materialFileUpload',
        method: 'get',
        headers: { 'Content-Type': 'multipart/form-data' },
        params
    });
}

export function getWxAssetsCount(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxAssets/materialCount',
        method: 'get',
        params
    });
}

export function getNewsWxAssets(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxAssets/materialNewsBatchGet',
        method: 'get',
        params
    });
}

export function deleteWxAssets(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxAssets/materialDelete',
        method: 'post',
        data: params
    });
}

/**
 * 添加图文永久素材
 * @param params
 * @returns {*}
 */
export function materialNewsUpload(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxAssets/materialNewsUpload',
        method: 'post',
        data: params
    });
}

/**
 * 修改图文素材
 * @param params
 * @returns {*}
 */
export function materialArticleUpdate(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxAssets/materialArticleUpdate',
        method: 'post',
        data: params
    });
}

////////////// 带参二维码相关 //////////////
export function getWxQrCodeList(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxQrCode/list',
        method: 'get',
        params
    });
}

export function addWxQrCode(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxQrCode/createTicket',
        method: 'post',
        data: params
    });
}

export function deleteWxQrCode(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/wxQrCode/delete',
        method: 'post',
        data: params
    });
}

////////////// 模板相关 //////////////
export function getWxMsgTemplate(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/msgTemplate/list',
        method: 'get',
        params
    });
}

export function deleteWxMsgTemplate(params) {
    return request({
        url: process.env.WECHAT_API + 'manage/msgTemplate/delete',
        method: 'post',
        data: params
    });
}

export function syncWxMsgTemplate(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/msgTemplate/syncWxTemplate',
        method: 'post',
        data: params
    });
}

/**
 * 保存模板
 * @param params
 * @returns {*}
 */
export function saveWxMsgTemplate(params) {
    return request({
        url: process.env.WECHAT_API + 'manage/msgTemplate/save',
        method: 'post',
        data: params
    });
}

/**
 * 插入或更新模板回复
 * @param option
 * @param params
 * @returns {*}
 */
export function insertOrUpdateMsgReply(option, params) {
    return request({
        url: process.env.WECHAT_API + '/manage/msgTemplate/' + option,
        method: 'post',
        data: params
    });
}


/**
 * 消息模板详情
 * @param params
 * @returns {*}
 */
export function msgTemplateManageInfo(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/msgTemplate/info/params',
        method: 'post',
        data: params
    });
}

/**
 * 批量向用户发送模板消息
 * 通过用户筛选条件（一般使用标签筛选），将消息发送给数据库中所有符合筛选条件的用户
 * @param params
 * @returns {*}
 */
export function sendMsgBatch(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/msgTemplate/sendMsgBatch',
        method: 'post',
        data: params
    });
}


export function templateMsgLogList(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/templateMsgLog/list',
        method: 'get',
        params
    });
}

export function templateMsgLogInfo(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/templateMsgLog/info/' + params,
        method: 'get'
    });
}

export function insertOrUpdateTemplateMsgLog(option, params) {
    return request({
        url: process.env.WECHAT_API + '/manage/templateMsgLog/' + option,
        method: 'post',
        data: params
    });
}

export function deleteTemplateMsgLog(params) {
    return request({
        url: process.env.WECHAT_API + '/manage/msgTemplate/sendMsgBatch',
        method: 'post',
        data: params
    });
}

