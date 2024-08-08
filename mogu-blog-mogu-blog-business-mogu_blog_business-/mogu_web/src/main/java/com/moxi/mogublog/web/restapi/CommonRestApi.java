package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.schema.CheckResourceVisitAuthVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.annotion.SubmitVerify.SubmitVerify;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.CommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 通用接口
 *
 * @author 陌溪
 * @date 2022年12月31日18:06:29
 */
@RestController
@RequestMapping("/common")
@Api(value = "通用接口", tags = {"通用接口"})
@Slf4j
public class CommonRestApi {

    @Resource
    CommonService commonService;

    /**
     * 如密码校验
     * @param checkResourceVisitAuthVO
     * @return
     */

    @AvoidRepeatableCommit(timeout = 1000)
    @ApiOperation(value = "校验资源是否有权限访问", notes = "校验资源是否有权限访问")
    @PostMapping("/checkResourceVisitAuth")
    public String checkResourceVisitAuth(@RequestBody CheckResourceVisitAuthVO checkResourceVisitAuthVO) {
        return ResultUtil.result(SysConf.SUCCESS, commonService.checkResourceVisit(checkResourceVisitAuthVO));
    }

}
