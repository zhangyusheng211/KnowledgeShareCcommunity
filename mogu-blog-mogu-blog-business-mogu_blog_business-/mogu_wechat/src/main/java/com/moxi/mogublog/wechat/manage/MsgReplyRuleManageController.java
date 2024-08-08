package com.moxi.mogublog.wechat.manage;

import com.moxi.mogublog.wechat.annotion.LoginVerify.LoginVerify;
import com.moxi.mogublog.wechat.common.global.SysConf;
import com.moxi.mogublog.wechat.common.utils.PageUtils;
import com.moxi.mogublog.wechat.common.utils.R;
import com.moxi.mogublog.wechat.entity.MsgReplyRule;
import com.moxi.mogublog.wechat.service.MsgReplyRuleService;
import com.moxi.mougblog.base.holder.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;


/**
 * 自动回复规则
 *
 * @author niefy
 * @email niefy@qq.com
 * @date 2019-11-12 18:30:15
 */
@RestController
@RequestMapping("/manage/msgReplyRule")
@Api(tags = {"自动回复规则-管理后台"})
public class MsgReplyRuleManageController {
    @Resource
    private MsgReplyRuleService msgReplyRuleService;
    @Resource
    private WxMpService wxMpService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "列表")
    public R list(HttpServletRequest request, @RequestParam Map<String, Object> params) {

        params.put("appid", request.getHeader(SysConf.AppId));
        PageUtils page = msgReplyRuleService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{ruleId}")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("ruleId") Integer ruleId) {
        MsgReplyRule msgReplyRule = msgReplyRuleService.getById(ruleId);

        return R.ok().put("msgReplyRule", msgReplyRule);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody MsgReplyRule msgReplyRule) {
        msgReplyRuleService.save(msgReplyRule);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody MsgReplyRule msgReplyRule) {
        msgReplyRuleService.updateById(msgReplyRule);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody Integer[] ruleIds) {
        msgReplyRuleService.removeByIds(Arrays.asList(ruleIds));

        return R.ok();
    }

}
