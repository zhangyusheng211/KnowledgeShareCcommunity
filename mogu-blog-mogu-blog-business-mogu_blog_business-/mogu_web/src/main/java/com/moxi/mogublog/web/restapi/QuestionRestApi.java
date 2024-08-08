package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.annotion.UserAuth.UserAuth;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.annotion.PublishLimitVerify.PublishLimitVerify;
import com.moxi.mogublog.web.annotion.SubmitVerify.SubmitVerify;
import com.moxi.mogublog.web.annotion.checkRegexVerify.CheckRegexVerify;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.QuestionService;
import com.moxi.mogublog.xo.service.QuestionTagService;
import com.moxi.mogublog.xo.service.QuestionTemplateService;
import com.moxi.mogublog.commons.vo.QuestionTagVO;
import com.moxi.mogublog.commons.vo.QuestionTemplateVO;
import com.moxi.mogublog.commons.vo.QuestionVO;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
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
 * 问答相关 RestApi
 *
 * @author 陌溪
 * @date 2021年5月5日18:13:52
 */
@Api(value = "问答相关接口", tags = {"问答相关接口"})
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionRestApi {

    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionTagService questionTagService;
    @Resource
    private QuestionTemplateService questionTemplateService;

    @ApiOperation(value = "获取问答列表", notes = "获取问答列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody QuestionVO questionVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取问答列表");
        // 前端获取的必须是已发布的
        questionVO.setIsPublish(EPublish.PUBLISH);
        questionVO.setIsBlack(0);
        return ResultUtil.result(SysConf.SUCCESS, questionService.getPageList(questionVO));
    }


    @ApiOperation(value = "获取当前用户的问答列表", notes = "获取当前用户的问答列表", response = String.class)
    @PostMapping("/getMeQuestionList")
    public String getMeQuestionList(@Validated({GetList.class}) @RequestBody QuestionVO questionVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取问答列表");
        // 获取自己的问答
        questionVO.setUserUid(RequestHolder.getUserUid());
        return ResultUtil.result(SysConf.SUCCESS, questionService.getPageList(questionVO));
    }


    @BussinessLog(value = "获取问答详情", behavior = EBehavior.QUESTION)
    @ApiOperation(value = "获取问答详情", notes = "获取问答详情")
    @PostMapping("/getQuestion")
    public String getQuestion(@RequestBody QuestionVO questionVO) {
        return questionService.getQuestion(questionVO);
    }

    @UserAuth(EAuthCode.ADD_QUESTION)
    @CheckRegexVerify(behavior = ERegexType.ADD_QUESTION)
    @PublishLimitVerify(behavior = EPublishLimitType.QUESTION_COUNT)
    @SubmitVerify
    @AvoidRepeatableCommit
    @BussinessLog(value = "增加问答", behavior = EBehavior.ADD_QUESTION)
    @ApiOperation(value = "增加问答", notes = "增加问答", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody QuestionVO questionVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        // 添加问答来源【用户投稿】
        questionVO.setQuestionSource(EContributeSource.USER_PUBLISH);
        log.info("增加问答");
        return questionService.addQuestion(questionVO);
    }

    @UserAuth(EAuthCode.ADD_QUESTION)
    @AvoidRepeatableCommit
    @BussinessLog(value = "编辑问答", behavior = EBehavior.EDIT_QUESTION)
    @ApiOperation(value = "编辑问答", notes = "编辑问答", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody QuestionVO questionVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        questionVO.setQuestionSource(EContributeSource.USER_PUBLISH);
        log.info("编辑问答");
        return questionService.editQuestion(questionVO);
    }

    @UserAuth(EAuthCode.ADD_QUESTION)
    @AvoidRepeatableCommit
    @BussinessLog(value = "删除问答", behavior = EBehavior.DELETE_QUESTION)
    @ApiOperation(value = "删除问答", notes = "删除博客", response = String.class)
    @PostMapping("/delete")
    public String delete(@Validated({Delete.class}) @RequestBody QuestionVO questionVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        // 文章类型只能是博客类型
        questionVO.setQuestionSource(EContributeSource.USER_PUBLISH);
        return questionService.deleteQuestion(questionVO);
    }

    @ApiOperation(value = "获取标签列表", notes = "获取标签列表", response = String.class)
    @PostMapping("/getTagList")
    public String getTagList(@Validated({GetList.class}) @RequestBody QuestionTagVO questionTagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取标签列表");
        return ResultUtil.result(SysConf.SUCCESS, questionTagService.getPageList(questionTagVO));
    }

    @ApiOperation(value = "获取问答模板列表", notes = "获取问答模板列表", response = String.class)
    @PostMapping("/getTemplateList")
    public String getTemplateList(@Validated({GetList.class}) @RequestBody QuestionTemplateVO questionTemplateVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取问答模板列表");
        return ResultUtil.result(SysConf.SUCCESS, questionTemplateService.getPageList(questionTemplateVO));
    }
}
