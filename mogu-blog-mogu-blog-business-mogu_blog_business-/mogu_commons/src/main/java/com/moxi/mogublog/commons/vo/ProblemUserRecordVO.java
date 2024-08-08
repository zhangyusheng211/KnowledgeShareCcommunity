package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

/**
 * 问题用户记录表
 *
 * @author 陌溪
 * @date 2022年3月12日08:54:09
 */
@Data
public class ProblemUserRecordVO extends BaseVO<ProblemUserRecordVO> {
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

    /**
     * 问题标题
     */
    private String title;
}
