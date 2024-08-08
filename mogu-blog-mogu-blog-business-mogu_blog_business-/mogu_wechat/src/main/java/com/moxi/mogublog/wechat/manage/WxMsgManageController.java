package com.moxi.mogublog.wechat.manage;

import com.moxi.mogublog.wechat.annotion.LoginVerify.LoginVerify;
import com.moxi.mogublog.wechat.common.global.SysConf;
import com.moxi.mogublog.wechat.common.utils.PageUtils;
import com.moxi.mogublog.wechat.common.utils.R;
import com.moxi.mogublog.wechat.entity.WxMsg;
import com.moxi.mogublog.wechat.form.WxMsgReplyForm;
import com.moxi.mogublog.wechat.service.MsgReplyService;
import com.moxi.mogublog.wechat.service.WxMsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;


/**
 * 微信消息
 *
 * @author niefy
 * @date 2020-05-14 17:28:34
 */
@RestController
@RequestMapping("/manage/wxMsg")
@Api(tags = {"公众号消息记录-管理后台"})
public class WxMsgManageController {
    @Autowired
    private WxMsgService wxMsgService;
    @Autowired
    private MsgReplyService msgReplyService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "列表")
    public R list(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        params.put("appid", request.getHeader(SysConf.AppId));
        PageUtils page = wxMsgService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "详情")
    public R info(@CookieValue String appid, @PathVariable("id") Long id) {
        WxMsg wxMsg = wxMsgService.getById(id);

        return R.ok().put("wxMsg", wxMsg);
    }

    /**
     * 回复
     */
    @PostMapping("/reply")
    @ApiOperation(value = "回复")
    public R reply(@CookieValue String appid, @RequestBody WxMsgReplyForm form) {

        msgReplyService.reply(form.getOpenid(), form.getReplyType(), form.getReplyContent());
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除")
    public R delete(@CookieValue String appid, @RequestBody Long[] ids) {
        wxMsgService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
