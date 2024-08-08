package com.moxi.mogublog.web.restapi;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.annotion.FeignSecurity.FeignSecurity;
import com.moxi.mogublog.commons.entity.CreditsLog;
import com.moxi.mogublog.commons.schema.CreditsChangeRequest;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.CreditsLogService;
import com.moxi.mogublog.commons.vo.CreditsLogVO;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mougblog.base.enums.ECreditType;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.vo.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 积分接口
 *
 * @author 遇见
 * @date 2018-09-04
 */
@RestController
@RequestMapping("/web/credits")
@Api(value = "积分接口", tags = {"积分接口"})
@Slf4j
public class CreditsRestApi {
    /**
     * 积分服务
     */
    @Resource
    CreditsLogService creditsLogService;
    @Resource
    UserService userService;

    /**
     * 查询积分流水
     *
     * @param pageInfo
     * @return
     */
    @ApiOperation(value = "查询积分流水", notes = "查询积分流水")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数目", required = true)
    })
    @RequestMapping(value = "/list")
    public String getCreditsLogByUserUid(@RequestBody PageInfo pageInfo) {
        Page<CreditsLogVO> pageList = creditsLogService.queryList(pageInfo);
        return ResultUtil.result(SysConf.SUCCESS, pageList);
    }

    /**
     * 查询积分月榜
     * @return
     */
    @ApiOperation(value = "查询积分月榜", notes = "查询积分月榜")
    @GetMapping(value = "/getLeaderMonth")
    public String getLeaderMonth(@ApiParam(name = "refresh", value = "是否刷新配置", required = false) @RequestParam(name = "refresh", required = false, defaultValue = "false") Boolean refresh) {
        List<CreditsLog> leaderMonth = creditsLogService.getLeaderMonth(refresh);
        return ResultUtil.result(SysConf.SUCCESS, leaderMonth);
    }

    /**
     * 查询积分周榜
     * @return
     */
    @ApiOperation(value = "查询积分周榜", notes = "查询积分周榜")
    @GetMapping(value = "/getLeaderWeek")
    public String getLeaderWeek(@ApiParam(name = "refresh", value = "是否刷新配置", required = false) @RequestParam(name = "refresh", required = false, defaultValue = "false") Boolean refresh) {
        List<CreditsLog> leaderWeek = creditsLogService.getLeaderWeek(refresh);
        return ResultUtil.result(SysConf.SUCCESS, leaderWeek);
    }

    @FeignSecurity
    @ApiOperation(value = "用户积分变更", notes = "用户积分变更")
    @PostMapping(value = "/creditsChange")
    public String creditsChange(@RequestBody CreditsChangeRequest creditsChangeRequest) {
        userService.addUserCredits(creditsChangeRequest.getCreditType().getAction(), creditsChangeRequest.getChangeCredits(), creditsChangeRequest.getResourceUid(), RequestHolder.getUserUid());
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }
}
