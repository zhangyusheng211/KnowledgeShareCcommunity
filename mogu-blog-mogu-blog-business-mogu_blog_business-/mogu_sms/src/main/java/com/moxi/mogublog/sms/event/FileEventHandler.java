package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.File;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 文件事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.FILE})
public class FileEventHandler extends AbstractEventHandler {

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = objectMapper.convertValue(domainEvent.getEntity(), File.class);
        switch (domainEvent.getEventAction()) {
            case FILE_ADD: {
                this.add(file, domainEvent.isAdminOperate());
            }
            break;
            case FILE_EDIT: {
                this.edit(file, domainEvent.isAdminOperate());
            }
            break;
            case FILE_DELETE: {
                this.delete(file, domainEvent.isAdminOperate());
            }
            break;
        }
    }

    /**
     * 添加文件
     *
     * @param file
     * @param isAdminOperate
     */
    public void add(File file, boolean isAdminOperate) {
        log.info("[FileEvent] 添加文件事件触发");

        // 新增文件，判断任务进行情况
        userTaskService.action(Action.FILE, null, null);
    }


    /**
     * 编辑文件
     *
     * @param file
     * @param isAdminOperate
     */
    public void edit(File file, boolean isAdminOperate) {
        log.info("[FileEvent] 编辑文件事件触发");
    }

    /**
     * 删除文件
     *
     * @param file
     * @param isAdminOperate
     */
    public void delete(File file, boolean isAdminOperate) {
        log.info("[FileEvent] 删除文件事件触发");
    }
}
