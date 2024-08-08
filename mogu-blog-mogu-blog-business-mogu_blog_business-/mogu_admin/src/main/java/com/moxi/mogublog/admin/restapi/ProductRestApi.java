package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.vo.ProductVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.ProductService;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品相关 RestApi
 *
 * @author 陌溪
 * @date 2024年6月1日21:56:53
 */
@RestController
@RequestMapping("/product")
@Api(value = "商品相关接口", tags = {"商品相关接口"})
@Slf4j
public class ProductRestApi {

    @Resource
    private ProductService productService;

    @AuthorityVerify
    @ApiOperation(value = "获取商品列表", notes = "获取商品列表", response = String.class)
    @PostMapping(value = "/getList")
    public String getList(@Validated({GetList.class}) @RequestBody ProductVO productVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取商品列表: {}", productVO);
        return ResultUtil.successWithData(productService.getPageList(productVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加商品")
    @ApiOperation(value = "增加商品", notes = "增加商品", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody ProductVO productVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加商品: {}", productVO);
        return productService.addProduct(productVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑商品")
    @ApiOperation(value = "编辑商品", notes = "编辑商品", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody ProductVO productVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑商品: {}", productVO);
        return productService.editProduct(productVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除商品")
    @ApiOperation(value = "删除商品", notes = "删除商品", response = String.class)
    @PostMapping("/deleteBatch")
    public String deleteBatch(@Validated({Delete.class}) @RequestBody List<ProductVO> productVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("删除商品: {}", productVOList);
        return productService.deleteBatchProduct(productVOList);
    }
}

