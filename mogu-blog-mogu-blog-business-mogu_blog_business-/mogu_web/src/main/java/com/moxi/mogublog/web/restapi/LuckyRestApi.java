package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.annotion.FeignSecurity.FeignSecurity;
import com.moxi.mogublog.commons.annotion.UserAuth.UserAuth;
import com.moxi.mogublog.commons.entity.Blog;
import com.moxi.mogublog.commons.entity.LuckyActivity;
import com.moxi.mogublog.commons.entity.MediaInfo;
import com.moxi.mogublog.commons.schema.ProductListRequest;
import com.moxi.mogublog.commons.schema.ProductRequest;
import com.moxi.mogublog.commons.schema.ProductVO;
import com.moxi.mogublog.commons.vo.LuckyActivityVO;
import com.moxi.mogublog.commons.vo.LuckyRecordVO;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mougblog.base.enums.EAuthCode;
import com.moxi.mougblog.base.enums.EChargeType;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 抽奖的接口
 *
 * @author 陌溪
 * @date 2023年7月23日11:14:19
 */
@RestController
@RefreshScope
@RequestMapping("/lucky")
@Api(value = "抽奖服务请求的接口", tags = {"抽奖服务请求的接口"})
@Slf4j
public class LuckyRestApi {
    @Resource
    private LuckyActivityService luckyActivityService;
    @Resource
    private LuckyRecordService luckyRecordService;

    @ApiOperation(value = "获取活动列表", notes = "获取活动列表")
    @RequestMapping(value = "/getActivityList", method = RequestMethod.POST)
    public String getList(@RequestBody LuckyActivityVO luckyActivityVO) {
        log.info("获取活动列表");
        return ResultUtil.successWithData(luckyActivityService.getPageList(luckyActivityVO));
    }

    @UserAuth(EAuthCode.CREDIT_LUCKY)
    @ApiOperation(value = "开始抽奖", notes = "开始抽奖")
    @RequestMapping(value = "/lucky", method = RequestMethod.POST)
    public String lucky(@RequestBody LuckyActivityVO luckyActivityVO) {
        log.info("开始抽奖");
        return ResultUtil.successWithData(luckyActivityService.lucky(luckyActivityVO));
    }

    @ApiOperation(value = "获取抽奖记录", notes = "获取抽奖记录")
    @RequestMapping(value = "/getLuckyRecordList", method = RequestMethod.POST)
    public String lucky(@RequestBody LuckyRecordVO luckyRecordVO) {
        log.info("获取抽奖记录");
        return ResultUtil.successWithData(luckyRecordService.getPageList(luckyRecordVO));
    }
}
