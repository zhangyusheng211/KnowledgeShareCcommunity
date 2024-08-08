package com.moxi.mogublog.wechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moxi.mogublog.wechat.common.utils.PageUtils;
import com.moxi.mogublog.wechat.common.utils.Query;
import com.moxi.mogublog.wechat.dao.WxQrCodeMapper;
import com.moxi.mogublog.wechat.entity.WxQrCode;
import com.moxi.mogublog.wechat.form.WxQrCodeForm;
import com.moxi.mogublog.wechat.service.WxQrCodeService;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;


@Service("wxQrCodeService")
@RequiredArgsConstructor
public class WxQrCodeServiceImpl extends ServiceImpl<WxQrCodeMapper, WxQrCode> implements WxQrCodeService {
    private final WxMpService wxService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String sceneStr = (String) params.get("sceneStr");
        String appid = (String) params.get("appid");
        IPage<WxQrCode> page = this.page(
                new Query<WxQrCode>().getPage(params),
                new QueryWrapper<WxQrCode>()
                        .eq(StringUtils.hasText(appid), "appid", appid)
                        .like(StringUtils.hasText(sceneStr), "scene_str", sceneStr)
        );

        return new PageUtils(page);
    }

    /**
     * 创建公众号带参二维码
     *
     * @param appid
     * @param form
     * @return
     */
    @Override
    public WxMpQrCodeTicket createQrCode(String appid, WxQrCodeForm form) throws WxErrorException {
        WxMpQrCodeTicket ticket;
        if (form.getIsTemp()) {//创建临时二维码
            ticket = wxService.getQrcodeService().qrCodeCreateTmpTicket(form.getSceneStr(), form.getExpireSeconds());
        } else {//创建永久二维码
            ticket = wxService.getQrcodeService().qrCodeCreateLastTicket(form.getSceneStr());
        }
        WxQrCode wxQrCode = new WxQrCode(form, appid);
        wxQrCode.setTicket(ticket.getTicket());
        wxQrCode.setUrl(ticket.getUrl());
        if (form.getIsTemp()) {
            wxQrCode.setExpireTime(new Date(System.currentTimeMillis() + ticket.getExpireSeconds() * 1000L));
        }
        this.save(wxQrCode);
        return ticket;
    }

}
