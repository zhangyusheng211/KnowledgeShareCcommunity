package com.moxi.mogublog.xo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.vo.ReportVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.dto.ReportOption;
import com.moxi.mogublog.xo.manager.ReportManager;
import com.moxi.mogublog.xo.mapper.ReportMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.EAuditStatus;
import com.moxi.mougblog.base.enums.EPublish;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.holder.RequestHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 举报模块处理类
 *
 * @author 遇见
 */
@Service
public class ReportManagerImpl implements ReportManager {

    @Resource
    ReportMapper reportMapper;
    /**
     * 博客模块
     */
    @Resource
    BlogService blogService;

    @Resource
    ProblemService problemService;
    /**
     * 问题模块
     */
    @Resource
    QuestionService questionService;
    /**
     * 评论模块
     */
    @Resource
    CommentService commentService;

    /**
     * 评论模块
     */
    @Resource
    UserMomentService userMomentService;

    @Resource
    ReportService reportService;

    @Resource
    private DomainEventUtil domainEventUtil;

    @Override
    public Page<ReportVO> queryList(ReportOption reportOption) {
        Page page = reportOption.creatPage();

        return reportMapper.queryList(page, reportOption);
    }

    @Override
    public Page<ReportVO> queryListByContent(ReportOption reportOption) {
        Page page = reportOption.creatPage();
        return reportMapper.queryListByContent(page, reportOption);
    }

