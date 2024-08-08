package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.UserMomentTopic;
import com.moxi.mogublog.commons.vo.UserMomentTopicVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.UserMomentTopicMapper;
import com.moxi.mogublog.xo.service.UserMomentTopicService;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


/**
 * 用户动态日志 服务实现类
 *
 * @author 陌溪
 * @since 2021年12月25日23:57:11
 */
@Service
public class UserMomentTopicServiceImpl extends SuperServiceImpl<UserMomentTopicMapper, UserMomentTopic> implements UserMomentTopicService {

    @Resource
    UserMomentTopicService userMomentTopicService;
    @Resource
    FileFeignUtil fileFeignUtil;

    @Override
    public IPage<UserMomentTopic> getPageList(UserMomentTopicVO userMomentTopicVO) {
        QueryWrapper<UserMomentTopic> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(userMomentTopicVO.getKeyword()) && !StringUtils.isEmpty(userMomentTopicVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.CONTENT, userMomentTopicVO.getKeyword().trim());
        }

        if (userMomentTopicVO.getIsPublish() != null) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, userMomentTopicVO.getIsPublish());
        }

        Page<UserMomentTopic> page = new Page<>();
        page.setCurrent(userMomentTopicVO.getCurrentPage());
        page.setSize(userMomentTopicVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.SORT);
        IPage<UserMomentTopic> pageList = userMomentTopicService.page(page, queryWrapper);
        List<UserMomentTopic> list = pageList.getRecords();

        List<String> fileUidList = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUidList.add(item.getFileUid());
            }
        });

        Map<String, String> pictureMap = fileFeignUtil.fileUidToFileUrlMap(fileUidList);
        for (UserMomentTopic item : list) {
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid()) && pictureMap.get(item.getFileUid()) != null) {
                item.setPhotoUrl(pictureMap.get(item.getFileUid()));
            }
        }
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public List<UserMomentTopic> getList() {
        return null;
    }

    @Override
    public String addUserMomentTopic(UserMomentTopicVO userMomentTopicVO) {
        UserMomentTopic userMomentTopic = new UserMomentTopic();
        BeanUtil.copyProperties(userMomentTopicVO, userMomentTopic, SysConf.STATUS);
        userMomentTopic.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editUserMomentTopic(UserMomentTopicVO userMomentTopicVO) {
        UserMomentTopic userMomentTopic = userMomentTopicService.getById(userMomentTopicVO.getUid());
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(userMomentTopicVO, userMomentTopic, SysConf.STATUS, SysConf.UID);
        userMomentTopic.setUpdateTime(new Date());
        userMomentTopic.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchUserMomentTopic(List<UserMomentTopicVO> userMomentTopicVOList) {
        if (userMomentTopicVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        userMomentTopicVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<UserMomentTopic> userMomentTopics = userMomentTopicService.listByIds(uids);
        userMomentTopics.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        userMomentTopicService.updateBatchById(userMomentTopics);
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }
}
