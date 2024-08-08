package com.moxi.mogublog.commons.vo;

import com.moxi.mogublog.commons.entity.UserMomentTopic;
import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

/**
 * 用户动态话题VO
 *
 * @author 陌溪
 * @date 2021年12月25日23:50:46
 */
@Data
public class UserMomentTopicVO extends BaseVO<UserMomentTopic> {

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 管理员
     */
    private String adminUid;

    /**
     * 动态内容
     */
    @NotBlank
    private String content;

    /**
     * 是否发布
     */
    @NotBlank
    private String isPublish;

    /**
     * 文件uid
     */
    private String fileUid;

    /**
     * 点击数
     */
    private Integer clickCount;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 排序字段
     */
    private Integer sort;
}
