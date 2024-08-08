package com.moxi.mogublog.commons.schema;

import com.moxi.mougblog.base.enums.ECreditType;
import com.moxi.mougblog.base.enums.EResourceType;
import lombok.Data;

/**
 * 积分变更请求
 * @date 2023年5月7日20:36:56
 */
@Data
public class CreditsChangeRequest {

    /**
     * 积分变更类型
     */
    private ECreditType creditType;

    /**
     * 变更的积分【可能是负数，就是扣除积分】
     */
    private Integer changeCredits;

    /**
     * 资源uid
     */
    private String resourceUid;

    /**
     * 资源类型
     */
    private EResourceType resourceType;
}
