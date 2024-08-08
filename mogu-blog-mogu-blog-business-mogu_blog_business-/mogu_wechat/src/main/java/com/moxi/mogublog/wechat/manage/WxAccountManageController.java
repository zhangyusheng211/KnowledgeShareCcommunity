package com.moxi.mogublog.wechat.manage;

import com.moxi.mogublog.wechat.annotion.LoginVerify.LoginVerify;
import com.moxi.mogublog.wechat.common.utils.R;
import com.moxi.mogublog.wechat.entity.WxAccount;
import com.moxi.mogublog.wechat.service.WxAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


/**
 * 公众号账号
 *
 * @author niefy
 * @date 2020-06-17 13:56:51
 */
@RestController
@RequestMapping("/manage/wxAccount")
@Api(tags = {"公众号账号-管理后台"})
public class WxAccountManageController {
    @Resource
    private WxAccountService wxAccountService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "列表")
    public R list() {
        List<WxAccount> list = wxAccountService.list();

        return R.ok().put("list", list);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{appid}")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("id") String appid) {
        WxAccount wxAccount = wxAccountService.getById(appid);

        return R.ok().put("wxAccount", wxAccount);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody WxAccount wxAccount) {
        wxAccountService.save(wxAccount);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] appids) {
        wxAccountService.removeByIds(Arrays.asList(appids));

        return R.ok();
    }

}
