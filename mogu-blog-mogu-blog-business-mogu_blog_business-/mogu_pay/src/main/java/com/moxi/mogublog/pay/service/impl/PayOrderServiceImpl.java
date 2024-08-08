package com.moxi.mogublog.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.PayOrder;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.feign.SmsFeignClient;
import com.moxi.mogublog.commons.schema.ProductVO;
import com.moxi.mogublog.commons.vo.PayOrderVO;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mogublog.pay.global.SQLConf;
import com.moxi.mogublog.pay.global.SysConf;
import com.moxi.mogublog.pay.mapper.PayOrderMapper;
import com.moxi.mogublog.pay.service.PayOrderService;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.EOrderStatus;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支付订单实现类
 *
 * @author 陌溪
 * @date 2023年4月25日22:21:35
 */
@Slf4j
@RefreshScope
@Service
public class PayOrderServiceImpl extends SuperServiceImpl<PayOrderMapper, PayOrder> implements PayOrderService {

    @Resource
    private PayOrderService payOrderService;
    @Resource
    private SmsFeignClient smsFeignClient;

    @Override
    public IPage<PayOrder> getPageList(PayOrderVO payOrderVO) {
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(payOrderVO.getResourceType())) {
            queryWrapper.eq(SQLConf.RESOURCE_TYPE, payOrderVO.getResourceType());
        }
        if (payOrderVO.getPayType() != null) {
            queryWrapper.eq(SQLConf.PAY_TYPE, payOrderVO.getPayType());
        }
        if (payOrderVO.getPayMethod() != null) {
            queryWrapper.eq(SQLConf.PAY_METHOD, payOrderVO.getPayMethod());
        }
        if (StringUtils.isNotEmpty(payOrderVO.getKeyword())) {
            queryWrapper.or().eq(BaseSQLConf.UID, payOrderVO.getKeyword()).or().eq(BaseSQLConf.USER_UID, payOrderVO.getKeyword()).or().eq(SQLConf.RESOURCE_UID, payOrderVO.getKeyword());
        }
        if (payOrderVO.getOrderStatus() != null) {
            queryWrapper.eq(SQLConf.ORDER_STATUS, payOrderVO.getOrderStatus());
        }

        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        List<PayOrder> payOrderList = new ArrayList<>();
        IPage<PayOrder> payOrderIPage = new Page<>();
        // 如果要计算累积排行
        if (StringUtils.isNotEmpty(payOrderVO.getOrderByDescColumn()) && SQLConf.SUM.equals(payOrderVO.getOrderByDescColumn())) {
            queryWrapper.groupBy(SQLConf.USER_UID);
            queryWrapper.last(SysConf.LIMIT_ONE);
            int count = payOrderService.count(queryWrapper);
            queryWrapper.last("");
            queryWrapper.orderByDesc(SQLConf.PRICE);
            Page<Map<String, Object>> orderPage = new Page<>(payOrderVO.getCurrentPage(), payOrderVO.getPageSize());
            List<Map<String, Object>> records = payOrderService.pageMaps(orderPage, queryWrapper
                    .select(SQLConf.USER_UID, "SUM(price) AS price")
                    .orderByDesc(SQLConf.PRICE)).getRecords();
            // 将查询结果转换为自定义数据类型
            payOrderList = records.stream().map(record -> {
                PayOrder d = new PayOrder();
                d.setUserUid((String) record.get(SQLConf.USER_UID));
                Double dd = Double.valueOf(record.get(SQLConf.PRICE).toString());
                d.setPrice(dd.longValue());
                return d;
            }).collect(Collectors.toList());
            payOrderIPage.setRecords(payOrderList);
            payOrderIPage.setTotal(count);
            payOrderIPage.setCurrent(payOrderVO.getCurrentPage());
            payOrderIPage.setSize(payOrderVO.getPageSize());
        } else {
            if (StringUtils.isNotEmpty(payOrderVO.getOrderByAscColumn())) {
                // 将驼峰转换成下划线
                String column = StringUtils.underLine(new StringBuffer(payOrderVO.getOrderByAscColumn())).toString();
                queryWrapper.orderByAsc(column);
            } else if (StringUtils.isNotEmpty(payOrderVO.getOrderByDescColumn())) {
                // 将驼峰转换成下划线
                String column = StringUtils.underLine(new StringBuffer(payOrderVO.getOrderByDescColumn())).toString();
                queryWrapper.orderByDesc(column);
            } else {
                queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
            }
            Page<PayOrder> page = new Page<>();
            page.setCurrent(payOrderVO.getCurrentPage());
            page.setSize(payOrderVO.getPageSize());
            payOrderIPage = payOrderService.page(page, queryWrapper);
            payOrderList = payOrderIPage.getRecords();
        }

