package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 问题用户记录表
 * @author 陌溪
 * @date 2022年3月12日08:56:13
 */
@Data
@TableName("t_problem_user_record")
public class ProblemUserRecord extends SuperEntity<ProblemUserRecord> {
    /**
     * 问题uid
     */
    private String problemUid;
    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 问题掌握程度：1：未掌握、2：见过、3：掌握
     */
    private String degree;
}
