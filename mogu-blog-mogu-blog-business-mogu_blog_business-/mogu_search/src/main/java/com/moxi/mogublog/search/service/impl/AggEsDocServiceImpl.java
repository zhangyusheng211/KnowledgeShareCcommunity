package com.moxi.mogublog.search.service.impl;

import cn.easyes.core.biz.EsPageInfo;
import cn.easyes.core.conditions.select.LambdaEsQueryWrapper;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.search.doc.AggEsDocument;
import com.moxi.mogublog.search.global.SysConf;
import com.moxi.mogublog.search.mapper.AggEsDocMapper;
import com.moxi.mogublog.search.service.AggEsDocService;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.EResourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: elasticsearch-tutorials
 * @doc: 批量操作API https://blog.csdn.net/weixin_44282094/article/details/125622021
 * @description: 产品文档相关服务的实现类
 * @author: 陌溪
 * @create: 2022年8月21日22:52:31
 **/
@Slf4j
@Service
public class AggEsDocServiceImpl implements AggEsDocService {

    @Resource
    private AggEsDocMapper aggEsDocMapper;
    @Resource
    private ElasticsearchClient elasticsearchClient;

    @Override
    public Map<String, Object> search(String keyword, EResourceType resourceType, Integer currentPage, Integer pageSize) {

        if (currentPage == 0) {
            currentPage = 1;
        }
        // 查询文档列表
        LambdaEsQueryWrapper<AggEsDocument> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(AggEsDocument::getTitle, keyword, 10f).or().match(AggEsDocument::getSummary, keyword, 5f).or().match(AggEsDocument::getContent, keyword);
        if (resourceType != null) {
            wrapper.eq(AggEsDocument::getResourceType, resourceType.getType());
        }
        EsPageInfo<AggEsDocument> pageInfo = aggEsDocMapper.pageQuery(wrapper, currentPage, pageSize);
        List<AggEsDoc> aggEsDocList = convertDto(pageInfo.getList());

        Map<String, Object> map = new HashMap<>();
        map.put(SysConf.TOTAL, pageInfo.getTotal());
        map.put(SysConf.PAGE_SIZE, pageSize);
        map.put(SysConf.CURRENT_PAGE, currentPage + 1);
        map.put(SysConf.BLOG_LIST, aggEsDocList);

        return map;
    }

    @Override
    public Boolean bulkCreate(List<AggEsDoc> aggEsDocuments) {
        List<AggEsDocument> aggEsDocumentList = convertBo(aggEsDocuments);
        try {
            BulkRequest.Builder br = new BulkRequest.Builder();
            // 将每一个product对象都放入builder中
            aggEsDocumentList.stream()
                    .forEach(aggEsDocument -> br
                            .operations(op -> op
                                    .index(idx -> idx
                                            .index(SysConf.ES_INDEX)
                                            .id(aggEsDocument.getUid())
                                            .document(aggEsDocument))));
            BulkResponse response = elasticsearchClient.bulk(br.build());
            if (!response.errors()) {
                return true;
            }
        } catch (Exception e) {
            log.error("[bulkCreate] 插入ES索引失败");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean bulkUpdate(List<AggEsDoc> aggEsDocuments) {
        List<AggEsDocument> aggEsDocumentList = convertBo(aggEsDocuments);
        try {
            BulkRequest.Builder br = new BulkRequest.Builder();
            // 将每一个product对象都放入builder中
            aggEsDocumentList.stream()
                    .forEach(aggEsDocument -> br
                            .operations(op -> op
                                    .update(idx -> idx
                                            .index(SysConf.ES_INDEX)
                                            .id(aggEsDocument.getUid())
                                            .action(a -> a
                                                    .doc(aggEsDocument)
                                            ))));

            BulkResponse response = elasticsearchClient.bulk(br.build());
            if (!response.errors()) {
                return true;
            }
        } catch (Exception e) {
            log.error("[bulkCreate] 更新ES索引失败");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean bulkDelete(List<String> docIds) {

        try {
            BulkRequest.Builder br = new BulkRequest.Builder();
            // 将每一个product对象都放入builder中
            docIds.stream()
                    .forEach(id -> br
                            .operations(op -> op
                                    .delete(d -> d
                                            .index(SysConf.ES_INDEX)
                                            .id(id))));
            BulkResponse response = elasticsearchClient.bulk(br.build());
            if (!response.errors()) {
                return true;
            }
        } catch (Exception e) {
            log.error("[bulkCreate] 更新ES索引失败");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean deleteIndex(String indexName) {
        log.info("[deleteIndex] 删除索引: {}", indexName);
        return aggEsDocMapper.deleteIndex(indexName);
    }


    /**
     * 转化为传输对象
     *
     * @param aggEsDocumentList
     * @return
     */
    private List<AggEsDoc> convertDto(List<AggEsDocument> aggEsDocumentList) {
        List<AggEsDoc> aggEsDocList = new ArrayList<>();
        aggEsDocumentList.forEach(item -> {
            AggEsDoc aggEsDoc = new AggEsDoc();
            BeanUtils.copyProperties(item, aggEsDoc);
            if (StringUtils.isNotEmpty(item.getHighlightTitle())) {
                aggEsDoc.setTitle(item.getHighlightTitle());
            }
            if (StringUtils.isNotEmpty(item.getHighlightSummary())) {
                aggEsDoc.setSummary(item.getHighlightSummary());
            }
            // 过滤掉内容
            aggEsDoc.setContent("");
            aggEsDocList.add(aggEsDoc);
        });
        return aggEsDocList;
    }

    /**
     * 转化为存储对象
     *
     * @param aggEsDocList
     * @return
     */
    private List<AggEsDocument> convertBo(List<AggEsDoc> aggEsDocList) {
        List<AggEsDocument> aggEsDocumentList = new ArrayList<>();

        for (AggEsDoc item : aggEsDocList) {
            AggEsDocument aggEsDocument = new AggEsDocument();
            BeanUtils.copyProperties(item, aggEsDocument);
            // 过滤掉特殊的html字符
            if (StringUtils.isNotEmpty(aggEsDocument.getTitle())) {
                aggEsDocument.setTitle(StringUtils.filterHtmlTags(aggEsDocument.getTitle()));
            }
            if (StringUtils.isNotEmpty(aggEsDocument.getSummary())) {
                aggEsDocument.setSummary(StringUtils.filterHtmlTags(aggEsDocument.getSummary()));
            }
            aggEsDocumentList.add(aggEsDocument);
        }
        return aggEsDocumentList;
    }
}
