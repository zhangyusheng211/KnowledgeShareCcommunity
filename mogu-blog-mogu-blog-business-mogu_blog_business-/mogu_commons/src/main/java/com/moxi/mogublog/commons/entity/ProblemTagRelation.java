package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 问题-标签关系表
 * @author 陌溪
 * @date 2022年3月7日21:42:26
 */
@Data
@TableName("t_problem_tag_relation")
public class ProblemTagRelation extends SuperEntity<ProblemTagRelation> {
    /**
     * 问题uid
     */
    private String problemUid;
    /**
     * 标签uid
     */
    private String tagUid;

}
