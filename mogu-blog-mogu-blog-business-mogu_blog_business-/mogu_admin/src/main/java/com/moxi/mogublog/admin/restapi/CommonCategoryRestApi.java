package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.vo.CommonCategoryVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.CommonCategoryService;
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
 * 通用分类相关 RestApi
 *
 * @author 陌溪
 * @date 2024年6月1日21:56:53
 */
@RestController
@RequestMapping("/commonCategory")
@Api(value = "通用分类相关接口", tags = {"通用分类相关接口"})
@Slf4j
public class CommonCategoryRestApi {

    @Resource
    private CommonCategoryService commonCategoryService;

    @AuthorityVerify
    @ApiOperation(value = "获取通用分类列表", notes = "获取通用分类列表", response = String.class)
    @PostMapping(value = "/getList")
    public String getList(@Validated({GetList.class}) @RequestBody CommonCategoryVO commonCategoryVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取通用分类列表: {}", commonCategoryVO);
        return ResultUtil.successWithData(commonCategoryService.getPageList(commonCategoryVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加通用分类")
    @ApiOperation(value = "增加通用分类", notes = "增加通用分类", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody CommonCategoryVO commonCategoryVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加通用分类: {}", commonCategoryVO);
        return commonCategoryService.addCommonCategory(commonCategoryVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑通用分类")
    @ApiOperation(value = "编辑通用分类", notes = "编辑通用分类", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody CommonCategoryVO commonCategoryVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑通用分类: {}", commonCategoryVO);
        return commonCategoryService.editCommonCategory(commonCategoryVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除通用分类")
    @ApiOperation(value = "删除通用分类", notes = "删除通用分类", response = String.class)
    @PostMapping("/deleteBatch")
    public String deleteBatch(@Validated({Delete.class}) @RequestBody List<CommonCategoryVO> commonCategoryVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("删除通用分类: {}", commonCategoryVOList);
        return commonCategoryService.deleteBatchCommonCategory(commonCategoryVOList);
    }

}

