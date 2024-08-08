package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.Emoticon;
import com.moxi.mogublog.commons.vo.EmoticonVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.EmoticonMapper;
import com.moxi.mogublog.xo.service.EmoticonService;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.exception.exceptionType.DeleteException;
import com.moxi.mougblog.base.exception.exceptionType.UpdateException;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 表情包表 服务实现类
 *
 * @author 陌溪
 * @date 2021年4月26日22:56:25
 */
@Service
@Slf4j
public class EmoticonServiceImpl extends SuperServiceImpl<EmoticonMapper, Emoticon> implements EmoticonService {

    @Resource
    EmoticonService emoticonService;

    @Resource
    private FileFeignUtil FileFeignUtil;
    @Resource
    private DomainEventUtil domainEventUtil;

    @Override
    public IPage<Emoticon> getPageList(EmoticonVO emoticonVO) {
        QueryWrapper<Emoticon> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(emoticonVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, emoticonVO.getUserUid());
        }

        if (StringUtils.isNotEmpty(emoticonVO.getIsSelection())) {
            queryWrapper.eq(SQLConf.IS_SELECTION, emoticonVO.getIsSelection());
        }
        Page<Emoticon> page = new Page<>();
        page.setCurrent(emoticonVO.getCurrentPage());
        page.setSize(emoticonVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(emoticonVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(emoticonVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(emoticonVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(emoticonVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT, SQLConf.CREATE_TIME);
        }
        IPage<Emoticon> pageList = emoticonService.page(page, queryWrapper);
        List<String> fileUidList = new ArrayList<>();
        List<Emoticon> emoticonList = pageList.getRecords();
        emoticonList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUidList.add(item.getFileUid());
            }
        });
        // 设置表情包
        Map<String, String> pictureMap = FileFeignUtil.fileUidToFileUrlMap(fileUidList);
        emoticonList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                item.setPhotoUrl(pictureMap.get(item.getFileUid()));
            }
        });
        return pageList;
    }


    @Override
    public String addEmoticon(EmoticonVO emoticonVO) {
        String userUid = RequestHolder.getUserUid();

        // 添加前，判断表情包是否存在
        QueryWrapper<Emoticon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.FILE_UID, emoticonVO.getFileUid());
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        Integer count = emoticonService.count(queryWrapper);
        if (count > 0) {
            return ResultUtil.errorWithMessage("添加失败，该表情包已存在！");
        }
        emoticonVO.setUserUid(userUid);
        Emoticon emoticon = new Emoticon();
        BeanUtil.copyProperties(emoticonVO, emoticon, SysConf.STATUS);
        boolean isSave = emoticon.insert();
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.EMOTICON_ADD, emoticon);
        }
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editEmoticon(EmoticonVO emoticonVO) {
        Emoticon emoticon = emoticonService.getById(emoticonVO.getUid());
        if (emoticon == null) {
            log.error("更新失败，表情包不存在");
            throw new UpdateException("更新失败，表情包不存在");
        }
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(emoticonVO, emoticon, SysConf.STATUS, SysConf.UID);
        emoticon.setUpdateTime(new Date());
        boolean isSave = emoticon.updateById();
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.EMOTICON_EDIT, emoticon);
        }
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String stickyEmoticon(EmoticonVO emoticonVO) {
        Emoticon emoticon = emoticonService.getById(emoticonVO.getUid());
        if (emoticon == null) {
            log.error("置顶失败，表情包不存在");
            throw new UpdateException("置顶失败，表情包不存在");
        }
        // 查询到排名最前的表情包
        QueryWrapper<Emoticon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.SORT);
        queryWrapper.last(SysConf.LIMIT_ONE);
        Emoticon maxEmoticon = emoticonService.getOne(queryWrapper);
        // 比最大的还要大
        if (maxEmoticon != null) {
            // 如果第一名是自己
            if (maxEmoticon.getUid().equals(emoticon.getUid())) {
                return ResultUtil.errorWithMessage("该表情包已在最前！");
            }
            emoticon.setSort(maxEmoticon.getSort() + 1);
        } else {
            emoticon.setSort(1);
        }
        boolean isSave = emoticon.updateById();
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.EMOTICON_STICKY, emoticon);
        }
        return ResultUtil.successWithMessage("置顶表情包成功");
    }

    @Override
    public String deleteBatchEmoticon(List<EmoticonVO> emoticonVOList) {
        String userUid = RequestHolder.getUserUid();
        if (emoticonVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        emoticonVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<Emoticon> tagList = emoticonService.listByIds(uids);
        tagList.forEach(item -> {
            if (!userUid.equals(item.getUserUid())) {
                log.error("您无法删除其它人的表情包");
                throw new DeleteException("您无法删除其它人的表情包");
            }
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
            // 发送删除表情包领域事件
            domainEventUtil.publishEvent(EventAction.EMOTICON_DELETE, item);
        });
        boolean save = emoticonService.updateBatchById(tagList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }
}
