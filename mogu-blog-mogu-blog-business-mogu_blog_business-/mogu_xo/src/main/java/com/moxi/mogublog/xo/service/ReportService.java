package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Report;
import com.moxi.mogublog.commons.vo.ReportVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 举报接口类
 *
 * @author 遇见
 */
public interface ReportService extends SuperService<Report> {
    /**
     * 获取举报列表
     *
     * @param reportVO
     * @return
     */
    IPage<Report> getPageList(ReportVO reportVO);

    /**
     * 批量删除
     *
     * @param reportUidList
     * @return
     */
    String batchDeleteReport(List<String> reportUidList);
}
