package com.moxi.mogublog.commons.vo;

import com.moxi.mogublog.commons.entity.Report;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * 举报内容的VO对象
 *
 * @author 遇见
 */
@Data
public class ReportVO extends BaseVO<ReportVO> {

    private Integer id;
    /**
     * 举报类型
     */
    private String reportType;
    /**
     * 举报人Uid
     */
    private String userUid;
    /**
     * 举报人名称
     */
    private String userName;
    /**
     * 被举报人Uid
     */
    private String reportUserUid;
    /**
     * 被举报人名称
     */
    private String reportUserName;
    /**
     * 被举报稿件Uid
     */
    private String reportContentUid;
    /**
     * 被举报稿件内容/标题
     */
    private String reportContent;
    /**
     * 违规类型
     */
    private String violationType;
    /**
     * 违规内容
     */
    private String violationContent;
    /**
     * 用户举报详情
     */
    private String content;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 流程
     */
    private Integer progress;


    public static ReportVO create(Report report) {
        ReportVO reportVO = new ReportVO();
        BeanUtils.copyProperties(report, reportVO);
        return reportVO;
    }
}
