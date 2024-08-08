package com.moxi.mogublog.admin.restapi;

import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.dto.ReportOption;
import com.moxi.mogublog.xo.manager.ReportManager;
import com.moxi.mogublog.xo.service.ReportService;
import com.moxi.mogublog.commons.vo.ReportVO;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.GetList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 举报接口 RestApi
 *
 * @author 遇见
 * @date 2018-09-04
 */
@RestController
@RefreshScope
@RequestMapping("/report")
@Api(value = "举报相关接口", tags = {"举报相关接口"})
@Slf4j
public class ReportRestApi {
    /**
     * 举报模块服务
     */
    @Resource
    ReportManager reportManager;

    @Resource
    ReportService reportService;

    @ApiOperation(value = "获取举报列表", notes = "获取举报列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody ReportVO reportVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(reportService.getPageList(reportVO));
    }

    @ApiOperation(value = "批量删除举报列表", notes = "批量删除举报列表", response = String.class)
    @PostMapping("/deleteBatch")
    public String deleteBatch(@RequestBody List<String> reportUidList) {
        return ResultUtil.successWithData(reportService.batchDeleteReport(reportUidList));
    }

    /**
     * 获取举报信息
     *
     * @param reportOption
     * @return
     */
    @ApiOperation(value = "获取举报信息", notes = "获取举报信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "关键词", required = false),
            @ApiImplicitParam(name = "currentPage", value = "当前页数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数目", required = true)
    })
    @PostMapping("/list")
    public String list(@RequestBody ReportOption reportOption) {
        return ResultUtil.successWithData(reportManager.queryListByContent(reportOption));
    }

    /**
     * 处理举报信息
     *
     * @param reportOption
     * @return
     */
    @ApiOperation(value = "处理举报信息", notes = "处理举报信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reportContentUid", value = "被举报稿件Uid", required = true),
            @ApiImplicitParam(name = "reportType", value = "举报的稿件类型", required = true),
            @ApiImplicitParam(name = "progress", value = "处理状态", required = true),
            @ApiImplicitParam(name = "reportUid", value = "举报记录的Uid", required = false)
    })
    @PostMapping("/handle")
    public String getReportByUserUid(@RequestBody ReportOption reportOption) {
        return reportManager.handleReport(reportOption.getReportContentUid(), reportOption.getReportType(), reportOption.getReportUid(), reportOption.getProgress());
    }
}
