package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.Report;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.vo.ReportVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.mapper.ReportMapper;
import com.moxi.mogublog.xo.service.ReportService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 举报服务 实体类服务实现类
 *
 * @author 遇见
 */
@Service
public class ReportServiceImpl extends SuperServiceImpl<ReportMapper, Report> implements ReportService {

    @Resource
    ReportMapper reportMapper;
    @Resource
    UserService userService;
    @Resource
    private DomainEventUtil domainEventUtil;

    @Override
    public IPage<Report> getPageList(ReportVO reportVO) {

        QueryWrapper<Report> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(reportVO.getContent())) {
            queryWrapper.eq(SQLConf.CONTENT, reportVO.getContent());
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Page<Report> page = new Page<>();
        page.setCurrent(reportVO.getCurrentPage());
        page.setSize(reportVO.getPageSize());
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<Report> iPage = reportMapper.selectPage(page, queryWrapper);
        List<String> userUidList = new ArrayList<>();
        iPage.getRecords().forEach(item -> {
            userUidList.add(item.getUserUid());
            userUidList.add(item.getReportUserUid());
        });
        Map<String, User> userMap = userService.getUserAvatarMapByIds(userUidList);
        iPage.getRecords().forEach(item -> {
            item.setUser(userMap.get(item.getUserUid()));
            item.setReportUser(userMap.get(item.getReportUserUid()));
        });

        return iPage;
    }

    @Override
    public String batchDeleteReport(List<String> reportUidList) {
        if (reportUidList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<Report> reportCollect = reportMapper.selectBatchIds(reportUidList);
        reportCollect.forEach(item -> {
            item.setStatus(EStatus.DISABLED);
            domainEventUtil.publishEvent(EventAction.REPORT_DELETE, item);
        });

        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }
}
