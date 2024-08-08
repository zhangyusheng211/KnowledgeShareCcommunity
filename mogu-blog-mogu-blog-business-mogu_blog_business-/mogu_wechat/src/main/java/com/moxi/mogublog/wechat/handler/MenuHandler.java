package com.moxi.mogublog.wechat.handler;

import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.utils.DateUtils;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.SignUtils;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.wechat.service.MsgReplyService;
import com.moxi.mougblog.base.global.BaseSysConf;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Binary Wang
 */
@Component
public class MenuHandler extends AbstractHandler {

    @Resource
    MsgReplyService msgReplyService;
    @Resource
    WebFeignClient webFeignClient;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {
        if (WxConsts.EventType.VIEW.equals(wxMessage.getEvent())) {
            return null;
        }
        String appid = WxMpConfigStorageHolder.get();
        String eventKey = wxMessage.getEventKey();
        String conetnt = wxMessage.getContent();
        // 用户的身份id
        String openid = wxMessage.getFromUser();

        logger.info("菜单事件：" + eventKey + ", 文本内容：" + conetnt);

        // TODO 调用其它接口，进行返回
        // 包含用户行为动作
        String result = "";

        if (StringUtils.isNotEmpty(eventKey) && eventKey.indexOf("EVENT_") == 0) {
            // 命中了自定义动作
            switch (eventKey) {
                // 签到
                case "EVENT_SIGN_IN": {
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("openid", openid);

                    String resultStr = webFeignClient.wechatUserSignIn(openid, SignUtils.getSign(paramsMap));
                    Map<String, Object> map = JsonUtils.jsonToMap(resultStr);
                    if (map != null) {
                        if (BaseSysConf.SUCCESS.equals(map.get(BaseSysConf.CODE))) {
                            result = "网站签到成功❤  签到时间:" + DateUtils.getNowTime() + "🎉";
                        } else {
                            result = map.get(BaseSysConf.MESSAGE).toString();
                        }
                    }
                }
                ;
                break;
                // 随机一题
                case "EVENT_RANDOM_PROBLEM": {
                    // 随机一题
                    String resultStr = webFeignClient.randomProblem();
                    Map<String, Object> map = JsonUtils.jsonToMap(resultStr);
                    if (map != null) {
                        if (BaseSysConf.SUCCESS.equals(map.get(BaseSysConf.CODE))) {
                            Object problemObj = map.get(BaseSysConf.DATA);
                            if (problemObj != null) {
                                Map<String, Object> problemMap = JsonUtils.objectToMap(problemObj);

                                Float problemOid = Float.valueOf(problemMap.get(BaseSysConf.OID).toString());
                                result = "题目：" + problemMap.get(BaseSysConf.TITLE) + "\n\n" +
                                        "答案：" + StringUtils.html2Text(problemMap.get("answer").toString()) + "\n\n" +
                                        "<a href='https://www.moguit.cn/cInfo/" + problemOid.intValue() + "'>点击打开面经详情🎉</a>";

                            } else {
                                result = "获取面经失败，请联系管理员";
                            }
                        } else {
                            result = map.get(BaseSysConf.MESSAGE).toString();
                        }
                    }

//                    result = "每日一题❤！\n" + "<a href='https://www.moguit.cn/cInfo/" + blogOid + "'>点击跳转</a>";
                }
                ;
                break;
                // 随机一篇博客
                case "EVENT_RANDOM_BLOG": {

                    // 随机一篇博客
                    String resultStr = webFeignClient.randomBlog();
                    Map<String, Object> map = JsonUtils.jsonToMap(resultStr);
                    if (map != null) {
                        if (BaseSysConf.SUCCESS.equals(map.get(BaseSysConf.CODE))) {
                            Object problemObj = map.get(BaseSysConf.DATA);
                            if (problemObj != null) {
                                Map<String, Object> problemMap = JsonUtils.objectToMap(problemObj);

                                Float blogOid = Float.valueOf(problemMap.get(BaseSysConf.OID).toString());
                                result = "题目：" + problemMap.get(BaseSysConf.TITLE) + "\n\n" +
                                        "简介：" + StringUtils.html2Text(problemMap.get("summary").toString()) + "\n\n" +
                                        "<a href='https://www.moguit.cn/info/" + blogOid.intValue() + "'>点击打开文章详情🎉</a>";

                            } else {
                                result = "获取文章失败，请联系管理员";
                            }
                        } else {
                            result = map.get(BaseSysConf.MESSAGE).toString();
                        }
                    }
                }
                ;
                break;
            }

            return WxMpXmlOutMessage.TEXT()
                    .content(result)
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .build();
        }

        msgReplyService.tryAutoReply(appid, true, wxMessage.getFromUser(), eventKey);
        return null;
    }


}
