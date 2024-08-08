package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.vo.SecretConfigVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.SecretConfigService;
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
 * 密钥配置表 RestApi
 *
 * @author 陌溪
 * @date 2024年2月17日09:22:08
 */
@Api(value = "密钥配置相关接口", tags = {"密钥配置相关接口"})
@RestController
@RequestMapping("/secretConfig")
@Slf4j
public class SecretConfigRestApi {

    @Resource
    SecretConfigService secretConfigService;

    @AuthorityVerify
    @ApiOperation(value = "获取密钥配置列表", notes = "获取密钥配置列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody SecretConfigVO secretConfigVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        log.info("获取密钥配置列表:{}", secretConfigVO);
        return ResultUtil.successWithData(secretConfigService.getPageList(secretConfigVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加密钥配置")
    @ApiOperation(value = "增加密钥配置", notes = "增加密钥配置", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody SecretConfigVO resourceSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加密钥配置:{}", resourceSortVO);
        return secretConfigService.addSecretConfig(resourceSortVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑密钥配置")
    @ApiOperation(value = "编辑密钥配置", notes = "编辑密钥配置", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody SecretConfigVO resourceSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑密钥配置:{}", resourceSortVO);
        return secretConfigService.editSecretConfig(resourceSortVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除密钥配置")
    @ApiOperation(value = "批量删除密钥配置", notes = "批量删除密钥配置", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<SecretConfigVO> resourceSortVOList, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除密钥配置:{}", resourceSortVOList);
        return secretConfigService.deleteBatchSecretConfig(resourceSortVOList);
    }
}

