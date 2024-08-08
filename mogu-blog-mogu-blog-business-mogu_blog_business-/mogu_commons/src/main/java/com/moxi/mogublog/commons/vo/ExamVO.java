package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 试卷表
 *
 * @author 陌溪
 * @date 2022年4月13日21:42:26
 */
@Data
public class ExamVO extends BaseVO<ExamVO> {

    /**
     * 唯一oid，自动递增
     */
    private Integer oid;
    /**
     * 用户id
     */
    private String userUid;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 点击数
     */
    private Integer clickCount;
    /**
     * 收藏数
     */
    private Integer collectCount;
    /**
     * 排序字段
     */
    private Integer sort;
    /**
     * 问题标签uid列表
     */
    private String examTagUid;
    /**
     * 是否公开
     */
    private Integer isOpen;

    // 以下字段不存入数据库，封装为了方便使用
    /**
     * 试卷问题id集合
     */
    private List<String> problemUid;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
