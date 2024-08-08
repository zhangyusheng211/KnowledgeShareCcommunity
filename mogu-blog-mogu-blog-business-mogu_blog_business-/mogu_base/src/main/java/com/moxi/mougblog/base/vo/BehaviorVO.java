package com.moxi.mougblog.base.vo;

import com.moxi.mougblog.base.enums.EBehavior;
import com.moxi.mougblog.base.enums.EResourceType;
import lombok.Data;
import lombok.ToString;

/**
 * BehaviorVo：用户行为上报
 *
 * @author: 陌溪
 * @create: 2022年5月4日17:56:04
 */
@Data
@ToString
public class BehaviorVO extends BaseVO<BehaviorVO> {

    /**
     * 业务id
     */
    private String bizUid;

    /**
     * 业务类型
     */
    private EResourceType bizType;

    /**
     * 用户行为
     */
    private EBehavior behavior;

    /**
     * 其它数据（用户访问的页面、用户操作的标签）
     */
    private String otherData;
}
