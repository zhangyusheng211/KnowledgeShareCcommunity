package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * MediaActorVO
 *
 * @author thh
 * @date 2021-12-14
 */
@ToString
@Data
public class MediaActorVO extends BaseVO<MediaActorVO> {

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 简述
     */
    private String description;

    /**
     * 奖项
     */
    private String awards;

    /**
     * 标签
     */
    private String label;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 点击数
     */
    private Integer clickCount;

    /**
     * 备注
     */
    private String remark;

    private List<String> notSelectedIdsList;

}
