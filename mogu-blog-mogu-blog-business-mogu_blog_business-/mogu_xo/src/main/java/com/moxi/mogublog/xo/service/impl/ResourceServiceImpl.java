package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.Resource;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.vo.PayOrderVO;
import com.moxi.mogublog.commons.vo.ResourceVO;
import com.moxi.mogublog.utils.DateUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.ResourceMapper;
import com.moxi.mogublog.xo.service.CommonService;
import com.moxi.mogublog.xo.service.ResourceService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.enums.EOrderStatus;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 资源 服务实现类
 *
 * @author 陌溪
 * @date 2023年3月23日23:50:20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceServiceImpl extends SuperServiceImpl<ResourceMapper, Resource> implements ResourceService {

    @Autowired
    private ResourceService resourceService;

    @javax.annotation.Resource
    private FileFeignUtil fileFeignUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonService commonService;

    /**
     * 领域事件工具类
     */
    @javax.annotation.Resource
    private DomainEventUtil domainEventUtil;

    @javax.annotation.Resource
    private RedisUtil redisUtil;

    @Override
    public IPage<Resource> getPageList(ResourceVO resourceVO) {
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(resourceVO.getKeyword()) && !StringUtils.isEmpty(resourceVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.NAME, resourceVO.getKeyword().trim()).or().like(SQLConf.SUMMARY, resourceVO.getKeyword().trim());
        }

        if (resourceVO.getResourceSort() != null) {
            queryWrapper.eq(SQLConf.RESOURCE_SORT, resourceVO.getResourceSort());
        }
        if (resourceVO.getPayType() != null) {
            queryWrapper.eq(SQLConf.PAY_TYPE, resourceVO.getPayType());
        }
        if (resourceVO.getChargeType() != null) {
            queryWrapper.eq(SQLConf.CHARGE_TYPE, resourceVO.getChargeType());
        }
        if (resourceVO.getPanType() != null) {
            queryWrapper.eq(SQLConf.PAY_TYPE, resourceVO.getPanType());
        }

        if (StringUtils.isNotEmpty(resourceVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(resourceVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(resourceVO.getOrderByDescColumn())) {
            if (SQLConf.RANDOM.equals(resourceVO.getOrderByDescColumn())) {
                queryWrapper.last(" order by RAND() desc");
            } else {
                // 将驼峰转换成下划线
                String column = StringUtils.underLine(new StringBuffer(resourceVO.getOrderByDescColumn())).toString();
                queryWrapper.orderByDesc(column);
            }
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT, SQLConf.CREATE_TIME);
        }

        Page<Resource> page = new Page<>();
        page.setCurrent(resourceVO.getCurrentPage());
        page.setSize(resourceVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        IPage<Resource> pageList = resourceService.page(page, queryWrapper);
        List<Resource> list = pageList.getRecords();

        List<String> fileUidList = new ArrayList<>();
        List<String> userUidList = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUidList.add(item.getFileUid());
            }
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                userUidList.add(item.getUserUid());
            }
        });

        Map<String, User> userMap = new HashMap<>();
        if (userUidList.size() > 0) {
            userMap = userService.getUserAvatarMapByIds(userUidList);
        }

        Map<String, String> pictureMap = fileFeignUtil.fileUidToFileUrlMap(fileUidList);
        for (Resource item : list) {
            //获取分类资源
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                item.setPhotoUrl(pictureMap.get(item.getFileUid()));
            }
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
        }
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String addResource(ResourceVO resourceVO) {
        Resource resource = new Resource();
        BeanUtils.copyProperties(resourceVO, resource, SysConf.STATUS);
        // 获取用户ID
        String userUid = userService.getUserUidByRequest();
        resource.setUserUid(userUid);
        resource.insert();
        // 增加资源
        domainEventUtil.publishEvent(EventAction.RESOURCE_ADD, resource);
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editResource(ResourceVO resourceVO) {
        Resource resource = resourceService.getById(resourceVO.getUid());
        BeanUtils.copyProperties(resourceVO, resource, SysConf.STATUS, SysConf.UID);
        resource.updateById();
        // 编辑资源
        domainEventUtil.publishEvent(EventAction.RESOURCE_EDIT, resource);
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchResource(List<ResourceVO> resourceVOList) {
        if (resourceVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        resourceVOList.forEach(item -> {
            uids.add(item.getUid());
        });
        Collection<Resource> blogSortList = resourceService.listByIds(uids);
        blogSortList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
            // 删除资源
            domainEventUtil.publishEvent(EventAction.RESOURCE_EDIT, item);
        });
        boolean save = resourceService.updateBatchById(blogSortList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }

    @Override
    public Resource getDetail(ResourceVO resourceVO) {
        if (StringUtils.isEmpty(resourceVO.getUid())) {
            throw new QueryException(MessageConf.PARAM_INCORRECT);
        }
        Resource resource = resourceService.getById(resourceVO.getUid());
        if (resource == null) {
            throw new QueryException(MessageConf.ENTITY_NOT_EXIST);
        }
        Map<String, User> userMap = userService.getUserAvatarMapByIds(Arrays.asList(resource.getUserUid()));
        resource.setUser(userMap.get(resource.getUserUid()));
        PayOrderVO payOrderVO = new PayOrderVO();
        payOrderVO.setOrderStatus(EOrderStatus.Finish);
        payOrderVO.setResourceUid(resource.getUid());
        resource.setPayOrderCount(commonService.getPayOrderCount(payOrderVO));

        // 记录访问次数
        String ip = RequestHolder.getIp();
        String jsonResult = redisUtil.get(RedisConf.RESOURCE_CLICK + Constants.SYMBOL_COLON + ip + Constants.SYMBOL_COLON + resourceVO.getUid());
        if (jsonResult == null) {
            //给资源增加点击数量
            Integer clickCount = resource.getClickCount() + 1;
            resource.setClickCount(clickCount);
            resource.updateById();

            //将该用户点击记录存储到redis中
            redisUtil.setEx(RedisConf.RESOURCE_CLICK + Constants.SYMBOL_COLON + ip + Constants.SYMBOL_COLON + resourceVO.getUid(), clickCount.toString(),
                    DateUtils.getNowToNextDaySeconds(), TimeUnit.SECONDS);

            // 过滤掉内容和简介【拷贝个对象，过滤掉无效信息】
            Resource tempResource = new Resource();
            tempResource.setUid(resource.getUid());
            tempResource.setUserUid(resource.getUserUid());
            // 发布访问的事件
            domainEventUtil.publishEvent(EventAction.RESOURCE_VISIT, tempResource);
        }

        // 检查是否购买
        boolean isPay = commonService.checkWhetherPay(resourceVO.getUid());
        // 未支付，将网盘密码隐藏
        if (!isPay) {
            resource.setPanPath("");
            resource.setPanCode("");
        }
        resource.setPay(isPay);
        return resource;
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public Resource download(ResourceVO resourceVO) {
        // 检查资源是否支付
        boolean isPay = commonService.checkWhetherPay(resourceVO.getUid());
        // 已经支付，直接返回
        if (!isPay) {
            throw new QueryException("请先完成支付后，在下载资源");
        }
        // 通过id，获取资源
        Resource resource = resourceService.getById(resourceVO.getUid());
        if (resource == null) {
            throw new QueryException(MessageConf.ENTITY_NOT_EXIST);
        }

        // 下载资源
        Integer downloadCount = resource.getDownloadCount() + 1;
        resource.setDownloadCount(downloadCount);
        resource.updateById();
        // 发布资源下载的事件
        domainEventUtil.publishEvent(EventAction.RESOURCE_DOWNLOAD, resource);
        return resource;
    }

    @Override
    public Integer getResourceCount(ResourceVO resourceVO) {
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(resourceVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, resourceVO.getUserUid());
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Integer count = resourceService.count(queryWrapper);
        return count;
    }

}
