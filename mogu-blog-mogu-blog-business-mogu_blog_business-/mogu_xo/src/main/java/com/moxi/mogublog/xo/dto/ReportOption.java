package com.moxi.mogublog.xo.dto;

import com.moxi.mogublog.commons.entity.Report;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mougblog.base.vo.PageInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * 举报内容的操作对象
 *
 * @author 遇见
 */
@Data
public class ReportOption extends PageInfo {

    private String reportUid;
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
     * 举报信息
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
     * 状态
     */
    private Integer status;
    /**
     * 流程
     */
    private Integer progress;

    public Report build(String userUid, String reportUserId) {
        Report report = new Report();
        BeanUtils.copyProperties(this, report, SysConf.STATUS);
        report.setReportUserUid(reportUserId);
        report.setUserUid(userUid);
        return report;
    }
}
