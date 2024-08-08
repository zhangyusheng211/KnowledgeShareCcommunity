package com.moxi.mogublog.xo.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.vo.ReportVO;
import com.moxi.mogublog.xo.dto.ReportOption;

/**
 * 举报接口
 *
 * @author 遇见
 * @date 2021/12/3
 */
public interface ReportManager {

    /**
     * 查询举报列表  以举报信息为主题
     * 分页
     *
     * @param reportOption
     * @return
     */
    Page<ReportVO> queryList(ReportOption reportOption);

    /**
     * 查询举报列表  以被举报内容为主题
     *
     * @param reportOption
     * @return
     */
    Page<ReportVO> queryListByContent(ReportOption reportOption);

    /**
     * 举报稿件/问答/评论
     *
     * @param reportOption
     * @return
     */
    ReportVO submitReport(ReportOption reportOption);

    /**
     * 处理稿件
     *
     * @param reportContentUid 被举报的内容ID
     * @param reportType       举报的内容类型
     * @param reportUid        举报记录Uid
     * @param progress         处理方式
     * @return
     */
    String handleReport(String reportContentUid, String reportType, String reportUid, Integer progress);
}
