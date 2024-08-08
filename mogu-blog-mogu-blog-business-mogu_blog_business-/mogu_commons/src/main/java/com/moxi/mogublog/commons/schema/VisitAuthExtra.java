package com.moxi.mogublog.commons.schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mougblog.base.enums.EAuthCode;
import com.moxi.mougblog.base.enums.EVisitAuthType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 可访问权限
 */
@Data
public class VisitAuthExtra {

    /**
     * 可访问的用户UID【指定用户可见】
     */
    private List<String> userUidList;

    /**
     * 可访问的密码【密码可见时】
     */
    private String password;

    /**
     * 可访问的用户标签【标签用户可见】
     */
    private List<String> userTagList;

    /**
     * 支付类型
     */
    private Integer payType;

    /**
     * 支付价格
     */
    private Integer price;

    /**
     * 加载范围： 1 都不可见，2 部分可见
     */
    private Integer loadingArea;

    /**
     * 访问权限文本【用于内容无访问权限时，展示的内容】
     */
    private String visitAuthText;
}
