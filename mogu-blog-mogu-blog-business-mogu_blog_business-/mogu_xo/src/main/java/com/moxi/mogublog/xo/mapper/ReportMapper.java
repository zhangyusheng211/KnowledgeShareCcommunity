package com.moxi.mogublog.xo.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.Report;
import com.moxi.mogublog.commons.vo.ReportVO;
import com.moxi.mogublog.xo.dto.ReportOption;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 遇见
 */
@Mapper
public interface ReportMapper extends SuperMapper<Report> {
    /**
     * 查询举报列表 以举报信息为主体
     *
     * @param page
     * @param reportOption
     * @return
     */
    Page<ReportVO> queryList(Page page, @Param("reportOption") ReportOption reportOption);

    /**
     * 查询举报列表 以被举报内容为主体
     *
     * @param page
     * @param reportOption
     * @return
     */
    Page<ReportVO> queryListByContent(Page page, @Param("reportOption") ReportOption reportOption);
}
