package com.moxi.mogublog.web.restapi;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.dto.ReportOption;
import com.moxi.mogublog.xo.manager.ReportManager;
import com.moxi.mogublog.commons.vo.ReportVO;
import com.moxi.mougblog.base.enums.EBehavior;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    @RequestMapping("/list")
    public String getReportByUserUid(@RequestBody ReportOption reportOption) {
        Page<ReportVO> pageList = reportManager.queryList(reportOption);
        return ResultUtil.result(SysConf.SUCCESS, pageList);
    }

    @AvoidRepeatableCommit
    @BussinessLog(value = "举报", behavior = EBehavior.REPORT)
    @ApiOperation(value = "举报", notes = "举报")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reportType", value = "举报内容类型", required = true),
            @ApiImplicitParam(name = "reportContentUid", value = "被举报内容Uid", required = true),
            @ApiImplicitParam(name = "violationType", value = "违规类型", required = true),
            @ApiImplicitParam(name = "content", value = "内容", required = false),
    })
    @PostMapping("/submit")
    public String submit(@RequestBody ReportOption reportOption) {
        ReportVO reportVO = reportManager.submitReport(reportOption);
        return ResultUtil.result(SysConf.SUCCESS, reportVO);
    }
}
