package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

import java.util.Date;

/**
 * 积分日志表
 *
 * @author 陌溪
 * @since 2021年11月27日16:34:27
 */
@Data
public class CreditsLogVO extends BaseVO<CreditsLogVO> {

    /**
     * 用户UID
     */
    private String adminUid;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 操作的资源uid【评论uid、博客uid、问答uid、通过actionCode来区分】
     */
    private String resourceUid;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 动作码
     */
    private String actionCode;

    /**
     * 动作名称
     */
    private String actionName;

    /**
     * 旧的积分数
     */
    private Integer oldCredits;

    /**
     * 变更的积分
     */
    private Integer changeCredits;
    /**
     * 新的积分数
     */
    private Integer newCredits;

    /**
     * 备注
     */
    private String remark;

    private Date createTime;

    /**
     * 信息头
     */
    private String title;

    /**
     * 是否已结算积分
     */
    private String hasGet;
}
