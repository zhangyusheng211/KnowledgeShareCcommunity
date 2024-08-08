package com.moxi.mogublog.sms.languageModel.impl;


import com.moxi.mogublog.commons.entity.SecretConfig;
import com.moxi.mogublog.sms.languageModel.LanguageModelService;
import com.moxi.mogublog.sms.languageModel.annotation.LanguageModelType;
import com.moxi.mogublog.sms.languageModel.impl.XingHuo.*;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.utils.text.Convert;
import com.moxi.mogublog.xo.service.SecretConfigService;
import com.moxi.mougblog.base.enums.ELanguageModelType;
import com.moxi.mougblog.base.enums.ESecretType;
import com.moxi.mougblog.base.global.BaseSysConf;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.UUID;

/**
 * 星火大模型实现服务
 * X
 *
 * @author 陌溪
 */
@Service
@LanguageModelType({ELanguageModelType.XING_HUO})
@Slf4j
public class XingHuoService implements LanguageModelService {

    @Resource
    private SecretConfigService secretConfigService;

    @Resource
    private XfXhStreamClient xfXhStreamClient;

//    @Resource
//    private XfXhConfig xfXhConfig;

    @Override
    public String getTextAnswer(String question) {
        // 如果是无效字符串，则不对大模型进行请求
        if (StringUtils.isBlank(question)) {
            return "无效问题，请重新输入";
        }
        // 获取连接令牌
        if (!xfXhStreamClient.operateToken(XfXhStreamClient.GET_TOKEN_STATUS)) {
            return "当前大模型连接数过多，请稍后再试";
        }

        // 获取密钥配置，拿到结果
        SecretConfig secretConfig = secretConfigService.getSecretConfig(ESecretType.LANGUAGE_MODEL.getType(), ELanguageModelType.XING_HUO.getType());
        // 组装密钥信息
        XfXhConfig xfXhConfig = new XfXhConfig();
        xfXhConfig.setAppId(secretConfig.getBizId());
        xfXhConfig.setHostUrl(secretConfig.getRequestUrl());
        xfXhConfig.setApiSecret(secretConfig.getBizSecret());
        xfXhConfig.setApiKey(secretConfig.getBizKey());
        // 获取扩展配置
        if (StringUtils.isNotEmpty(secretConfig.getExtra())) {
            XingHuoExtraSetting xingHuoExtraSetting = JsonUtils.jsonToPojo(secretConfig.getExtra(), XingHuoExtraSetting.class);
            if (xingHuoExtraSetting != null) {
                xfXhConfig.setMaxResponseTime(xingHuoExtraSetting.getMaxResponseTime());
                xfXhConfig.setDomain(xingHuoExtraSetting.getDomain());
            }
        } else {
            xfXhConfig.setMaxResponseTime(30);
            xfXhConfig.setDomain("generalv2");
        }

        // 创建消息对象
        MsgDTO msgDTO = MsgDTO.createUserMsg(question);
        // 创建监听器
        XfXhWebSocketListener listener = new XfXhWebSocketListener();
        // 发送问题给大模型，生成 websocket 连接
        WebSocket webSocket = xfXhStreamClient.sendMsg(xfXhConfig, UUID.randomUUID().toString().substring(0, 10), Collections.singletonList(msgDTO), listener);
        if (webSocket == null) {
            // 归还令牌
            xfXhStreamClient.operateToken(XfXhStreamClient.BACK_TOKEN_STATUS);
            return "系统内部错误，请联系管理员";
        }
        try {
            int count = 0;
            // 为了避免死循环，设置循环次数来定义超时时长
            int maxCount = xfXhConfig.getMaxResponseTime() * 5;
            while (count <= maxCount) {
                Thread.sleep(200);
                if (listener.isWsCloseFlag()) {
                    break;
                }
                count++;
            }
            if (count > maxCount) {
                return "大模型响应超时，请联系管理员";
            }
            // 响应大模型的答案
            return listener.getAnswer().toString();
        } catch (InterruptedException e) {
            log.error("错误：" + e.getMessage());
            return "系统内部错误，请联系管理员";
        } finally {
            // 关闭 websocket 连接
            webSocket.close(1000, "");
            // 归还令牌
            xfXhStreamClient.operateToken(XfXhStreamClient.BACK_TOKEN_STATUS);
        }
    }
}
