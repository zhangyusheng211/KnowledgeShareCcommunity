package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.Subject;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.Withdraw;
import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.commons.vo.NoticeVO;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.global.RedisConf;
import com.moxi.mogublog.utils.DateUtils;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.EAuditStatus;
import com.moxi.mougblog.base.enums.EBusinessType;
import com.moxi.mougblog.base.enums.ENoticeType;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * 关注事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.SUBJECT})
public class SubjectEventHandler extends AbstractEventHandler {

    @Resource
    private WebFeignClient webFeignClient;

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        Subject subject = objectMapper.convertValue(domainEvent.getEntity(), Subject.class);
        switch (domainEvent.getEventAction()) {
            case SUBJECT_ADD: {
                this.addSubject(subject);
            }
            break;
            case SUBJECT_EDIT: {
                this.editSubject(subject);
            }
            break;
            case SUBJECT_DELETE: {
                this.deleteSubject(subject);
            }
            break;
            case SUBJECT_VISIT: {
                this.visitSubject(subject);
            }
            break;
        }
    }

    /**
     * 新增专栏
     * @param subject
     */
    public void addSubject(Subject subject) {
        log.info("[addSubject] 新增专栏");
    }

    /**
     * 新增专栏
     * @param subject
     */
    public void editSubject(Subject subject) {
        log.info("[editSubject] 编辑专栏");
    }

    /**
     * 删除专栏
     * @param subject
     */
    public void deleteSubject(Subject subject) {
        log.info("[deleteSubject] 删除专栏");
    }

    /**
     * 新增专栏
     * @param subject
     */
    public void visitSubject(Subject subject) {
        log.info("[visitSubject] 访问专栏");
        if (subject == null || StringUtils.isEmpty(subject.getUid())) {
            log.info("[visitSubject] 专栏为空，跳过处理");
            return;
        }
        String ip = ThreadLocalUtil.getIp();
        String key = RedisConf.SUBJECT_CLICK + Constants.SYMBOL_COLON + ip + Constants.SYMBOL_COLON + subject.getUid();
        String visitExtra = redisUtil.get(key);
        if (StringUtils.isNotEmpty(visitExtra)) {
            log.info("[visitSubject] 用户近一天内访问过，跳过处理");
            return;
        }
        Subject subjectPo = subjectService.getById(subject.getUid());
        if (subjectPo == null) {
            return;
        }
        // 更新专栏点击数
        subjectPo.setClickCount(subjectPo.getClickCount() + 1);
        subjectPo.updateById();
        // 记录缓存
        redisUtil.setEx(key, "1", DateUtils.getNowToNextDaySeconds(), TimeUnit.SECONDS);
    }
}
