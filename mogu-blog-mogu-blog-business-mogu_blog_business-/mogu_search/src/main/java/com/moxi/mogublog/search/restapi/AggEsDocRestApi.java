package com.moxi.mogublog.search.restapi;

import com.moxi.mogublog.commons.annotion.FeignSecurity.FeignSecurity;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.search.global.MessageConf;
import com.moxi.mogublog.search.global.SysConf;
import com.moxi.mogublog.search.service.AggEsDocService;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.EResourceType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * ElasticSearch RestAPI
 *
 * @author: 陌溪
 * @create: 2022年8月22日00:07:15
 */
@RequestMapping("/search")
@Api(value = "ElasticSearch相关接口", tags = {"ElasticSearch相关接口"})
@RestController
@Slf4j
public class AggEsDocRestApi {

    @Resource
    private AggEsDocService aggEsDocService;

    @GetMapping("/hello")
    public String hellEs() {
        return "hello es";
    }

    @ApiOperation(value = "通过ElasticSearch搜索聚合内容", notes = "通过ElasticSearch搜索博客", response = String.class)
    @GetMapping(value = "/elasticSearchAgg", produces = "application/json;charset=UTF-8")
    public String searchBlog(
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) EResourceType resourceType,
            @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        if (pageSize > 20) {
            return ResultUtil.errorWithMessage("分页大小设置过大");
        }
        if (StringUtils.isEmpty(keywords)) {
            return ResultUtil.errorWithMessage(MessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        log.info("通过ES聚合搜索内容：" + keywords);
        return ResultUtil.successWithData(aggEsDocService.search(keywords, resourceType, currentPage, pageSize));
    }

    @FeignSecurity
    @ApiOperation(value = "批量删除Es索引", notes = "批量删除Es索引", response = String.class)
    @PostMapping("/batchDeleteEsIndex")
    public String batchDeleteEsIndex(@RequestBody List<String> aggEsDocIdList) {
        log.info("开始批量删除Es索引:" + JsonUtils.objectToJson(aggEsDocIdList));
        if (aggEsDocIdList.size() > 0) {
            aggEsDocService.bulkDelete(aggEsDocIdList);
        }
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    @FeignSecurity
    @ApiOperation(value = "批量创建Es索引", notes = "ElasticSearch初始化索引", response = String.class)
    @PostMapping("/batchCreateEsIndex")
    public String batchCreateEsIndex(@RequestBody List<AggEsDoc> aggEsDocList) {
        log.info("开始批量创建Es索引" + JsonUtils.objectToJson(aggEsDocList));
        // 批量新增
        boolean success = aggEsDocService.bulkCreate(aggEsDocList);
        if (!success) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.OPERATION_FAIL);
        }
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
    }

    @FeignSecurity
    @ApiOperation(value = "批量更新Es索引", notes = "ElasticSearch初始化索引", response = String.class)
    @PostMapping("/batchUpdateEsIndex")
    public String batchUpdateEsIndex(@RequestBody List<AggEsDoc> aggEsDocList) {
        log.info("开始批量更新Es索引:" + JsonUtils.objectToJson(aggEsDocList));
        // 批量更新
        boolean success = aggEsDocService.bulkUpdate(aggEsDocList);
        if (!success) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.OPERATION_FAIL);
        }
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
    }

    @FeignSecurity
    @ApiOperation(value = "清空Es索引", notes = "清空Es索引", response = String.class)
    @PostMapping("/cleanEsIndex")
    public String cleanEsIndex() {
        log.info("开始清空Es索引");
        // 清空Es索引
        boolean success = aggEsDocService.deleteIndex(SysConf.ES_INDEX);
        if (!success) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.OPERATION_FAIL);
        }
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
    }
}
