package com.moxi.mogublog.web.restapi;

import cn.hutool.http.HttpUtil;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.annotion.UserAuth.UserAuth;
import com.moxi.mogublog.utils.RegexUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.web.annotion.PublishLimitVerify.PublishLimitVerify;
import com.moxi.mogublog.web.annotion.SubmitVerify.SubmitVerify;
import com.moxi.mogublog.web.annotion.checkRegexVerify.CheckRegexVerify;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.UserMomentService;
import com.moxi.mogublog.xo.service.UserMomentTopicService;
import com.moxi.mogublog.commons.vo.UserMomentTopicVO;
import com.moxi.mogublog.commons.vo.UserMomentVO;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户动态RestApi
 *
 * @author 陌溪
 * @date 2021年8月8日15:11:47
 */
@RestController
@RequestMapping("/userMoment")
@Api(value = "用户动态相关接口", tags = {"用户动态相关接口"})
@Slf4j
public class UserMomentRestApi {

    @Resource
    UserMomentService userMomentService;
    @Resource
    UserMomentTopicService userMomentTopicService;

    @ApiOperation(value = "获取用户动态列表", notes = "获取用户动态列表", response = String.class)
    @PostMapping("/getUserMomentList")
    public String getUserMomentList(@Validated({GetList.class}) @RequestBody UserMomentVO userMomentVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(userMomentService.getPageList(userMomentVO));
    }

    @ApiOperation(value = "获取用户动态列表", notes = "获取用户动态列表", response = String.class)
    @PostMapping("/list")
    public String listToWeb(@Validated({GetList.class}) @RequestBody UserMomentVO userMomentVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(userMomentService.list(userMomentVO));
    }


    @ApiOperation(value = "获取用户动态话题列表", notes = "获取用户动态话题列表", response = String.class)
    @PostMapping("/getUserMomentTopicList")
    public String getUserMomentTopicList(HttpServletRequest request, @Validated({GetList.class}) @RequestBody UserMomentTopicVO userMomentTopicVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        userMomentTopicVO.setIsPublish(EPublish.PUBLISH);
        return ResultUtil.successWithData(userMomentTopicService.getPageList(userMomentTopicVO));
    }

    @UserAuth(EAuthCode.ADD_MOMENT)
    @SubmitVerify
    @CheckRegexVerify(behavior = ERegexType.ADD_MOMENT)
    @PublishLimitVerify(behavior = EPublishLimitType.MOMENT_COUNT)
    @BussinessLog(value = "新增用户动态", behavior = EBehavior.ADD_MOMENT)
    @AvoidRepeatableCommit(timeout = 10000)
    @ApiOperation(value = "新增用户动态", notes = "新增用户动态", response = String.class)
    @PostMapping("/addUserMoment")
    public String addUserMoment(@Validated({Insert.class}) @RequestBody UserMomentVO userMomentVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return userMomentService.addUserMoment(userMomentVO);
    }

    @UserAuth(EAuthCode.ADD_MOMENT)
    @BussinessLog(value = "删除选中用户动态", behavior = EBehavior.DELETE_MOMENT)
    @ApiOperation(value = "删除选中用户动态", notes = "删除选中用户动态", response = String.class)
    @PostMapping("/deleteBatch")
    public String deleteBatch(@Validated({Delete.class}) @RequestBody List<UserMomentVO> userMomentVOList, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return userMomentService.deleteBatchUserMoment(userMomentVOList);
    }

    @BussinessLog(value = "解析URL信息", behavior = EBehavior.PARSE_URL)
    @ApiOperation(value = "解析URL信息", notes = "解析URL信息", response = String.class)
    @GetMapping("/parseUrl")
    public String parseUrl(@ApiParam(name = "url", value = "URL信息") @RequestParam(name = "url") String url) {
        if (StringUtils.isEmpty(url)) {
            return ResultUtil.errorWithMessage("请输入合法的url");
        }
        Boolean isHttpUrl = RegexUtils.isHttpUrl(url);
        if (!isHttpUrl) {
            return ResultUtil.errorWithMessage("请输入合法的url");
        }
        String result = HttpUtil.get(url);
        Pattern pattern = Pattern.compile("<title>[\\s\\S]*?</title>");
        Matcher matcher = pattern.matcher(result);
        String title = "推荐一个网站";
        // 获得结果集
        while (matcher.find()) {
            title = matcher.group(0);
            if (StringUtils.isNotEmpty(title)) {
                title = title.replace("<title>", "").replace("</title>", "");
                break;
            }
        }
        return ResultUtil.successWithData(title);
    }

    /**
     * 查询动态排行榜
     * @return
     */
    @ApiOperation(value = "查询动态排行榜", notes = "查询动态排行榜")
    @GetMapping(value = "/getLeaderMoment")
    public String getLeaderMoment(@ApiParam(name = "refresh", value = "是否刷新配置", required = false) @RequestParam(name = "refresh", required = false, defaultValue = "false") Boolean refresh) {
        return ResultUtil.result(SysConf.SUCCESS, userMomentService.getLeaderMoment(refresh));
    }
}

