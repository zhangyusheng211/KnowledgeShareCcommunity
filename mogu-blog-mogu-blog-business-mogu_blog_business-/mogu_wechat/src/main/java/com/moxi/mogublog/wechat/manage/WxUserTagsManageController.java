package com.moxi.mogublog.wechat.manage;

import com.moxi.mogublog.wechat.annotion.LoginVerify.LoginVerify;
import com.moxi.mogublog.wechat.common.global.SysConf;
import com.moxi.mogublog.wechat.common.utils.R;
import com.moxi.mogublog.wechat.form.WxUserBatchTaggingForm;
import com.moxi.mogublog.wechat.form.WxUserTagForm;
import com.moxi.mogublog.wechat.service.WxUserService;
import com.moxi.mogublog.wechat.service.WxUserTagsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.tag.WxUserTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 公众号用户标签
 */
@RestController
@RequestMapping("/manage/wxUserTags")
@Api(tags = {"公众号用户标签-管理后台"})
public class WxUserTagsManageController {
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private WxUserTagsService wxUserTagsService;

    /**
     * 查询用户标签
     */
    @GetMapping("/list")
    @ApiOperation(value = "列表")
    public R list(HttpServletRequest request) throws WxErrorException {
        List<WxUserTag> wxUserTags = wxUserTagsService.getWxTags(request.getHeader(SysConf.AppId));
        return R.ok().put("list", wxUserTags);
    }

    /**
     * 修改或新增标签
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存")
    public R save(@CookieValue String appid, @RequestBody WxUserTagForm form) throws WxErrorException {
        Long tagid = form.getId();
        if (tagid == null || tagid <= 0) {
            wxUserTagsService.creatTag(appid, form.getName());
        } else {
            wxUserTagsService.updateTag(appid, tagid, form.getName());
        }
        return R.ok();
    }

    /**
     * 删除标签
     */
    @PostMapping("/delete/{tagid}")
    @ApiOperation(value = "删除标签")
    public R delete(@CookieValue String appid, @PathVariable("tagid") Long tagid) throws WxErrorException {
        if (tagid == null || tagid <= 0) {
            return R.error("标签ID不得为空");
        }
        wxUserTagsService.deleteTag(appid, tagid);
        return R.ok();
    }

    /**
     * 批量给用户打标签
     */
    @PostMapping("/batchTagging")
    @ApiOperation(value = "批量给用户打标签")
    public R batchTagging(@CookieValue String appid, @RequestBody WxUserBatchTaggingForm form) throws WxErrorException {

        wxUserTagsService.batchTagging(appid, form.getTagid(), form.getOpenidList());
        return R.ok();
    }

    /**
     * 批量移除用户标签
     */
    @PostMapping("/batchUnTagging")
    @ApiOperation(value = "批量移除用户标签")
    public R batchUnTagging(@CookieValue String appid, @RequestBody WxUserBatchTaggingForm form) throws WxErrorException {
        wxUserTagsService.batchUnTagging(appid, form.getTagid(), form.getOpenidList());
        return R.ok();
    }
}
