package com.moxi.mogublog.xo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.vo.ProductVO;
import com.moxi.mogublog.commons.vo.ReportVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.dto.ReportOption;
import com.moxi.mogublog.xo.manager.ProductManager;
import com.moxi.mogublog.xo.manager.ReportManager;
import com.moxi.mogublog.xo.mapper.ReportMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.EAuditStatus;
import com.moxi.mougblog.base.enums.EPublish;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品模块处理类
 *
 * @author 陌溪
 * @date 2024年6月3日22:52:52
 */
@Slf4j
@Service
public class ProductManagerImpl implements ProductManager {

    @Resource
    ProductService productService;

    @Override
    public Product getProductDetail(ProductVO productVO) {
        if (StringUtils.isEmpty(productVO.getUid())) {
            throw new QueryException("商品不存在");
        }
        Product product = productService.getById(productVO.getUid());
        if (product == null || product.getIsPublish() == 0) {
            throw new QueryException("商品不存在");
        }
        List<Product> productList = Lists.newArrayList(product);
        // 转换商品信息
        productService.convertProductList(productList);
        return productList.get(0);
    }
}
