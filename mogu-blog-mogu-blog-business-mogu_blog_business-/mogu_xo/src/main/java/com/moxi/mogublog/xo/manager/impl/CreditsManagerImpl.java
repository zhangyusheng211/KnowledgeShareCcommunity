package com.moxi.mogublog.xo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.schema.UpdateCreditsRequest;
import com.moxi.mogublog.commons.vo.CreditsLogVO;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.manager.CreditsManager;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mougblog.base.enums.ECreditType;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 积分管理
 *
 * @author 遇见
 */
@Service
@Slf4j
public class CreditsManagerImpl implements CreditsManager {
    /**
     * 积分模块底层
     */
    @Resource
    CreditsLogService creditsLogService;
    /**
     * 签到服务
     */
    @Resource
    SignInRecordService signInRecordService;
    /**
     * 博客服务
     */
    @Resource
    BlogService blogService;
    /**
     * 评论服务
     */
    @Resource
    CommentService commentService;
    /**
     * 问答服务
     */
    @Resource
    QuestionService questionService;
    /**
     * 动态服务
     */
    @Resource
    UserMomentService userMomentService;
    /**
     * 用户服务
     */
    @Resource
    UserService userService;

    @Resource
    private AsyncService asyncService;


    @Override
    public String queryCredits(UserVO userVO) {
        Page page = userVO.creatPage();
        return ResultUtil.successWithData(creditsLogService.queryCredits(page, userVO.getUid()));
    }

    @Override
    public String compensation(CreditsLogVO creditsLogVO) {
        ECreditType creditType = ECreditType.getByCode(creditsLogVO.getActionCode());
        if (creditType == null) {
            return ResultUtil.errorWithMessage("积分状态码不正确,无法补偿积分");
        }
        User user = userService.getById(creditsLogVO.getUserUid());
        if (user == null) {
            return ResultUtil.errorWithMessage("用户Uid不正确,无法补偿积分");
        }

        Integer limit = creditType.getLimit();
        Boolean allowAdd = false;
        /**
         * action文章当天时间
         */
        Date date = new Date();

        switch (creditType) {
            case Blog:
                Blog blog = blogService.getOne(
                        new LambdaQueryWrapper<Blog>()
                                .eq(Blog::getUserUid, creditsLogVO.getUserUid())
                                .eq(Blog::getUid, creditsLogVO.getResourceUid())
                );
                if (blog == null) {
                    throw new RuntimeException("该博客未查询到,无法补偿积分");
                }
                date = blog.getCreateTime();
                Integer blogCount = creditsLogService.queryCount(creditsLogVO.getUserUid(), date, creditType.getCode());
                allowAdd = limit.compareTo(blogCount == null ? 0 : blogCount) > 0 ? true : false;
                break;
            case Comment:
                Comment comment = commentService.getOne(
                        new LambdaQueryWrapper<Comment>()
                                .eq(Comment::getUserUid, creditsLogVO.getUserUid())
                                .eq(Comment::getUid, creditsLogVO.getResourceUid())
                );
                if (comment == null) {
                    throw new RuntimeException("该评论未查询到,无法补偿积分");
                }
                date = comment.getCreateTime();
                Integer commentCount = creditsLogService.queryCount(creditsLogVO.getUserUid(), date, creditType.getCode());
                allowAdd = limit.compareTo(commentCount == null ? 0 : commentCount) > 0 ? true : false;
                break;
            case Question:
                Question question = questionService
                        .getOne(
                                new LambdaQueryWrapper<Question>()
                                        .eq(Question::getUserUid, creditsLogVO.getUserUid())
                                        .eq(Question::getUid, creditsLogVO.getResourceUid())
                        );
                if (question == null) {
                    throw new RuntimeException("该问答未查询到,无法补偿积分");
                }
                date = question.getCreateTime();
                Integer questionCount = creditsLogService.queryCount(creditsLogVO.getUserUid(), date, creditType.getCode());
                allowAdd = limit.compareTo(questionCount == null ? 0 : questionCount) > 0 ? true : false;
                break;
            case Moment:
                UserMoment userMoment = userMomentService
                        .getOne(
                                new LambdaQueryWrapper<UserMoment>()
                                        .eq(UserMoment::getUserUid, creditsLogVO.getUserUid())
                                        .eq(UserMoment::getUid, creditsLogVO.getResourceUid())
                        );
                if (userMoment == null) {
                    throw new RuntimeException("该动态未查询到,无法补偿积分");
                }
                Integer userMomentCount = creditsLogService.queryCount(creditsLogVO.getUserUid(), date, creditType.getCode());
                allowAdd = limit.compareTo(userMomentCount == null ? 0 : userMomentCount) > 0 ? true : false;
                break;
            default:
                allowAdd = false;
        }

        if (allowAdd) {
            //使用用户应该得到的积分
            creditsLogVO.setChangeCredits(creditType.getCredit());
            creditsLogVO.setUserName(user.getUserName());
            //使用补偿
            creditsLogVO.setActionName(ECreditType.Compensation.getAction());
            creditsLogVO.setActionCode(ECreditType.Compensation.getCode());
            creditsLogVO.setCreateTime(new Date());
            creditsLogService.addCreditsLog(creditsLogVO);
            /**
             *  手动发放
             */
            userService.updateCreditsByUserUid(creditsLogVO.getUserUid(), creditType.getCredit());
            return ResultUtil.successWithMessage("积分补偿成功");
        } else {
            return ResultUtil.errorWithMessage("积分补偿失败,该动作已达到当天最大限制获取次数");
        }
    }