    @Override
    public ReportVO submitReport(ReportOption reportOption) {
        /**
         * 当前举报人
         */
        String userUid = RequestHolder.getUserUid();
        ReportType type = ReportType.getType(reportOption.getReportType());
        if (type == null) {
            throw new BusinessException("举报类型错误,请重新核对后举报");
        }
        String reportUserId = null;
        /**
         * 根据类型获取举报的内容
         */
        switch (type) {
            case PROBLEM:
                Problem problem = problemService.getOne(
                        new LambdaQueryWrapper<Problem>()
                                .eq(Problem::getUid, reportOption.getReportContentUid())
                                .eq(Problem::getStatus, EStatus.ENABLE)
                );
                if (problem == null) {
                    throw new BusinessException("该面经已被删除,感谢您为了社区健康环境做出贡献!");
                }
                checkReportStatus(userUid, problem.getUid(), type.getKey());
                reportUserId = problem.getUserUid();
                break;
            case BLOG:
                Blog blog = blogService.getOne(
                        new LambdaQueryWrapper<Blog>()
                                .eq(Blog::getUid, reportOption.getReportContentUid())
                                .eq(Blog::getStatus, EStatus.ENABLE)
                );
                if (blog == null) {
                    throw new BusinessException("该博客已被删除,感谢您为了社区健康环境做出贡献!");
                }
                // 检查该资源是否被举报过
                checkReportStatus(userUid, blog.getUid(), type.getKey());
                reportUserId = blog.getUserUid();
                break;
            case QUESTION:
                Question question = questionService.getOne(
                        new LambdaQueryWrapper<Question>()
                                .eq(Question::getUid, reportOption.getReportContentUid())
                                .eq(Question::getStatus, EStatus.ENABLE)
                );
                if (question == null) {
                    throw new BusinessException("该问答已被删除,感谢您为了社区健康环境做出贡献!");
                }
                // 检查该资源是否被举报过
                checkReportStatus(userUid, question.getUid(), type.getKey());
                reportUserId = question.getUserUid();
                break;
            case COMMENT:
                Comment comment = commentService.getOne(
                        new LambdaQueryWrapper<Comment>()
                                .eq(Comment::getUid, reportOption.getReportContentUid())
                                .eq(Comment::getStatus, EStatus.ENABLE)
                );
                if (comment == null) {
                    throw new BusinessException("该评论已被删除,感谢您为了社区健康环境做出贡献!");
                }
                // 检查该资源是否被举报过
                checkReportStatus(userUid, comment.getUid(), type.getKey());
                reportUserId = comment.getUserUid();
                break;
            case MOMENT:
                UserMoment userMoment = userMomentService.getOne(
                        new LambdaQueryWrapper<UserMoment>()
                                .eq(UserMoment::getUid, reportOption.getReportContentUid())
                                .eq(UserMoment::getStatus, EStatus.ENABLE)
                );
                if (userMoment == null) {
                    throw new BusinessException("该动态已被删除,感谢您为了社区健康环境做出贡献!");
                }
                // 检查该资源是否被举报过
                checkReportStatus(userUid, userMoment.getUid(), type.getKey());
                reportUserId = userMoment.getUserUid();
                break;
            default:
                throw new BusinessException("举报类型错误,请重新核对后举报");
        }
        Report report = reportOption.build(userUid, reportUserId);
        boolean isSave = reportService.save(report);
        // 发送举报领域事件
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.REPORT_ADD, report);
        }
        /**
         * 下架操作
         */
        if (beyondTime(reportOption.getReportContentUid(), type)) {
            this.revocationContent(reportOption.getReportContentUid(), type);
        }
        return ReportVO.create(report);
    }

    @Override
    public String handleReport(String reportContentUid, String reportType, String reportUid, Integer progress) {
        List<Report> reportList = reportService.list(
                new LambdaQueryWrapper<Report>()
                        .eq(Report::getProgress, 0)
                        .eq(reportContentUid != null, Report::getReportContentUid, reportContentUid)
                        .eq(Report::getReportType, reportType)
                        .eq(reportUid != null, Report::getUid, reportUid)
        );


        if (reportList == null || reportList.size() == 0) {
            throw new BusinessException("未查询到该内容的举报信息");
        }


        reportList.forEach(item -> {
            item.setProgress(progress);
            domainEventUtil.publishEvent(EventAction.REPORT_HANDLE, item);
        });
        boolean isSave = reportService.updateBatchById(reportList);

        for (int i = reportList.size() - 1; i >= 0; i--) {
            Report report = reportList.get(i);
            /**
             * progress 值
             * 1为已查看 不做处理
             * 2为已处理
             */
            switch (progress) {
                case 1:
                    /**
                     *
                     * 暂不做处理
                     */
                    break;
                case 2:
                    /**
                     * 下架处理
                     */
                    this.revocationContent(report.getReportContentUid(), ReportType.getType(report.getReportType()));
                    break;
                default:
                    break;
            }
        }

        return ResultUtil.successWithMessage("处理成功");
    }

    /**
     * 检测举报次数是否超出阈值
     *
     * @param reportUid
     * @param reportType
     * @return
     */
    public Boolean beyondTime(String reportUid, ReportType reportType) {
        Integer count = reportService.count(
                new LambdaQueryWrapper<Report>()
                        .eq(Report::getReportContentUid, reportUid)
                        .eq(Report::getReportType, reportType.getKey())
        );
        //todo 写死为10 后续加入举报限制配置  读取配置
        /**
         * count+1>=10
         * 新增前检测 所以是 默认count多+1
         */
        return count >= 10 ? true : false;
    }

    /**
     * 下架操作
     */
    public Boolean revocationContent(String reportUid, ReportType reportType) {
        Boolean update = true;
        switch (reportType) {
            case BLOG:
                Blog blog = blogService.getById(reportUid);
                if (blog == null) {
                    return false;
                }
                blog.setRejectReason("因举报过多暂时下架");
                blog.setAuditStatus(EAuditStatus.REJECT);
                blog.setIsPublish(EPublish.NO_PUBLISH);
                update = blog.updateById();

                break;
            case QUESTION:
                Question question = questionService.getById(reportUid);
                if (question == null) {
                    return false;
                }
                question.setRejectReason("因举报过多暂时下架");
                question.setAuditStatus(EAuditStatus.REJECT);
                question.setIsPublish(EPublish.NO_PUBLISH);
                update = question.updateById();

                break;
            case PROBLEM:
                Problem problem = problemService.getById(reportUid);
                if (problem == null) {
                    return false;
                }
                problem.setRejectReason("因举报过多暂时下架");
                problem.setAuditStatus(EAuditStatus.REJECT);
                update = problem.updateById();
                break;
            case COMMENT:
                Comment comment = commentService.getById(reportUid);
                if (comment == null) {
                    return false;
                }
                comment.setRejectReason("因举报过多暂时下架");
                comment.setAuditStatus(EAuditStatus.REJECT);
                update = comment.updateById();
                break;
            case MOMENT:
                UserMoment userMoment = userMomentService.getById(reportUid);
                if (userMoment == null) {
                    return false;
                }
                userMoment.setRejectReason("因举报过多暂时下架");
                userMoment.setAuditStatus(EAuditStatus.REJECT);
                update = userMoment.updateById();

                break;

        }
        return update;
    }

    /**
     * 检查该资源是否被举报过
     *
     * @param userUid
     * @param reportContentUid
     * @param reportType
     * @return
     */
    private void checkReportStatus(String userUid, String reportContentUid, String reportType) {
        Integer count = reportService.count(new LambdaQueryWrapper<Report>()
                .eq(Report::getUserUid, userUid)
                .eq(Report::getReportContentUid, reportContentUid)
                .eq(Report::getReportType, reportType)
                .eq(Report::getStatus, EStatus.ENABLE)
        );
        if (count > 0) {
            throw new QueryException("你已举报过，请勿重复操作");
        }
    }


    public enum ReportType {
        PROBLEM("problem"),
        BLOG("blog"),
        QUESTION("question"),
        COMMENT("comment"),
        MOMENT("moment");

        private String key;

        ReportType(String key) {
            this.key = key;
        }

        public static ReportType getType(String key) {
            for (ReportType info : ReportType.values()) {
                if (info.getKey().equals(key)) {
                    return info;
                }
            }
            return null;
        }

        public String getKey() {
            return this.key;
        }

    }
}
