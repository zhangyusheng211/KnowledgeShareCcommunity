package com.moxi.mogublog.wechat.handler;


import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.utils.SignUtils;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.wechat.entity.WxMsg;
import com.moxi.mogublog.wechat.service.MsgReplyService;
import com.moxi.mogublog.wechat.service.WxMsgService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Binary Wang
 */
@Component
public class MsgHandler extends AbstractHandler {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    MsgReplyService msgReplyService;
    @Resource
    WxMsgService wxMsgService;
    private static final String TRANSFER_CUSTOMER_SERVICE_KEY = "人工";
    @Resource
    WebFeignClient webFeignClient;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        String content = wxMessage.getContent();
        String fromUser = wxMessage.getFromUser();
        String appid = WxMpConfigStorageHolder.get();
        String result = "";

        // 是否是登录验证码校验
        if (StringUtils.isNotEmpty(content) && (content.toUpperCase().indexOf("DL") == 0 || content.toUpperCase().indexOf("BD") == 0 )) {
            content = content.toUpperCase();
            // 用户的身份id
            String openid = wxMessage.getFromUser();
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("openid", openid);
            paramsMap.put("content", content);

            result = webFeignClient.wechatUserLogin(openid, content, SignUtils.getSign(paramsMap));

            return WxMpXmlOutMessage.TEXT()
                    .content(result)
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .build();

        }

        // 是否命中事件
        String eventText = msgReplyService.eventReplyText(wxMessage.getFromUser(), content);
        if (!org.springframework.util.StringUtils.isEmpty(eventText)) {
            return WxMpXmlOutMessage.TEXT()
                    .content(eventText)
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .build();
        }

        // 是否命中文字版自动回复
        String replyText = msgReplyService.tryAutoReplyText(appid, true, wxMessage.getFromUser(), wxMessage.getContent());
        if (!org.springframework.util.StringUtils.isEmpty(replyText)) {
            return WxMpXmlOutMessage.TEXT()
                    .content(replyText)
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .build();
        }

        // ChatGPT自动回复
        String chatGPTText = msgReplyService.chatGPTReplayText(wxMessage.getFromUser(), wxMessage.getContent());
        if (!org.springframework.util.StringUtils.isEmpty(chatGPTText)) {
            return WxMpXmlOutMessage.TEXT()
                    .content(chatGPTText)
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .build();
        }

        boolean autoReplyed = msgReplyService.tryAutoReply(appid, false, fromUser, content);
        //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
        if (TRANSFER_CUSTOMER_SERVICE_KEY.equals(content) || !autoReplyed) {
            wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.TRANSFER_CUSTOMER_SERVICE, fromUser, null));
            return WxMpXmlOutMessage
                    .TRANSFER_CUSTOMER_SERVICE()
                    .fromUser(wxMessage.getToUser())
                    .toUser(fromUser)
                    .build();
        }
        return null;

    }

}
