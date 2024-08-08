package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.vo.VipConfigVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.VipConfigService;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 会员配置相关 RestApi
 *
 * @author 陌溪
 * @date 2024年5月26日08:33:35
 */
@RestController
@RequestMapping("/vipConfig")
@Api(value = "会员配置相关接口", tags = {"会员配置相关接口"})
@Slf4j
public class VipConfigRestApi {

    @Resource
    private VipConfigService vipConfigService;

    @AuthorityVerify
    @ApiOperation(value = "获取会员配置列表", notes = "获取会员配置列表", response = String.class)
    @PostMapping(value = "/getList")
    public String getList(@Validated({GetList.class}) @RequestBody VipConfigVO vipConfigVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取会员配置列表: {}", vipConfigVO);
        return ResultUtil.successWithData(vipConfigService.getPageList(vipConfigVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加会员配置")
    @ApiOperation(value = "增加会员配置", notes = "增加会员配置", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody VipConfigVO vipConfigVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加会员配置: {}", vipConfigVO);
        return vipConfigService.addVipConfig(vipConfigVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑会员配置")
    @ApiOperation(value = "编辑会员配置", notes = "编辑会员配置", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody VipConfigVO vipConfigVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑会员配置: {}", vipConfigVO);
        return vipConfigService.editVipConfig(vipConfigVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除会员配置")
    @ApiOperation(value = "删除会员配置", notes = "删除会员配置", response = String.class)
    @PostMapping("/deleteBatch")
    public String deleteBatch(@Validated({Delete.class}) @RequestBody List<VipConfigVO> vipConfigVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("删除会员配置: {}", vipConfigVOList);
        return vipConfigService.deleteBatchVipConfig(vipConfigVOList);
    }

}

