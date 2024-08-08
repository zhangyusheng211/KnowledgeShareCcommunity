package com.moxi.mogublog.wechat.manage;

import com.moxi.mogublog.wechat.annotion.LoginVerify.LoginVerify;
import com.moxi.mogublog.wechat.common.global.SysConf;
import com.moxi.mogublog.wechat.common.utils.PageUtils;
import com.moxi.mogublog.wechat.common.utils.R;
import com.moxi.mogublog.wechat.entity.WxUser;
import com.moxi.mogublog.wechat.service.WxUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;


/**
 * 用户表
 *
 * @author niefy
 * @email niefy@qq.com
 * @date 2020-03-07 13:55:23
 */
@RestController
@RequestMapping("/manage/wxUser")
@Api(tags = {"公众号粉丝-管理后台"})
public class WxUserManageController {
    @Autowired
    private WxUserService userService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "列表")
    public R list(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        params.put("appid", request.getHeader(SysConf.AppId));
        PageUtils page = new PageUtils(userService.queryPage(params));

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @PostMapping("/listByIds")
    @ApiOperation(value = "列表-ID查询")
    public R listByIds(@CookieValue String appid, @RequestBody String[] openids) {
        Collection<WxUser> users = userService.listByIds(Arrays.asList(openids));
        return R.ok().put(users);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{openid}")
    @ApiOperation(value = "详情")
    public R info(@CookieValue String appid, @PathVariable("openid") String openid) {
        WxUser wxUser = userService.getById(openid);

        return R.ok().put("wxUser", wxUser);
    }

    /**
     * 同步用户列表
     */
    @PostMapping("/syncWxUsers")
    @ApiOperation(value = "同步用户列表到数据库")
    public R syncWxUsers(@CookieValue String appid) {
        userService.syncWxUsers(appid);

        return R.ok("任务已建立");
    }


    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除")
    public R delete(@CookieValue String appid, @RequestBody String[] ids) {
        userService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
