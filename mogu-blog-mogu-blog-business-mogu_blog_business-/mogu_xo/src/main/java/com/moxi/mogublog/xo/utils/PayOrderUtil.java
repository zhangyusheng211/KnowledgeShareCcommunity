package com.moxi.mogublog.xo.utils;

import com.moxi.mogublog.commons.entity.Resource;
import com.moxi.mogublog.commons.schema.ProductVO;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付相关工具类
 */
@Slf4j
public class PayOrderUtil {


    /**
     * 将resource转productVO
     *
     * @param resource
     * @return
     */
    public static ProductVO resource2Product(Resource resource) {
        if (resource == null) {
            log.error("[resource2Product] 资源不存在");
            throw new QueryException(MessageConf.ENTITY_NOT_EXIST);
        }
        ProductVO productVO = new ProductVO();
        productVO.setName(resource.getName());
        productVO.setSummary(resource.getSummary());
        productVO.setResourceType(EResourceType.RESOURCE.getType());
        productVO.setResourceUid(resource.getUid());
        productVO.setUserUid(resource.getUserUid());
        productVO.setPrice(resource.getPrice());
        productVO.setDiscountPrice(resource.getDiscountPrice());
        productVO.setChargeType(resource.getChargeType());
        productVO.setDiscountStartTime(resource.getDiscountStartTime());
        productVO.setDiscountEndTime(resource.getDiscountEndTime());
        productVO.setPhotoUrl(resource.getPhotoUrl());
        productVO.setPayType(resource.getPayType());
        return productVO;
    }
}
