package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.Report;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 举报事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.REPORT})
public class ReportEventHandler extends AbstractEventHandler {

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        Report report = objectMapper.convertValue(domainEvent.getEntity(), Report.class);
        switch (domainEvent.getEventAction()) {
            case REPORT_ADD: {
                this.add(report);
            }
            break;
            case REPORT_DELETE: {
                this.delete(report);
            }
            break;
            case REPORT_HANDLE: {
                this.handle(report);
            }
            break;
        }
    }

    /**
     * 新增举报
     *
     * @param report
     */
    public void add(Report report) {
        log.info("[NoticeEventHandler] 新增举报");

        // 新增举报，判断任务进行情况
        userTaskService.action(Action.REPORT, null, null);
    }


    /**
     * 删除举报
     *
     * @param report
     */
    public void delete(Report report) {
        log.info("[NoticeEventHandler] 删除举报");
    }

    /**
     * 删除举报
     *
     * @param report
     */
    public void handle(Report report) {
        log.info("[NoticeEventHandler] 处理举报");
        if (report.getProgress() == 1) {
            // 站内信通知举报人
            asyncService.executeAsyncBusinessNotice(report.getUserUid(), "博主，您举报的 " + StringUtils.dealContent(report.getReportContent()) + " 经过核实暂时不做处理，感谢您为净化社区环境做的努力。");
        } else if (report.getProgress() == 2) {
            // 站内信通知被举报人
            asyncService.executeAsyncBusinessNotice(report.getReportUserUid(), "博主，为了规范社区内容，您发表的 " + StringUtils.dealContent(report.getReportContent()) + " 已被下架处理");
            asyncService.executeAsyncBusinessNotice(report.getUserUid(), "博主，您举报的 " + StringUtils.dealContent(report.getReportContent()) + " 已被下架处理，感谢您净化社区环境。");
        }
    }
}
