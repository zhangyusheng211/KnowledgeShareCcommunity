package com.moxi.mogublog.wechat.handler;

import com.moxi.mogublog.wechat.service.MsgReplyService;
import com.moxi.mogublog.wechat.service.WxUserService;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Binary Wang
 */
@Component
public class SubscribeHandler extends AbstractHandler {
    @Resource
    MsgReplyService msgReplyService;
    @Resource
    WxUserService userService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser() + "，事件：" + wxMessage.getEventKey());
        String appid = WxMpConfigStorageHolder.get();
        this.logger.info("appid:{}", appid);
        userService.refreshUserInfo(wxMessage.getFromUser(), appid);

        // 是否命中文字版自动回复
        String replyText = msgReplyService.tryAutoReplyText(appid, true, wxMessage.getFromUser(), wxMessage.getEvent());
        if (!StringUtils.isEmpty(replyText)) {
            return WxMpXmlOutMessage.TEXT()
                    .content(replyText)
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .build();
        }

        msgReplyService.tryAutoReply(appid, true, wxMessage.getFromUser(), wxMessage.getEvent());

        if (StringUtils.hasText(wxMessage.getEventKey())) {// 处理特殊事件，如用户扫描带参二维码关注
            msgReplyService.tryAutoReply(appid, true, wxMessage.getFromUser(), wxMessage.getEventKey());
        }
        return null;
    }

    /**
     * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
     */
    protected WxMpXmlOutMessage handleSpecial(WxMpXmlMessage wxMessage) {
        this.logger.info("特殊请求-新关注用户 OPENID: " + wxMessage.getFromUser());
        //对关注事件和扫码事件分别处理
        String appid = WxMpConfigStorageHolder.get();
        userService.refreshUserInfo(wxMessage.getFromUser(), appid);
        msgReplyService.tryAutoReply(appid, true, wxMessage.getFromUser(), wxMessage.getEvent());
        if (StringUtils.hasText(wxMessage.getEventKey())) {
            msgReplyService.tryAutoReply(appid, true, wxMessage.getFromUser(), wxMessage.getEventKey());
        }
        return null;
    }

}