        List<String> userUidList = new ArrayList<>();
        for (PayOrder payOrder : payOrderList) {
            if (StringUtils.isNotEmpty(payOrder.getSnapshot())) {
                ProductVO productVO = JsonUtils.jsonToPojo(payOrder.getSnapshot(), ProductVO.class);
                payOrder.setProductVO(productVO);
            }
            if (StringUtils.isNotEmpty(payOrder.getUserUid())) {
                userUidList.add(payOrder.getUserUid());
            }
        }

        // 获取用户信息
        List<User> userList = new ArrayList<>();
        if (userUidList.size() > 0) {
            UserVO userVO = new UserVO();
            userVO.setUserUidList(userUidList);
            userVO.setPageSize((long)userUidList.size());
            userVO.setCurrentPage(1L);
            List<User> result = smsFeignClient.getUserListByPage(userVO);
            if (result != null) {
                userList = result;
            }
        }

        Map<String, User> userMap = new HashMap<>();
        if (userList.size() > 0) {
            for (User user : userList) {
                userMap.put(user.getUid(), user);
            }
        }

        for (PayOrder payOrder : payOrderList) {
            if (StringUtils.isEmpty(payOrder.getUserUid())) {
                continue;
            }
            payOrder.setUser(userMap.get(payOrder.getUserUid()));
        }
        return payOrderIPage;
    }

    @Override
    public int getPayOrderCount(PayOrderVO payOrderVO) {
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(payOrderVO.getResourceType())) {
            queryWrapper.eq(SQLConf.RESOURCE_TYPE, payOrderVO.getResourceType());
        }
        if (StringUtils.isNotEmpty(payOrderVO.getResourceUid())) {
            queryWrapper.eq(SQLConf.RESOURCE_UID, payOrderVO.getResourceUid());
        }
        if (StringUtils.isNotEmpty(payOrderVO.getUid())) {
            queryWrapper.eq(BaseSQLConf.UID, payOrderVO.getUid());
        }
        if (payOrderVO.getPayType() != null) {
            queryWrapper.eq(BaseSQLConf.PAY_TYPE, payOrderVO.getPayType());
        }
        if (StringUtils.isNotEmpty(payOrderVO.getUserUid())) {
            queryWrapper.eq(BaseSQLConf.USER_UID, payOrderVO.getUserUid());
        }
        if (payOrderVO.getOrderStatus() != null) {
            queryWrapper.eq(SQLConf.ORDER_STATUS, payOrderVO.getOrderStatus());
        }
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        return payOrderService.count(queryWrapper);
    }

    @Override
    public int getPayOrderSumFee(PayOrderVO payOrderVO) {
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(payOrderVO.getResourceType())) {
            queryWrapper.eq(SQLConf.RESOURCE_TYPE, payOrderVO.getResourceType());
        }
        if (StringUtils.isNotEmpty(payOrderVO.getResourceUid())) {
            queryWrapper.eq(SQLConf.RESOURCE_UID, payOrderVO.getResourceUid());
        }
        if (payOrderVO.getPayType() != null) {
            queryWrapper.eq(BaseSQLConf.PAY_TYPE, payOrderVO.getPayType());
        }
        if (StringUtils.isNotEmpty(payOrderVO.getUid())) {
            queryWrapper.eq(BaseSQLConf.UID, payOrderVO.getUid());
        }
        if (StringUtils.isNotEmpty(payOrderVO.getUserUid())) {
            queryWrapper.eq(BaseSQLConf.USER_UID, payOrderVO.getUserUid());
        }
        if (payOrderVO.getOrderStatus() != null) {
            queryWrapper.eq(SQLConf.ORDER_STATUS, payOrderVO.getOrderStatus());
        }
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.select("sum(price) as total", SQLConf.USER_UID);
        queryWrapper.groupBy(SQLConf.USER_UID);
        Map<String, Object> payOrderMap = payOrderService.getMap(queryWrapper);
        BigDecimal bigDecimal = (BigDecimal) payOrderMap.get("total");
        return bigDecimal.intValue();
    }

    @Override
    public boolean checkWhetherPay(String productUid, String orderUid) {
        String userUid = RequestHolder.getUserUid();
        if (StringUtils.isEmpty(userUid)) {
            return false;
        }
        LambdaQueryWrapper<PayOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayOrder::getUserUid, userUid);
        queryWrapper.eq(PayOrder::getOrderStatus, EOrderStatus.Finish);
        if (StringUtils.isNotEmpty(orderUid)) {
            queryWrapper.eq(PayOrder::getUid, orderUid);
        } else {
            queryWrapper.eq(PayOrder::getResourceUid, productUid);
        }
        queryWrapper.last(SysConf.LIMIT_ONE);
        PayOrder payOrder = payOrderService.getOne(queryWrapper);
        return payOrder != null;
    }
}
