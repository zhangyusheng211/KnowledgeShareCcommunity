package com.moxi.mogublog.xo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.UserPraiseRecord;
import com.moxi.mogublog.commons.vo.UserPraiseRecordVO;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.manager.CommonManager;
import com.moxi.mogublog.xo.manager.UserPraiseRecordManager;
import com.moxi.mogublog.xo.mapper.UserPraiseRecordMapper;
import com.moxi.mogublog.xo.service.NoticeService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.EPraiseType;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.holder.RequestHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户点赞模块
 *
 * @author 遇见
 */
@Service
public class UserPraiseRecordManagerImpl implements UserPraiseRecordManager {

    @Resource
    UserPraiseRecordMapper userPraiseRecordMapper;
    @Autowired
    NoticeService noticeService;
    @Resource
    private DomainEventUtil domainEventUtil;
    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String praise(UserPraiseRecordVO userPraiseRecordVO) {
        String userUid = RequestHolder.getUserUid();
        // 类型校验
        EResourceType resourceType = EResourceType.getType(userPraiseRecordVO.getResourceType());
        /**
         * 检索
         * 指定uid 指定资源id 指定资源类型  且为点赞的数据
         * 判断是否已点赞过
         *
         */
        UserPraiseRecord userPraiseRecord = userPraiseRecordMapper.selectOne(
                new LambdaQueryWrapper<UserPraiseRecord>()
                        .eq(UserPraiseRecord::getUserUid, userUid)
                        .eq(UserPraiseRecord::getResourceUid, userPraiseRecordVO.getResourceUid())
                        .eq(UserPraiseRecord::getResourceType, resourceType.getType())
                        .eq(UserPraiseRecord::getStatus, EStatus.ENABLE)
                        .last(SysConf.LIMIT_ONE)
        );

        if (userPraiseRecord != null) {
            return ResultUtil.errorWithMessage(MessageConf.YOU_HAVE_BEEN_PRISE);
        }
        UserPraiseRecord newRecord = new UserPraiseRecord();
        newRecord.setUserUid(userUid);
        newRecord.setPraiseType(EPraiseType.PRAISE.getType());
        newRecord.setResourceUid(userPraiseRecordVO.getResourceUid());
        newRecord.setResourceType(resourceType.getType());
        newRecord.setStatus(EStatus.ENABLE);
        userPraiseRecordMapper.insert(newRecord);

        // 发送点赞成功通知
        domainEventUtil.publishEvent(EventAction.PRAISE_ADD, newRecord);

        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public String tread(UserPraiseRecordVO userPraiseRecordVO) {
        String userUid = RequestHolder.getUserUid();
        // 类型校验
        EResourceType resourceType = EResourceType.getType(userPraiseRecordVO.getResourceType());
        /**
         * 检索
         * 指定uid 指定资源id 指定资源类型  且为点踩的数据
         * 判断是否已点踩过
         *
         */
        UserPraiseRecord userPraiseRecord = userPraiseRecordMapper.selectOne(
                new LambdaQueryWrapper<UserPraiseRecord>()
                        .eq(UserPraiseRecord::getUserUid, userUid)
                        .eq(UserPraiseRecord::getResourceUid, userPraiseRecordVO.getResourceUid())
                        .eq(UserPraiseRecord::getResourceType, resourceType.getType())
                        .eq(UserPraiseRecord::getStatus, EStatus.ENABLE)
                        .last(SysConf.LIMIT_ONE)
        );
        if (userPraiseRecord != null) {
            return ResultUtil.errorWithMessage(MessageConf.YOU_HAVE_BEEN_TREADE);
        }
        UserPraiseRecord newRecord = new UserPraiseRecord();
        newRecord.setUserUid(userUid);
        newRecord.setPraiseType(EPraiseType.TREAD.getType());
        newRecord.setResourceUid(userPraiseRecordVO.getResourceUid());
        newRecord.setResourceType(resourceType.getType());
        newRecord.setStatus(EStatus.ENABLE);
        userPraiseRecordMapper.insert(newRecord);
        // 发送点踩成功通知
        domainEventUtil.publishEvent(EventAction.TREAD_ADD, newRecord);
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public String cancelPraise(UserPraiseRecordVO userPraiseRecordVO) {
        String userUid = RequestHolder.getUserUid();
        // 类型校验
        EResourceType resourceType = EResourceType.getType(userPraiseRecordVO.getResourceType());
        /**
         * 获取点赞列表
         */
        UserPraiseRecord userPraiseRecord = userPraiseRecordMapper.selectOne(
                new LambdaQueryWrapper<UserPraiseRecord>()
                        .eq(UserPraiseRecord::getPraiseType, EPraiseType.PRAISE.getType())
                        .eq(UserPraiseRecord::getResourceUid, userPraiseRecordVO.getResourceUid())
                        .eq(UserPraiseRecord::getResourceType, resourceType.getType())
                        .eq(UserPraiseRecord::getUserUid, userUid)
                        .eq(UserPraiseRecord::getStatus, EStatus.ENABLE)
                        .last(SysConf.LIMIT_ONE)
        );
        if (userPraiseRecord != null) {
            userPraiseRecord.setStatus(EStatus.DISABLED);
            boolean isSave = userPraiseRecord.updateById();
            // 发送取消点赞成功通知
            if (isSave) {
                domainEventUtil.publishEvent(EventAction.PRAISE_CANCEL, userPraiseRecord);
            }
        }
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }


    @Override
    public String cancelTread(UserPraiseRecordVO userPraiseRecordVO) {
        String userUid = RequestHolder.getUserUid();
        // 类型校验
        EResourceType resourceType = EResourceType.getType(userPraiseRecordVO.getResourceType());
        /**
         * 取消点踩
         */
        UserPraiseRecord userPraiseRecord = userPraiseRecordMapper.selectOne(
                new LambdaQueryWrapper<UserPraiseRecord>()
                        .eq(UserPraiseRecord::getPraiseType, EPraiseType.TREAD.getType())
                        .eq(UserPraiseRecord::getResourceUid, userPraiseRecordVO.getResourceUid())
                        .eq(UserPraiseRecord::getResourceType, resourceType.getType())
                        .eq(UserPraiseRecord::getUserUid, userUid)
                        .eq(UserPraiseRecord::getStatus, EStatus.ENABLE)
                        .last(SysConf.LIMIT_ONE)
        );
        if (userPraiseRecord != null) {
            userPraiseRecord.setStatus(EStatus.DISABLED);
            boolean isSave = userPraiseRecord.updateById();
            // 发送取消点赞成功通知
            if (isSave) {
                domainEventUtil.publishEvent(EventAction.TREAD_CANCEL, userPraiseRecord);
            }
        }
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public Boolean hasPraised(UserPraiseRecordVO userPraiseRecordVO) {
        String userUid = RequestHolder.getUserUid();
        // 类型校验
        EPraiseType praiseType = EPraiseType.getType(userPraiseRecordVO.getPraiseType());
        EResourceType resourceType = EResourceType.getType(userPraiseRecordVO.getResourceType());
        return userPraiseRecordMapper.selectOne(
                new LambdaQueryWrapper<UserPraiseRecord>()
                        .eq(UserPraiseRecord::getUserUid, userUid)
                        .eq(UserPraiseRecord::getResourceUid, userPraiseRecordVO.getResourceUid())
                        .eq(UserPraiseRecord::getResourceType, resourceType.getType())
                        .eq(UserPraiseRecord::getPraiseType, praiseType.getType())
                        .eq(UserPraiseRecord::getStatus, EStatus.ENABLE)
        ) == null ? false : true;
    }

    @Override
    public List<User> getPraiseList(UserPraiseRecordVO userPraiseRecordVO) {

        // 查找出所有点赞的用户
        List<UserPraiseRecord> userPraiseRecordList = userPraiseRecordMapper.selectList(new QueryWrapper<UserPraiseRecord>().select("DISTINCT `user_uid`").lambda()
                .eq(UserPraiseRecord::getResourceUid, userPraiseRecordVO.getResourceUid())
                .eq(UserPraiseRecord::getResourceType, userPraiseRecordVO.getResourceType())
                .eq(UserPraiseRecord::getPraiseType, EPraiseType.PRAISE.getType())
                .eq(UserPraiseRecord::getStatus, EStatus.ENABLE)
        );

        // 找到用户列表
        List<String> userUidList = userPraiseRecordList.stream().map(UserPraiseRecord::getUserUid).collect(Collectors.toList());

        // 判断一下当前登录的用户，是否在点赞列表，如果在则置顶
        String userUid = RequestHolder.getUserUid();
        List<User> userList = new ArrayList<>();
        if (userUidList.size() > 0) {
            List<User> tempUserList = userService.getUserListAndAvatarByIds(userUidList);
            for (User user : tempUserList) {
                if (user.getUid().equals(userUid)) {
                    userList.add(0, user);
                } else {
                    userList.add(user);
                }
            }
        }
        return userList;
    }
}
