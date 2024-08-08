package com.moxi.mogublog.search.service;

import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mougblog.base.enums.EResourceType;

import java.util.List;
import java.util.Map;

/**
 * @program: elasticsearch-tutorials
 * @description: 文档相关服务的接口
 * @author: 陌溪
 * @create: 2022年8月21日22:51:53
 **/
public interface AggEsDocService {


    /**
     * 根据文档id查找文档
     *
     * @return
     * @throws Exception
     */
    Map<String, Object> search(String keyword, EResourceType resourceType, Integer currentPage, Integer pageSize);

    /**
     * 批量增加文档
     *
     * @param aggEsDocuments 要增加的对象集合
     * @return 批量操作的结果
     * @throws Exception
     */
    Boolean bulkCreate(List<AggEsDoc> aggEsDocuments);

    /**
     * 批量更新文档
     *
     * @param aggEsDocuments
     * @return
     * @throws Exception
     */
    Boolean bulkUpdate(List<AggEsDoc> aggEsDocuments);

    /**
     * 批量删除文档
     *
     * @param docIds 要删除的文档id集合
     * @return
     * @throws Exception
     */
    Boolean bulkDelete(List<String> docIds);

    /**
     * 删除索引
     *
     * @param indexName
     * @return
     * @throws Exception
     */
    Boolean deleteIndex(String indexName);
}
