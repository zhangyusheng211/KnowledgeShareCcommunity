package com.moxi.mogublog.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.OrderAmountLog;
import com.moxi.mogublog.commons.entity.PayOrder;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.feign.SmsFeignClient;
import com.moxi.mogublog.commons.global.MessageConf;
import com.moxi.mogublog.commons.schema.ProductVO;
import com.moxi.mogublog.commons.vo.OrderAmountLogVO;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mogublog.pay.global.SQLConf;
import com.moxi.mogublog.pay.mapper.OrderAmountLogMapper;
import com.moxi.mogublog.pay.service.OrderAmountLogService;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.EBusinessType;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 订单流水表 服务实现类
 *
 * @author 陌溪
 * @date 2022年7月18日08:30:13
 */
@Service
public class OrderAmountLogServiceImpl extends SuperServiceImpl<OrderAmountLogMapper, OrderAmountLog> implements OrderAmountLogService {

    @Resource
    private OrderAmountLogService orderAmountLogService;
    @Resource
    private SmsFeignClient smsFeignClient;
    @Resource
    private PayOrderServiceImpl payOrderService;

    @Override
    public IPage<OrderAmountLog> getPageList(OrderAmountLogVO orderAmountLogVO) {
        Page<OrderAmountLog> page = new Page<>();
        page.setCurrent(orderAmountLogVO.getCurrentPage());
        page.setSize(orderAmountLogVO.getPageSize());

        QueryWrapper<OrderAmountLog> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(orderAmountLogVO.getKeyword())) {
            queryWrapper.eq(BaseSQLConf.USER_UID, orderAmountLogVO.getKeyword());
        }
        if (orderAmountLogVO.getBusinessType() != null) {
            queryWrapper.eq(BaseSQLConf.BUSINESS_TYPE, orderAmountLogVO.getBusinessType());
        }
        if (StringUtils.isNotEmpty(orderAmountLogVO.getUid())) {
            queryWrapper.eq(BaseSQLConf.UID, orderAmountLogVO.getUid());
        }
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        // 排序字段
        if (StringUtils.isNotEmpty(orderAmountLogVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(orderAmountLogVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(orderAmountLogVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(orderAmountLogVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }

        IPage<OrderAmountLog> orderAmountLogIPage = orderAmountLogService.page(page, queryWrapper);
        // 获取订单
        List<String> orderUidList = new ArrayList<>();
        List<String> userUidList = new ArrayList<>();
        orderAmountLogIPage.getRecords().forEach(item -> {
            if (EResourceType.ORDER.getType().equals(item.getResourceType()) && StringUtils.isNotEmpty(item.getResourceUid())) {
                orderUidList.add(item.getResourceUid());
            }
            userUidList.add(item.getUserUid());
        });
        Map<String, PayOrder> payOrderMap = new HashMap<>();
        if (orderUidList.size() > 0) {
            Collection<PayOrder> payOrder = payOrderService.listByIds(orderUidList);
            for (PayOrder order : payOrder) {
                // 获取快照
                if (StringUtils.isNotEmpty(order.getSnapshot())) {
                    ProductVO productVO = JsonUtils.jsonToPojo(order.getSnapshot(), ProductVO.class);
                    if (productVO == null) {
                        continue;
                    }
                    order.setProductVO(productVO);
                }
                payOrderMap.put(order.getUid(), order);
            }
        }


        // 组装用户信息
        Map<String, User> userMap = new HashMap<>();
        if (userUidList.size() > 0) {
            UserVO userVO = new UserVO();
            userVO.setUserUidList(userUidList);
            userVO.setPageSize(1000L);
            userVO.setCurrentPage(1L);
            List<User> userList = smsFeignClient.getUserListByPage(userVO);
            for (User user : userList) {
                userMap.put(user.getUid(), user);
            }
        }

        orderAmountLogIPage.getRecords().forEach(item -> {
            item.setPayOrder(payOrderMap.get(item.getResourceUid()));
            item.setUser(userMap.get(item.getUserUid()));
        });
        return orderAmountLogIPage;
    }

    @Override
    public String addOrderAmountLog(OrderAmountLogVO orderAmountLogVO) {
        // 插入一条流水记录【需要再一个事务处理完成】
        OrderAmountLog orderAmountLog = new OrderAmountLog();
        orderAmountLog.setUserAmount(orderAmountLogVO.getUserAmount());
        // 提现平台不分账
        orderAmountLog.setPlatformAmount(0L);
        orderAmountLog.setOldAmount(orderAmountLogVO.getOldAmount());
        orderAmountLog.setNewAmount(orderAmountLogVO.getNewAmount());
        orderAmountLog.setUserUid(orderAmountLogVO.getUserUid());
        orderAmountLog.setBusinessType(orderAmountLogVO.getBusinessType());
        orderAmountLog.setStatus(EStatus.ENABLE);
        orderAmountLog.setResourceType(orderAmountLogVO.getResourceType());
        orderAmountLog.setResourceUid(orderAmountLogVO.getResourceUid());
        orderAmountLog.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editOrderAmountLog(OrderAmountLogVO orderAmountLogVO) {
        return null;
    }

    @Override
    public Long getUserAmount() {
        // 解析token，获取用户信息
        QueryWrapper<OrderAmountLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.USER_UID, RequestHolder.getUserUid());
        queryWrapper.select("sum(user_amount) as amount");
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        Map<String, Object> resultMap = orderAmountLogService.getMap(queryWrapper);
        Long amount = StringUtils.convertEvery(resultMap.get(SQLConf.AMOUNT), Long.class);
        return amount == null ? 0 : amount;
    }
}
