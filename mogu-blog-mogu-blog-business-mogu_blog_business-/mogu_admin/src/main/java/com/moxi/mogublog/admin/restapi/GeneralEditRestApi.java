package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.global.SysConf;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.GeneralEditService;
import com.moxi.mogublog.commons.vo.GeneralEditVO;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
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

import java.util.List;

/**
 * 通用修改表 RestApi
 *
 * @author 陌溪
 * @date 2022年3月7日22:37:39
 */
@Api(value = "通用修改接口", tags = {"通用修改接口"})
@RestController
@RequestMapping("/generalEdit")
@Slf4j
public class GeneralEditRestApi {

    @Autowired
    private GeneralEditService generalEditService;

    /**
     * 获取通用修改列表
     *
     * @param generalEditVO
     * @param result
     * @return
     */
    @AuthorityVerify
    @ApiOperation(value = "获取通用修改列表", notes = "获取通用修改列表", response = String.class)
    @PostMapping("/getGeneralEditList")
    public String getGeneralEditList(@Validated({GetList.class}) @RequestBody GeneralEditVO generalEditVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取通用修改列表");
        return ResultUtil.result(SysConf.SUCCESS, generalEditService.getGeneralEditList(generalEditVO));
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除")
    @ApiOperation(value = "批量删除", notes = "批量删除", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<GeneralEditVO> generalEditVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除问题");
        return generalEditService.deleteBatchGeneralEdit(generalEditVOList);
    }

    @AuthorityVerify
    @OperationLogger(value = "审核修改")
    @ApiOperation(value = "审核修改", notes = "审核修改", response = String.class)
    @PostMapping("/audit")
    public String audit(@RequestBody GeneralEditVO generalEditVO) {
        return generalEditService.auditGeneralEdit(generalEditVO);
    }


}
