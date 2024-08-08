package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.group.GetOne;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;

/**
 * NoticeVO
 *
 * @author: 陌溪
 * @create: 2021年8月5日23:44:41
 */
@ToString
@Data
public class NoticeVO extends BaseVO<NoticeVO> {

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 管理员
     */
    private String adminUid;

    /**
     * 通知内容
     */
    @NotBlank(groups = {Insert.class, GetOne.class})
    private String content;

    /**
     * 通知类型
     */
    @NotBlank(groups = {Insert.class, GetOne.class})
    private Integer noticeType;

    /**
     * 通知状态  0:已创建，1:已查看'
     */
    private Integer noticeStatus;

    /**
     * 业务uid
     */
    private String businessUid;

    /**
     * 业务类型 【博客，问答，评论】
     */
    private Integer businessType;

    /**
     * 是否前台通知 【0 前台 ，1 后台】
     */
    private Integer isBlack;
}
