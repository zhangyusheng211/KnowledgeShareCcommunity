package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.Blog;
import com.moxi.mogublog.commons.entity.Subject;
import com.moxi.mogublog.utils.DateUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.service.SubjectItemService;
import com.moxi.mogublog.xo.service.SubjectService;
import com.moxi.mogublog.xo.service.SubjectSortService;
import com.moxi.mogublog.commons.vo.SubjectItemVO;
import com.moxi.mogublog.commons.vo.SubjectSortVO;
import com.moxi.mogublog.commons.vo.SubjectVO;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.EPublish;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.validator.group.GetList;
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
import java.util.concurrent.TimeUnit;

/**
 * 专题表 RestApi
 *
 * @author 陌溪
 * @since 2020年8月24日10:37:39
 */
@Api(value = "专题相关接口", tags = {"专题相关接口"})
@RestController
@RequestMapping("/subject")
@Slf4j
public class SubjectRestApi {

    @Resource
    SubjectService subjectService;

    @Resource
    SubjectSortService subjectSortService;

    @Resource
    SubjectItemService subjectItemService;
    @Resource
    DomainEventUtil domainEventUtil;
    @Resource
    RedisUtil redisUtil;

    @ApiOperation(value = "获取专题列表", notes = "获取专题列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody SubjectVO subjectVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        subjectVO.setIsPublish(Integer.valueOf(EPublish.PUBLISH));
        return ResultUtil.result(SysConf.SUCCESS, subjectService.getPageList(subjectVO));
    }

    @ApiOperation(value = "获取专题分类列表", notes = "获取专题分类列表", response = String.class)
    @PostMapping("/getSortList")
    public String getSortList(@Validated({GetList.class}) @RequestBody SubjectSortVO subjectSortVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        subjectSortVO.setIsPublish(EPublish.PUBLISH);
        return ResultUtil.result(SysConf.SUCCESS, subjectSortService.getPageList(subjectSortVO));
    }

    @ApiOperation(value = "获取专题Item列表", notes = "获取专题Item列表", response = String.class)
    @PostMapping("/getItemList")
    public String getItemList(@Validated({GetList.class}) @RequestBody SubjectItemVO subjectItemVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        subjectItemVO.setIsPublish(EPublish.PUBLISH);

        // 发送专栏访问领域事件
        if (StringUtils.isNotEmpty(subjectItemVO.getSubjectUid())) {
            Subject subject = new Subject();
            subject.setUid(subjectItemVO.getSubjectUid());
            domainEventUtil.publishEvent(EventAction.SUBJECT_VISIT, subject);
        }
        return ResultUtil.result(SysConf.SUCCESS, subjectItemService.getPageList(subjectItemVO));
    }
}

