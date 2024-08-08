package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

/**
 * 热搜表
 *
 * @author 陌溪
 * @date 2022年6月17日21:42:26
 */
@Data
public class HotSearchVO extends BaseVO<HotSearchVO> {

    /**
     * 用户id
     */
    private String userUid;

    /**
     * 修改内容
     */
    private String content;

    /**
     * 管理员uid
     */
    private String adminUid;

    /**
     * 用户ip
     */
    private String userIp;

}
