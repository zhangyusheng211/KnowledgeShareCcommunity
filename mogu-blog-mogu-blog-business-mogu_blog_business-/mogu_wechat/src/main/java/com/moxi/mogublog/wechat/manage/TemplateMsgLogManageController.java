package com.moxi.mogublog.wechat.manage;

import com.moxi.mogublog.wechat.annotion.LoginVerify.LoginVerify;
import com.moxi.mogublog.wechat.common.global.SysConf;
import com.moxi.mogublog.wechat.common.utils.PageUtils;
import com.moxi.mogublog.wechat.common.utils.R;
import com.moxi.mogublog.wechat.entity.TemplateMsgLog;
import com.moxi.mogublog.wechat.service.TemplateMsgLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;


/**
 * 模版消息发送记录
 *
 * @author niefy
 * @email niefy@qq.com
 * @date 2019-11-12 18:30:15
 */
@RestController
@RequestMapping("/manage/templateMsgLog")
@Api(tags = {"模板消息发送记录-管理后台"})
public class TemplateMsgLogManageController {
    @Resource
    private TemplateMsgLogService templateMsgLogService;
    @Resource
    private WxMpService wxMpService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "列表")
    public R list(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        params.put("appid", request.getHeader(SysConf.AppId));
        PageUtils page = templateMsgLogService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{logId}")
    @ApiOperation(value = "详情")
    public R info(@CookieValue String appid, @PathVariable("logId") Integer logId) {
        TemplateMsgLog templateMsgLog = templateMsgLogService.getById(logId);
        return R.ok().put("templateMsgLog", templateMsgLog);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存")
    public R save(@CookieValue String appid, @RequestBody TemplateMsgLog templateMsgLog) {
        templateMsgLogService.save(templateMsgLog);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改")
    public R update(@CookieValue String appid, @RequestBody TemplateMsgLog templateMsgLog) {
        templateMsgLogService.updateById(templateMsgLog);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除")
    public R delete(@CookieValue String appid, @RequestBody Integer[] logIds) {
        templateMsgLogService.removeByIds(Arrays.asList(logIds));

        return R.ok();
    }

}
