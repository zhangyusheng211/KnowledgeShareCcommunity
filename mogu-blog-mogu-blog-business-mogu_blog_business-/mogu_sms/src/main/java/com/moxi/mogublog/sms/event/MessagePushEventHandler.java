package com.moxi.mogublog.sms.event;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.MessagePush;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.factory.EventFactory;
import com.moxi.mogublog.sms.event.factory.MessagePushFactory;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mougblog.base.enums.EPushArea;
import com.moxi.mougblog.base.enums.EPushMethod;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息触达事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.MESSAGE_PUSH})
public class MessagePushEventHandler extends AbstractEventHandler {
    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        MessagePush messagePush = objectMapper.convertValue(domainEvent.getEntity(), MessagePush.class);
        switch (domainEvent.getEventAction()) {
            case MESSAGE_PUSH_ADD: {
                this.add(messagePush);
            }
            break;
            case MESSAGE_PUSH: {
                this.push(messagePush);
            }
            break;
            case MESSAGE_PUSH_EDIT: {
                this.edit(messagePush);
            }
            break;
            case MESSAGE_PUSH_DELETE: {
                this.delete(messagePush);
            }
            break;
        }
    }

    /**
     * 添加关注
     *
     * @param messagePush
     */
    public void add(MessagePush messagePush) {
        log.info("[MessagePushEventHandler] 新增推送暂未实现");
    }

    /**
     * 推送消息
     *
     * @param messagePush
     */
    public void push(MessagePush messagePush) {
        try {
            // 可能配置多个推送渠道，需要进行转换
            String pushMethodJson = messagePush.getPushMethod();
            List<String> pushMethodList = JsonUtils.jsonToList(pushMethodJson, String.class);
            if (pushMethodList == null || pushMethodList.size() == 0) {
                log.error("转化推送方法失败： pushMethodJson: {}", pushMethodJson);
                return;
            }
            List<User> userList = getUserListByMessagePush(messagePush);
            for (String pushMethodStr: pushMethodList) {
                EPushMethod pushMethod = EPushMethod.getPushMethod(Integer.valueOf(pushMethodStr));
                MessagePushFactory.getEventService(pushMethod).pushHandler(messagePush, userList);
            }
            messagePush.setPushStatus(2);
            messagePush.updateById();
        } catch (Exception e) {
            messagePush.setPushStatus(3);
            messagePush.updateById();
            log.error("[EventListener] doEventHandler error：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void edit(MessagePush messagePush) {
        log.info("[MessagePushEventHandler] 编辑触达事件暂未实现");
    }


    /**
     * 删除
     *
     * @param messagePush
     */
    public void delete(MessagePush messagePush) {
        log.info("[MessagePushEventHandler] 删除触达事件暂未实现");
    }


    /**
     * 通过消息推送，获取用户列表
     * @param messagePush
     * @return
     */
    public List<User> getUserListByMessagePush(MessagePush messagePush) {
        // 获取要推送的内容，以及用户的邮箱
        EPushArea pushArea = EPushArea.getPushArea(messagePush.getPushArea());
        // 根据推送范围查询指定的用户信息
        UserVO userVO = new UserVO();
        userVO.setNeedAvatar(false);
        userVO.setCurrentPage(1L);
        switch (pushArea) {
            case ALL: {
                userVO.setPageSize(Long.MAX_VALUE);
            } break;
            case USER_TAG: {
                userVO.setUserTag(messagePush.getUserTag());
                userVO.setPageSize(Long.MAX_VALUE);
            } break;
            case USER_LIST: {
                // 获取到用户列表
                String userListJson = messagePush.getPushUserUidList();
                List<String> userUidList = JsonUtils.jsonToList(userListJson, String.class);
                if (userUidList == null || userUidList.size() == 0) {
                    throw new InsertException("推送消息失败，用户列表为空");
                }
                userVO.setUserUidList(userUidList);
                userVO.setPageSize(Long.valueOf(userUidList.size()));
            }
        }

        IPage<User> userIPage = userService.getPageList(userVO);
        return userIPage.getRecords();
    }
}
