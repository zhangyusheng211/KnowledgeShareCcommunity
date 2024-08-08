package com.moxi.mogublog.wechat.handler;

import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.wechat.entity.WxMsg;
import com.moxi.mogublog.wechat.service.WxMsgService;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Binary Wang
 */
@Component
public class LogHandler extends AbstractHandler {
    @Resource
    WxMsgService wxMsgService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        try {
            this.logger.debug("\n接收到请求消息，内容：{}", JsonUtils.objectToJson(wxMessage));
            wxMsgService.addWxMsg(new WxMsg(wxMessage));
        } catch (Exception e) {
            this.logger.error("记录消息异常", e);
        }

        return null;
    }

}
