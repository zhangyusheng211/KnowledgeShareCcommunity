package com.moxi.mogublog.web.restapi;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.VipConfig;
import com.moxi.mogublog.commons.vo.VipConfigVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.BlogService;
import com.moxi.mogublog.xo.service.TagService;
import com.moxi.mogublog.xo.service.VipConfigService;
import com.moxi.mougblog.base.enums.EBehavior;
import com.moxi.mougblog.base.enums.EPublish;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 会员相关RestApi
 *
 * @author 陌溪
 * @date 2024年5月26日10:54:20
 */
@RestController
@RequestMapping("/vip")
@Api(value = "会员相关接口", tags = {"会员相关接口"})
@Slf4j
public class VipRestApi {

    @Resource
    private VipConfigService vipConfigService;


    @ApiOperation(value = "获取VIP列表信息", notes = "获取标签的信息")
    @GetMapping("/getList")
    public String getVipList() {
        log.info("获取VIP列表信息");
        VipConfigVO vipConfigVO = new VipConfigVO();
        vipConfigVO.setPageSize(Long.MIN_VALUE);
        vipConfigVO.setCurrentPage(1L);
        vipConfigVO.setIsPublish(1);
        IPage<VipConfig> vipConfigIPage = vipConfigService.getPageList(vipConfigVO);
        return ResultUtil.successWithData(vipConfigIPage);
    }
}

