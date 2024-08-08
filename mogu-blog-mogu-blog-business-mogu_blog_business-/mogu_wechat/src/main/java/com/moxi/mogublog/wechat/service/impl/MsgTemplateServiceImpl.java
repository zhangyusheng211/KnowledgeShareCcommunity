package com.moxi.mogublog.wechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moxi.mogublog.wechat.common.utils.PageUtils;
import com.moxi.mogublog.wechat.common.utils.Query;
import com.moxi.mogublog.wechat.dao.MsgTemplateMapper;
import com.moxi.mogublog.wechat.entity.MsgTemplate;
import com.moxi.mogublog.wechat.service.MsgTemplateService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("msgTemplateService")
public class MsgTemplateServiceImpl extends ServiceImpl<MsgTemplateMapper, MsgTemplate> implements MsgTemplateService {
    @Autowired
    private WxMpService wxService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String title = (String) params.get("title");
        String name = (String) params.get("name");
        String appid = (String) params.get("appid");
        IPage<MsgTemplate> page = this.page(
                new Query<MsgTemplate>().getPage(params),
                new QueryWrapper<MsgTemplate>()
                        .eq(StringUtils.hasText(appid), "appid", appid)
                        .like(StringUtils.hasText(title), "title", title)
                        .like(StringUtils.hasText(name), "name", name)
        );

        return new PageUtils(page);
    }

    @Override
    public MsgTemplate selectByName(String name) {
        Assert.hasText(name, "模板名称不得为空");
        return this.getOne(new QueryWrapper<MsgTemplate>()
                .eq("name", name)
                .eq("status", 1)
                .last("LIMIT 1"));
    }

    @Override
    public void syncWxTemplate(String appid) throws WxErrorException {
        List<WxMpTemplate> wxMpTemplateList = wxService.getTemplateMsgService().getAllPrivateTemplate();
        List<MsgTemplate> templates = wxMpTemplateList.stream().map(item -> new MsgTemplate(item, appid)).collect(Collectors.toList());
        this.saveBatch(templates);
    }

}
