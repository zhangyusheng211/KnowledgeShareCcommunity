package com.moxi.mogublog.wechat.manage;

import com.moxi.mogublog.wechat.annotion.LoginVerify.LoginVerify;
import com.moxi.mogublog.wechat.common.global.SysConf;
import com.moxi.mogublog.wechat.common.utils.PageUtils;
import com.moxi.mogublog.wechat.common.utils.R;
import com.moxi.mogublog.wechat.entity.MsgTemplate;
import com.moxi.mogublog.wechat.form.TemplateMsgBatchForm;
import com.moxi.mogublog.wechat.service.MsgTemplateService;
import com.moxi.mogublog.wechat.service.TemplateMsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;


/**
 * 消息模板
 *
 * @author niefy
 * @email niefy@qq.com
 * @date 2019-11-12 18:30:15
 */
@RestController
@RequestMapping("/manage/msgTemplate")
@Api(tags = {"消息模板-管理后台", "模板消息的模板"})
public class MsgTemplateManageController {
    @Resource
    private MsgTemplateService msgTemplateService;
    @Resource
    private TemplateMsgService templateMsgService;
    @Resource
    private WxMpService wxMpService;

    /**
     * 列表
     */
    @LoginVerify
    @GetMapping("/list")
    @ApiOperation(value = "列表")
    public R list(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        params.put("appid", request.getHeader(SysConf.AppId));
        PageUtils page = msgTemplateService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "详情-通过ID")
    public R info(@PathVariable("id") Long id) {
        MsgTemplate msgTemplate = msgTemplateService.getById(id);
        return R.ok().put("msgTemplate", msgTemplate);
    }

    /**
     * 信息
     */
    @GetMapping("/getByName")
    @ApiOperation(value = "详情-通过名称")
    public R getByName(String name) {
        MsgTemplate msgTemplate = msgTemplateService.selectByName(name);

        return R.ok().put("msgTemplate", msgTemplate);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody MsgTemplate msgTemplate) {
        msgTemplateService.save(msgTemplate);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody MsgTemplate msgTemplate) {
        msgTemplateService.updateById(msgTemplate);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] ids) {
        msgTemplateService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 同步公众号模板
     */
    @PostMapping("/syncWxTemplate")
    @ApiOperation(value = "同步公众号模板")
    public R syncWxTemplate(@CookieValue String appid) throws WxErrorException {
        this.wxMpService.switchoverTo(appid);
        msgTemplateService.syncWxTemplate(appid);
        return R.ok();
    }

    /**
     * 批量向用户发送模板消息
     * 通过用户筛选条件（一般使用标签筛选），将消息发送给数据库中所有符合筛选条件的用户
     */
    @PostMapping("/sendMsgBatch")
    @ApiOperation(value = "批量向用户发送模板消息", notes = "将消息发送给数据库中所有符合筛选条件的用户")
    public R sendMsgBatch(@CookieValue String appid, @RequestBody TemplateMsgBatchForm form) {
        this.wxMpService.switchoverTo(appid);
        templateMsgService.sendMsgBatch(form, appid);
        return R.ok();
    }


}
