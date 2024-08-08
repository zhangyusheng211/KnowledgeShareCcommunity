package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.group.Default;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

/**
 * UserPraiseRecordVO
 *
 * @author: 陌溪
 * @create: 2022年1月14日21:16:30
 */
@Data
public class UserPraiseRecordVO extends BaseVO<UserPraiseRecordVO> {
    /**
     * 资源类型
     */
    @NotBlank(groups = {Default.class})
    private String resourceType;
    /**
     * 资源uid
     */
    @NotBlank(groups = {Default.class})
    private String resourceUid;
    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 被点赞/点赞的用户uid
     */
    private String toUserUid;

    /**
     * 备注
     */
    private String remark;
    /**
     * 点赞类型
     */
    private String praiseType;
}