    @Override
    public String updateCredit(String userUid, ECreditType creditType, Integer credit, String resourceUid) {
        if (creditType == null) {
            return ResultUtil.errorWithMessage("积分状态码不正确,无法补偿积分");
        }
        User user = userService.getById(userUid);
        if (user == null) {
            return ResultUtil.errorWithMessage("用户Uid不正确,无法补偿积分");
        }
        Integer limit = creditType.getLimit();
        Boolean allowAdd = false;
        /**
         * action文章当天时间
         */
        Date date = new Date();
        switch (creditType) {
            case SignIn:
                SignInRecord signInRecord = signInRecordService.getOne(
                        new LambdaQueryWrapper<SignInRecord>()
                                .eq(SignInRecord::getUserUid, userUid)
                                .eq(SignInRecord::getUid, resourceUid)
                );
                if (signInRecord == null) {
                    throw new RuntimeException("该签到未查询到,无法发放积分");
                }
            case Blog:
                Blog blog = blogService.getOne(
                        new LambdaQueryWrapper<Blog>()
                                .eq(Blog::getUserUid, userUid)
                                .eq(Blog::getUid, resourceUid)
                );
                if (blog == null) {
                    throw new RuntimeException("该博客未查询到,无法发放积分");
                }
                date = blog.getCreateTime();
                Integer blogCount = creditsLogService.queryCount(userUid, date, creditType.getCode());
                allowAdd = limit.compareTo(blogCount == null ? 0 : blogCount) > 0 ? true : false;
                break;
            case Comment:
                Comment comment = commentService.getOne(
                        new LambdaQueryWrapper<Comment>()
                                .eq(Comment::getUserUid, userUid)
                                .eq(Comment::getUid, resourceUid)
                );
                if (comment == null) {
                    throw new RuntimeException("该评论未查询到,无法发放积分");
                }
                date = comment.getCreateTime();
                Integer commentCount = creditsLogService.queryCount(userUid, date, creditType.getCode());
                allowAdd = limit.compareTo(commentCount == null ? 0 : commentCount) > 0 ? true : false;
                break;
            case Question:
                Question question = questionService
                        .getOne(
                                new LambdaQueryWrapper<Question>()
                                        .eq(Question::getUserUid, userUid)
                                        .eq(Question::getUid, resourceUid)
                        );
                if (question == null) {
                    throw new RuntimeException("该问答未查询到,无法发放积分");
                }
                date = question.getCreateTime();
                Integer questionCount = creditsLogService.queryCount(userUid, date, creditType.getCode());
                allowAdd = limit.compareTo(questionCount == null ? 0 : questionCount) > 0 ? true : false;
                break;
            case Moment:
                UserMoment userMoment = userMomentService
                        .getOne(
                                new LambdaQueryWrapper<UserMoment>()
                                        .eq(UserMoment::getUserUid, userUid)
                                        .eq(UserMoment::getUid, resourceUid)
                        );
                if (userMoment == null) {
                    throw new RuntimeException("该动态未查询到,无法发放积分");
                }
                Integer userMomentCount = creditsLogService.queryCount(userUid, date, creditType.getCode());
                allowAdd = limit.compareTo(userMomentCount == null ? 0 : userMomentCount) > 0 ? true : false;
                break;
            default:
                allowAdd = false;
        }

        if (allowAdd) {
            CreditsLogVO creditsLogVO = new CreditsLogVO();
            //使用用户应该得到的积分
            creditsLogVO.setChangeCredits(creditType.getCredit());
            creditsLogVO.setUserName(user.getUserName());
            creditsLogVO.setActionName(creditType.getAction());
            creditsLogVO.setActionCode(creditType.getCode());
            creditsLogVO.setCreateTime(new Date());
            creditsLogService.addCreditsLog(creditsLogVO);
            // 更新用户积分
            userService.updateCreditsByUserUid(creditsLogVO.getUserUid(), creditType.getCredit());
            // 更新用户经验值
            userService.updateExpValueByUserUid(creditsLogVO.getUserUid(), creditType.getCredit());
            return ResultUtil.successWithMessage("积分发放成功");
        } else {
            return ResultUtil.errorWithMessage("积分发放失败,该动作已达到当天最大限制获取次数");
        }
    }

    @Override
    public String updateCredit(UpdateCreditsRequest updateCreditsRequest) {
        if (updateCreditsRequest.getUpdateCredits() == 0) {
            return ResultUtil.errorWithMessage("变更的积分不能为0");
        }
        String userUid = updateCreditsRequest.getUserUid();
        User user = userService.getById(userUid);
        if (user == null) {
            return ResultUtil.errorWithMessage("未查询到用户信息");
        }
        userService.addUserCredits(ECreditType.SYSTEM.getAction(), updateCreditsRequest.getUpdateCredits(),"", userUid);
        // 进行站内信触达
        if (Constants.STR_ONE.equals(updateCreditsRequest.getOpenMessagePush())) {
            asyncService.executeAsyncBusinessNotice(userUid, updateCreditsRequest.getRemark());
        }
        return ResultUtil.successWithMessage("更新成功");
    }
}
