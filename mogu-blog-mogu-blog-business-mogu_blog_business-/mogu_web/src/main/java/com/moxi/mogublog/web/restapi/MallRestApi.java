package com.moxi.mogublog.web.restapi;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.CommonCategory;
import com.moxi.mogublog.commons.entity.Product;
import com.moxi.mogublog.commons.vo.CommonCategoryVO;
import com.moxi.mogublog.commons.vo.ProductVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.manager.ProductManager;
import com.moxi.mogublog.xo.service.CommonCategoryService;
import com.moxi.mogublog.xo.service.ProductService;
import com.moxi.mougblog.base.enums.EPublish;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.GetOne;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 商品相关RestApi
 *
 * @author 陌溪
 * @date 2024年6月2日11:18:51
 */
@RestController
@RequestMapping("/mall")
@Api(value = "商品相关接口", tags = {"商品相关接口"})
@Slf4j
public class MallRestApi {

    @Resource
    private ProductService productService;
    @Resource
    private ProductManager productManager;

    @Resource
    private CommonCategoryService commonCategoryService;

    @ApiOperation(value = "获取商品列表信息", notes = "获取商品列表信息")
    @PostMapping("/getProductList")
    public String getProductList(@Validated({GetList.class}) @RequestBody ProductVO productVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取商品列表信息");
        productVO.setIsPublish(EPublish.PUBLISH);
        IPage<Product> productIPage = productService.getPageList(productVO);
        return ResultUtil.successWithData(productIPage);
    }

    @ApiOperation(value = "获取商品详情信息", notes = "获取商品列表信息")
    @PostMapping("/getProductDetail")
    public String getProductDetail(@Validated({GetOne.class}) @RequestBody ProductVO productVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取商品详情信息");
        Product product = productManager.getProductDetail(productVO);
        return ResultUtil.successWithData(product);
    }

    @ApiOperation(value = "获取通用分类列表信息", notes = "获取通用分类列表信息")
    @PostMapping("/getCommonCategoryList")
    public String getCommonCategoryList(@Validated({GetList.class}) @RequestBody CommonCategoryVO commonCategoryVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取商品列表信息");
        commonCategoryVO.setIsPublish(EPublish.PUBLISH);
        IPage<CommonCategory> commonCategoryIPage = commonCategoryService.getPageList(commonCategoryVO);
        return ResultUtil.successWithData(commonCategoryIPage);
    }
}

