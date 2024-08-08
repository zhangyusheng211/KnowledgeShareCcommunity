package com.moxi.mogublog.pay.service.impl;

import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beust.ah.A;
import com.ijpay.alipay.AliPayApi;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.commons.feign.SmsFeignClient;
import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.commons.global.MessageConf;
import com.moxi.mogublog.commons.vo.AdminVO;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mogublog.commons.vo.WithdrawVO;
import com.moxi.mogublog.pay.global.SQLConf;
import com.moxi.mogublog.pay.global.SysConf;
import com.moxi.mogublog.pay.mapper.WithdrawMapper;
import com.moxi.mogublog.pay.service.OrderAmountLogService;
import com.moxi.mogublog.pay.service.WithdrawService;
import com.moxi.mogublog.pay.utils.DomainEventUtil;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import com.moxi.mougblog.base.vo.FileVO;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 提现表 服务实现类
 *
 * @author 陌溪
 * @date 2022年7月18日08:30:13
 */
@Service
public class WithdrawServiceImpl extends SuperServiceImpl<WithdrawMapper, Withdraw> implements WithdrawService {

    @Resource
    private WithdrawService withdrawService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private SmsFeignClient smsFeignClient;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Resource
    DomainEventUtil domainEventUtil;

    @Override
    public IPage<Withdraw> getPageList(WithdrawVO withdrawVO) {
        Page<Withdraw> page = new Page<>();
        page.setCurrent(withdrawVO.getCurrentPage());
        page.setSize(withdrawVO.getPageSize());

        QueryWrapper<Withdraw> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(withdrawVO.getKeyword())) {
            queryWrapper.or().eq(BaseSQLConf.USER_UID, withdrawVO.getKeyword()).or().eq(BaseSQLConf.ACCOUNT, withdrawVO.getKeyword()).or().eq(BaseSQLConf.UID, withdrawVO.getKeyword());
        }
        if (withdrawVO.getUserUid() != null) {
            queryWrapper.eq(BaseSQLConf.USER_UID, withdrawVO.getUserUid());
        }
        if (withdrawVO.getAuditStatus() != null) {
            queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, withdrawVO.getAuditStatus());
        }
        if (withdrawVO.getWithdrawStatus() != null) {
            queryWrapper.eq(BaseSQLConf.WITHDRAW_STATUS, withdrawVO.getWithdrawStatus());
        }

        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(BaseSQLConf.CREATE_TIME);

        if (StringUtils.isNotEmpty(withdrawVO.getUid())) {
            queryWrapper.eq(BaseSQLConf.UID, withdrawVO.getUid());
        }

        // 排序字段
        if (StringUtils.isNotEmpty(withdrawVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(withdrawVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(withdrawVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(withdrawVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }

        IPage<Withdraw> withdrawIPage = withdrawService.page(page, queryWrapper);
        List<Withdraw> withdrawList = withdrawIPage.getRecords();
        List<String> userUidList = new ArrayList<>();
        List<String> adminUidList = new ArrayList<>();
        List<String> fileUidList = new ArrayList<>();

        withdrawList.forEach(item -> {
            userUidList.add(item.getUserUid());
            adminUidList.add(item.getAdminUid());
            fileUidList.add(item.getFileUid());
        });

        // 组装admin
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

        // 组装用户
        Map<String, Admin> adminMap = new HashMap<>();
        if (adminUidList.size() > 0) {
            AdminVO adminVO = new AdminVO();
            adminVO.setAdminUidList(adminUidList);
            adminVO.setPageSize(10L);
            adminVO.setCurrentPage(1L);
            List<Admin> adminList = smsFeignClient.getAdminListByPage(adminVO);
            for (Admin admin : adminList) {
                adminMap.put(admin.getUid(), admin);
            }
        }

        // 组装文件
        Map<String, File> fileMap;
        if (fileUidList.size() > 0) {
            FileVO fileVO = new FileVO();
            fileVO.setFileUidList(fileUidList);
            fileVO.setCurrentPage(1L);
            fileVO.setPageSize(1000L);
            fileMap = pictureFeignClient.getFileListMap(fileVO);
        } else {
            fileMap = new HashMap<>();
        }


        withdrawList.forEach(item -> {
            item.setUser(userMap.get(item.getUserUid()));
            item.setAdmin(adminMap.get(item.getAdminUid()));
            File file = fileMap.get(item.getFileUid());
            if (file != null) {
                item.setPhotoUrl(file.getPictureUrl());
            }
        });
        withdrawIPage.setRecords(withdrawList);
        return withdrawIPage;
    }

    @Override
    public Long getWithdrawAmount(WithdrawVO withdrawVO) {
        QueryWrapper<Withdraw> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(withdrawVO.getUserUid())) {
            queryWrapper.eq(BaseSQLConf.USER_UID, withdrawVO.getUserUid());
        }
        queryWrapper.select("sum(amount) as amount");
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);

        Withdraw withdraw = withdrawService.getOne(queryWrapper);

        if (withdraw == null) {
            return 0L;
        }
        return withdraw.getAmount();
    }

    @Override
    public String addWithdraw(WithdrawVO withdrawVO) {
        // 解析token，获取用户信息
        String userUid = RequestHolder.getUserUid();
        Long userAmount = getUserAmount(userUid);
        String errorMessage;
        if (withdrawVO.getAmount() == 0) {
            throw new BusinessException("提现金额不能为空");
        }
        if (withdrawVO.getAmount() > userAmount) {
            throw new BusinessException("提现金额大于总金额");
        }
        if (StringUtils.isEmpty(withdrawVO.getAccount())) {
            throw new BusinessException("微信账号不能为空");
        }

        String limitCount = smsFeignClient.getSysParamsByKey(SysConf.SYS_WITHDRAW_LIMIT);
        double limit = Double.parseDouble(limitCount);
        if (withdrawVO.getAmount() < limit) {
            throw new BusinessException("单次提现金额必须大于：" + limit/100 + "元");
        }

        // 消耗用户金额
        String updateAmountResult = smsFeignClient.updateAmountByUserUid(userUid, -withdrawVO.getAmount());
        errorMessage = ResultUtil.getErrorMessage(updateAmountResult);
        if (StringUtils.isNotEmpty(errorMessage)) {
            log.error("[addWithdraw] 扣除用户钱包余额失败, errorMessage: " + errorMessage);
            throw new InsertException(errorMessage);
        }

        // 新增提现单
        Withdraw withdraw = new Withdraw();
        withdraw.setUserUid(userUid);
        withdraw.setAccount(withdrawVO.getAccount());
        withdraw.setWithdrawStatus(EWithdrawStatus.Init.getStatus()); // 设置状态，提现状态
        withdraw.setAmount(withdrawVO.getAmount());
        withdraw.setFileUid(withdrawVO.getFileUid());
        withdraw.setAuditStatus(EAuditStatus.WAIT);
        withdraw.insert();

        // 插入一条流水记录【需要再一个事务处理完成】
        OrderAmountLog orderAmountLog = new OrderAmountLog();
        orderAmountLog.setUserAmount(-withdrawVO.getAmount());
        // 提现平台不分账
        orderAmountLog.setPlatformAmount(0L);
        orderAmountLog.setOldAmount(userAmount);
        orderAmountLog.setNewAmount(userAmount - withdrawVO.getAmount());
        orderAmountLog.setUserUid(userUid);
        orderAmountLog.setBusinessType(EBusinessType.WITHDRAW.getCode());
        orderAmountLog.setStatus(EStatus.ENABLE);
        orderAmountLog.setResourceType(EResourceType.WITHDRAW.getType());
        orderAmountLog.setResourceUid(withdraw.getUid());
        orderAmountLog.insert();

        // 发送提交提现领域事件
        domainEventUtil.publishEvent(EventAction.ADD_WITHDRAW, withdraw);

        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @NotNull
    private Long getUserAmount(String userUid) {
        // 获取用户当前未提现的总金额
        String result = smsFeignClient.getUserAmount(userUid);
        if (!ResultUtil.checkSuccess(result)) {
            log.error("[addWithdraw] 提现异常，获取用户钱包金额失败, userUid: " + userUid);
            throw new InsertException("服务异常，请稍后再试");
        }
        Object amountObj = ResultUtil.getData(result);
        Double amountDouble = StringUtils.convertEvery(amountObj, Double.class);
        if (amountDouble == null) {
            log.error("[addWithdraw] 提现异常，转化用户金额失败, userUid: " + userUid);
            throw new InsertException("服务异常，请稍后再试");
        }
        return StringUtils.convertEvery(amountObj, Double.class).longValue();
    }

    @Override
    public String auditWithdraw(WithdrawVO withdrawVO) {
        // 解析token，获取用户信息
        String adminUid = RequestHolder.getAdminUid();
        String userName = RequestHolder.getUserName();
        Withdraw withdraw = withdrawService.getById(withdrawVO.getUid());
        if (withdraw == null) {
            throw new QueryException("查询不到提现单");
        }

        // 增加分布式锁，防止一次提现被多次触发
        String key = "withdraw:" + withdraw.getUid();
        String withdrawStr = redisUtil.get(key);
        if (StringUtils.isNotEmpty(withdrawStr)) {
            log.error("[auditWithdraw] 提现正在处理中, withdrawUid: " + withdraw.getUid());
            throw new QueryException("提现正在处理中");
        }
        if (!EAuditStatus.WAIT.equals(withdraw.getAuditStatus())) {
            log.error("[auditWithdraw] 该提现已审批完成，无法再次处理, withdrawUid: " + withdraw.getUid());
            throw new QueryException("该提现已审批完成，无法再次处理");
        }
        // 设置分布式锁
        redisUtil.setEx(key, key, 10, TimeUnit.MINUTES);
        if (EAuditStatus.AGREE.equals(withdrawVO.getAuditStatus())) {
            // 审核状态设置同意
            withdraw.setAuditStatus(EAuditStatus.AGREE);
            // 提现状态设为完成
            withdraw.setWithdrawStatus(EWithdrawStatus.Finish.getStatus());
        } else if (Objects.equals(withdrawVO.getAuditStatus(), EAuditStatus.REJECT)) {
            withdraw.setAuditStatus(EAuditStatus.REJECT);
            withdraw.setRejectReason(withdrawVO.getRejectReason());
            // 同时需要将用户申请提现的资金打回用户的账户
            if (withdraw.getAmount() > 0) {
                Long returnAmount = withdraw.getAmount();
                Long userAmount = getUserAmount(withdraw.getUserUid());
                // 退回用户钱包余额
                String updateAmountResult = smsFeignClient.updateAmountByUserUid(withdraw.getUserUid(), returnAmount);
                String errorMessage = ResultUtil.getErrorMessage(updateAmountResult);
                if (StringUtils.isNotEmpty(errorMessage)) {
                    log.error("[addWithdraw] 退款失败, errorMessage: " + errorMessage);
                    throw new InsertException(errorMessage);
                }
                // 记录一份用户提现退款日志
                // 插入一条流水记录【需要再一个事务处理完成】
                OrderAmountLog orderAmountLog = new OrderAmountLog();
                orderAmountLog.setUserAmount(withdraw.getAmount());
                // 提现平台不分账
                orderAmountLog.setPlatformAmount(0L);
                orderAmountLog.setOldAmount(userAmount);
                orderAmountLog.setNewAmount(userAmount + returnAmount);
                orderAmountLog.setUserUid(withdraw.getUserUid());
                orderAmountLog.setBusinessType(EBusinessType.WITHDRAW_FAIL.getCode());
                orderAmountLog.setStatus(EStatus.ENABLE);
                orderAmountLog.setResourceType(EResourceType.WITHDRAW.getType());
                orderAmountLog.setResourceUid(withdraw.getUid());
                orderAmountLog.insert();
            }
            withdraw.setWithdrawStatus(EWithdrawStatus.Fail.getStatus());
        }
        withdraw.setAdminUid(adminUid);
        withdraw.setAuditTime(new Date());
        withdraw.setAuditName(userName);
        withdraw.updateById();
        // 删除分布式锁
        redisUtil.delete(key);

        // 发送审核提现领域事件
        domainEventUtil.publishEvent(EventAction.AUDIT_WITHDRAW, withdraw);

        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public String editWithdraw(WithdrawVO withdrawVO) {
        return null;
    }

    @Override
    public Withdraw getRecentWithdraw() {
        WithdrawVO withdrawVO = new WithdrawVO();
        withdrawVO.setUserUid(RequestHolder.getUserUid());
        withdrawVO.setCurrentPage(1L);
        withdrawVO.setPageSize(1L);
        IPage<Withdraw> withdrawIPage = getPageList(withdrawVO);
        List<Withdraw> withdrawList = withdrawIPage.getRecords();
        if (withdrawList.size() > 0) {
            return withdrawList.get(0);
        }
        return null;
    }
}
