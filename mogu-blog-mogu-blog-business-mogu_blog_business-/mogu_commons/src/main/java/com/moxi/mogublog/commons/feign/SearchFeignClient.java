package com.moxi.mogublog.commons.feign;

import com.moxi.mogublog.commons.config.feign.FeignConfiguration;
import com.moxi.mogublog.commons.fallback.SearchFeignFallback;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mougblog.base.enums.EResourceType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 搜索服务feign远程调用
 *
 * @author 陌溪
 * @date 2020年10月6日09:08:13
 */
@FeignClient(name = "mogu-search", contextId = "searchFeignClient", configuration = FeignConfiguration.class)
public interface SearchFeignClient {


    /**
     * 批量删除ES索引【不指定就删除】
     * @return
     */
    @PostMapping(value = "/search/cleanEsIndex", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String cleanEsIndex();


    /**
     * 批量新增索引
     * @param aggEsDocList
     * @return
     */
    @PostMapping(value = "/search/batchCreateEsIndex", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String batchCreateEsIndex(List<AggEsDoc> aggEsDocList);

    /**
     * 批量更新索引
     * @param aggEsDocList
     * @return
     */
    @PostMapping(value = "/search/batchUpdateEsIndex", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String batchUpdateEsIndex(List<AggEsDoc> aggEsDocList);

    /**
     * 批量删除索引
     *
     * @param aggEsDocIdList
     * @return
     */
    @PostMapping(value = "/search/batchDeleteEsIndex", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String batchDeleteEsIndex(@RequestBody List<String> aggEsDocIdList);


}
