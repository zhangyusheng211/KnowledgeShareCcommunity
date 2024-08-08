package com.moxi.mogublog.xo.service.impl;

import com.moxi.mogublog.commons.entity.UserMessage;
import com.moxi.mogublog.xo.mapper.UserMessageMapper;
import com.moxi.mogublog.xo.service.UserMessageService;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 遇见
 */
@Service
public class UserMessageServiceImpl extends SuperServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {
}
