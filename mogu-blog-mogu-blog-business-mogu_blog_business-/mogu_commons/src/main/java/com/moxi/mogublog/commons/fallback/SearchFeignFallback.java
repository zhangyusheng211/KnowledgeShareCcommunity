package com.moxi.mogublog.commons.fallback;

import com.moxi.mogublog.commons.feign.SearchFeignClient;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 搜索服务降级兜底方法【当服务不可用时会触发】
 *
 * @author: 陌溪
 * @create: 2020年10月6日09:03:19
 */
@Component
@Slf4j
public class SearchFeignFallback implements SearchFeignClient {

    @Override
    public String cleanEsIndex() {
        log.error("搜索服务出现异常, 服务降级返回,  清空ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 清空ElasticSearch索引失败");
    }

    @Override
    public String batchCreateEsIndex(List<AggEsDoc> aggEsDocList) {
        log.error("搜索服务出现异常, 服务降级返回, 创建ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 创建ElasticSearch索引失败");
    }

    @Override
    public String batchUpdateEsIndex(List<AggEsDoc> aggEsDocList) {
        log.error("搜索服务出现异常, 服务降级返回, 更新ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 更新ElasticSearch索引失败");
    }

    @Override
    public String batchDeleteEsIndex(List<String> aggEsDocIdList) {
        log.error("搜索服务出现异常, 服务降级返回, 批量删除ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 删除ElasticSearch索引失败");
    }
}
