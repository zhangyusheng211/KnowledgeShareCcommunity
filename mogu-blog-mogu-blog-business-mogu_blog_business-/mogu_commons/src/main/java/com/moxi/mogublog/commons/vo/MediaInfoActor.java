package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;

/**
 * 媒资演员关联对象 t_media_info_actor
 *
 * @author thh
 * @date 2021-12-14
 */
@ToString
@Data
public class MediaInfoActor extends BaseVO<MediaInfoActor> {

    /**
     * 演员uid
     */
    private String actorUid;

    /**
     * 电影uid
     */
    private String movieUid;

    /**
     * 主演actor  演员
     */
    private String type;

    /**
     * 备注
     */
    private String remark;

}
