package com.moxi.mogublog.xo.service.impl;

import com.moxi.mogublog.commons.entity.Blog;
import com.moxi.mogublog.commons.entity.PayOrder;
import com.moxi.mogublog.commons.entity.Subject;
import com.moxi.mogublog.commons.feign.PayFeignClient;
import com.moxi.mogublog.commons.global.MessageConf;
import com.moxi.mogublog.commons.schema.CheckResourceVisitAuthVO;
import com.moxi.mogublog.commons.schema.ProductVO;
import com.moxi.mogublog.commons.schema.VisitAuthExtra;
import com.moxi.mogublog.commons.vo.PayOrderVO;
import com.moxi.mogublog.utils.DateUtils;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.BlogService;
import com.moxi.mogublog.xo.service.CommonService;
import com.moxi.mogublog.xo.service.SubjectService;
import com.moxi.mogublog.xo.utils.SensitiveUtils;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

    @Autowired
    private SensitiveUtils sensitiveUtils;
    @Autowired
    private PayFeignClient payFeignClient;
    @Resource
    private BlogService blogService;
    @Resource
    private SubjectService subjectService;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public String checkIsAudit(HttpServletRequest request, String... contents) {

        for (String content : contents) {
            // 判断是否为广告
            Map<String, String> sensitiveBroadMap = sensitiveUtils.filter(content, true, SysConf.SYS_SENSITIVE_SLOGAN);
            Boolean flg = false;
            if (Integer.parseInt(sensitiveBroadMap.get(SysConf.COUNT)) > Constants.NUM_ZERO) {
                flg = true;
            }
            //查看当前用户 是否是特权用户
            double userTag = 0D;
            if (request.getAttribute(SysConf.USER_TAG) != null) {
                userTag = (double) request.getAttribute(SysConf.USER_TAG);
            }
            if (flg) {
                // 如果是特权用户，免检
                if (userTag <= EUserTag.NORMAL_USER.getValue().intValue()) {
                    return EAuditStatus.WAIT;
                } else {
                    return EAuditStatus.AGREE;
                }
            }
        }
        return EAuditStatus.AGREE;
    }

    @Override
    public PayOrder buildPayOrder(ProductVO productVO) {
        String userUid = RequestHolder.getUserUid();
        double userTag = RequestHolder.getUserTag();
        if (StringUtils.isEmpty(userUid)) {
            throw new QueryException(MessageConf.LOGIN_AUTH);
        }
        PayOrder payOrder = new PayOrder();
        payOrder.setUserUid(userUid);
        payOrder.setMerchantUserUid(productVO.getUserUid());
        payOrder.setResourceUid(productVO.getResourceUid());
        payOrder.setResourceType(productVO.getResourceType());
        payOrder.setProductPrice(productVO.getPrice());
        payOrder.setTitle(productVO.getName());
        payOrder.setPayType(productVO.getPayType());
        payOrder.setSummary(productVO.getSummary());

        // 判断是否是折扣商品
        long price = productVO.getPrice();
        // 如果是折扣商品，需要判断下是否符合折扣的日期
        if (EChargeType.Discount.getType() == productVO.getChargeType()) {
            // 折扣商品，需要判断是否在折扣区间
            if (productVO.getDiscountStartTime() == null || productVO.getDiscountEndTime() == null) {

            } else {
                boolean checkIn = DateUtils.checkNowBetweenIn(productVO.getDiscountStartTime(), productVO.getDiscountEndTime());
                if (checkIn) {
                    price = productVO.getDiscountPrice();
                }
            }
        }

        // 如果是会员免费商品，需要判断下是否是会员
        if (EChargeType.VIP_Free.getType() == productVO.getChargeType()) {
            // 判断是否是会员，如果是会员，价格为0元，非会员正常价
            if (userTag >= 1) {
                price = 0L;
            }
        }
        payOrder.setPrice(price);
        payOrder.setSnapshot(JsonUtils.objectToJson(productVO));
        return payOrder;
    }

    @Override
    public boolean checkWhetherPay(String productUid) {
        String result = payFeignClient.checkWhetherPay(productUid);
        Map<String, Object> resultMap = JsonUtils.jsonToMap(result);
        if (resultMap != null && SysConf.SUCCESS.equals(resultMap.get(SysConf.CODE)) && resultMap.get(SysConf.DATA) != null) {
            return resultMap.get(SysConf.DATA).toString().equals("true");
        }
        return false;
    }

    @Override
    public int getPayOrderCount(PayOrderVO payOrderVO) {
        String result = payFeignClient.getPayOrderCount(payOrderVO);
        Map<String, Object> resultMap = JsonUtils.jsonToMap(result);
        if (resultMap != null && SysConf.SUCCESS.equals(resultMap.get(SysConf.CODE)) && resultMap.get(SysConf.DATA) != null) {
            int value = ((Number) resultMap.get(SysConf.DATA)).intValue();
            return value;
        }
        return 0;
    }

    @Override
    public int getPayOrderSumFee(PayOrderVO payOrderVO) {
        String result = payFeignClient.getPayOrderSumFee(payOrderVO);
        Map<String, Object> resultMap = JsonUtils.jsonToMap(result);
        if (resultMap != null && SysConf.SUCCESS.equals(resultMap.get(SysConf.CODE)) && resultMap.get(SysConf.DATA) != null) {
            int value = ((Number) resultMap.get(SysConf.DATA)).intValue();
            return value;
        }
        return 0;
    }

    @Override
    public boolean checkResourceVisit(CheckResourceVisitAuthVO checkResourceVisitAuthVO) {
        String userUid = RequestHolder.getUserUid();
        if (StringUtils.isEmpty(userUid)) {
            return false;
        }
        boolean checkSuccess = checkResourceVisitAuth(checkResourceVisitAuthVO);
        // 缓存校验成功的结果，避免重复输入密码
        if (checkSuccess) {
            redisUtil.setEx(RedisConf.VISIT_AUTH + Constants.SYMBOL_COLON + checkResourceVisitAuthVO.getResourceUid() + Constants.SYMBOL_COLON + userUid, JsonUtils.objectToJson(true), 7, TimeUnit.DAYS);
        }
        return checkSuccess;
    }

    /**
     * 校验资源访问权限
     * @param checkResourceVisitAuthVO
     * @return
     */
    private boolean checkResourceVisitAuth(CheckResourceVisitAuthVO checkResourceVisitAuthVO) {
        // 校验资源访问权限
        EResourceType resourceType = checkResourceVisitAuthVO.getResourceType();
        if (resourceType == null) {
            return false;
        }
        List<EResourceType> whiteList =  new ArrayList<>();
        whiteList.add(EResourceType.BLOG);
        whiteList.add(EResourceType.SUBJECT);
        if (!whiteList.contains(resourceType)) {
            log.error("[checkResourceVisit] 暂时只支持博客和专栏校验");
            return false;
        }
        // 获取访问权限对应的
        String visitAuthExtraStr = this.getVisitAuthExtra(checkResourceVisitAuthVO);
        if (StringUtils.isEmpty(visitAuthExtraStr)) {
            return false;
        }
        VisitAuthExtra visitAuthExtra1 = JsonUtils.jsonToPojo(visitAuthExtraStr, VisitAuthExtra.class);
        if (visitAuthExtra1 == null) {
            return false;
        }
        // 如果是校验密码的，直接校验即可
        if (Objects.equals(checkResourceVisitAuthVO.getVisitAuth(), EVisitAuthType.PASSWORD.getType())) {
            // 校验密码是否正常
            if (checkResourceVisitAuthVO.getPassword().equals(visitAuthExtra1.getPassword())) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 根据资源类型获取访问权限VO
     * @param checkResourceVisitAuthVO
     * @return
     */
    private String getVisitAuthExtra(CheckResourceVisitAuthVO checkResourceVisitAuthVO) {
        switch (checkResourceVisitAuthVO.getResourceType()) {
            case BLOG: {
                Blog blog = blogService.getById(checkResourceVisitAuthVO.getResourceUid());
                if (blog != null) {
                    return blog.getVisitAuthExtra();
                }
            } break;
            case SUBJECT: {
                Subject subject = subjectService.getById(checkResourceVisitAuthVO.getResourceUid());
                if (subject != null) {
                    return subject.getVisitAuthExtra();
                }
            } break;
        }
        return "";
    }
}
