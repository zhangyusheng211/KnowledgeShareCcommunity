package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.convert.AggEsDocConvert;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.Resource;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mougblog.base.enums.EOperate;
import com.moxi.mougblog.base.holder.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 举报事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.RESOURCE})
public class ResourceEventHandler extends AbstractEventHandler {

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        Resource resource = objectMapper.convertValue(domainEvent.getEntity(), Resource.class);
        switch (domainEvent.getEventAction()) {
            case RESOURCE_ADD: {
                this.add(resource);
            }
            break;
            case RESOURCE_EDIT: {
                this.edit(resource);
            }
            break;
            case RESOURCE_DELETE: {
                this.delete(resource);
            }
            break;
            case RESOURCE_VISIT: {
                this.visit(resource);
            }
            break;
            case RESOURCE_DOWNLOAD: {
                this.download(resource);
            }
            break;
        }
    }

    /**
     * 新增资源
     *
     * @param resource
     */
    public void add(Resource resource) {
        this.syncAggEsDoc(resource, EOperate.ADD);
    }

    /**
     * 删除资源
     *
     * @param resource
     */
    public void edit(Resource resource) {
        this.syncAggEsDoc(resource, EOperate.EDIT);
    }


    /**
     * 删除资源
     *
     * @param resource
     */
    public void delete(Resource resource) {
        this.syncAggEsDoc(resource, EOperate.DELETE);
    }

    /**
     * 资源访问
     *
     * @param resource
     */
    public void visit(Resource resource) {
        // 执行资源访问任务
        if (StringUtils.isNotEmpty(ThreadLocalUtil.getUserUid())) {
            Map<String, Object> map = new HashMap<>();
            map.put(SysConf.TO_USER_UID, resource.getUserUid());
            map.put(SysConf.UID, resource.getUid());
            userTaskService.action(Action.RESOURCE_VISIT, null, map);
        }
    }


    /**
     * 资源下载
     *
     * @param resource
     */
    public void download(Resource resource) {
        // 执行资源下载任务
        Map<String, Object> map = new HashMap<>();
        map.put(SysConf.TO_USER_UID, resource.getUserUid());
        map.put(SysConf.UID, resource.getUid());
        userTaskService.action(Action.RESOURCE_DOWNLOAD, null, map);
    }


    /**
     * syncAggEsDoc 同步到ES
     *
     * @param resource
     * @param operate
     */
    private void syncAggEsDoc(Resource resource, EOperate operate) {
        AggEsDoc aggEsDoc = AggEsDocConvert.convertResource(resource);
        asyncService.executeAsyncUpdateEsAndRedis(aggEsDoc, operate);
    }

}
