package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.CollectService;
import com.moxi.mogublog.commons.vo.CollectVO;
import com.moxi.mougblog.base.enums.EBehavior;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 博客收藏 RestApi
 *
 * @author 蓝毛怪
 * @date 2021年12月05日09:31:54
 */
@RestController
@RequestMapping("/collect")
@Api(value = "收藏相关接口", tags = {"博客收藏相关接口"})
@Slf4j
public class CollectRestApi {
    @Resource
    private CollectService collectService;

    @ApiOperation(value = "通过业务id获取收藏", notes = "通过业务id获取收藏")
    @PostMapping("/getCollectCount")
    public String getCollectCount(@Validated({Insert.class}) @RequestBody CollectVO collectVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("通过业务id获取收藏");
        return ResultUtil.result(SysConf.SUCCESS, collectService.getCollectCount(collectVO));
    }

    @AvoidRepeatableCommit(timeout = 2000)
    @BussinessLog(value = "增加收藏", behavior = EBehavior.COLLECT)
    @ApiOperation(value = "增加收藏", notes = "增加收藏")
    @PostMapping("/addCollect")
    public String addCollect(@Validated({Insert.class}) @RequestBody CollectVO collectVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("增加收藏");
        return collectService.addCollect(collectVO);
    }

    @AvoidRepeatableCommit(timeout = 2000)
    @BussinessLog(value = "取消收藏", behavior = EBehavior.CANCEL_COLLECT)
    @ApiOperation(value = "取消收藏", notes = "取消收藏")
    @PostMapping("/deleteCollect")
    public String deleteCollect(@Validated({Insert.class}) @RequestBody CollectVO collectVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("取消收藏");
        return collectService.deleteCollect(collectVO);
    }

    @ApiOperation(value = "获取我的收藏列表", notes = "获取我的收藏列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody CollectVO collectVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取我的收藏列表");
        String userUid = RequestHolder.getUserUid();
        collectVO.setUserUid(userUid);
        return ResultUtil.result(SysConf.SUCCESS, collectService.getPageList(collectVO));
    }
}

