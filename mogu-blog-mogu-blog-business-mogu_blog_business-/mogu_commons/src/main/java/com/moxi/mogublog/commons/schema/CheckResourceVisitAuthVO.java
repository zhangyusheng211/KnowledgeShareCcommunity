package com.moxi.mogublog.commons.schema;


import com.moxi.mougblog.base.enums.EResourceType;
import lombok.Data;

/**
 *
 */

@Data
public class CheckResourceVisitAuthVO {

    /**
     * 资源类型
     */
    private EResourceType resourceType;

    /**
     * 资源ID
     */
    private String resourceUid;

    /**
     * 校验Code
     */
    private Integer visitAuth;

    /**
     * 校验的密码
     */
    private String password;
}
