package com.moxi.mogublog.commons.schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * UpdateCreditsRequest 更新用户积分
 *
 * @author: 陌溪
 * @create: 2023年9月17日17:57:13
 */
@Data
@ToString
public class UpdateCreditsRequest extends BaseVO<UpdateCreditsRequest> {
    /**
     * 更新的用户积分
     */
    private int updateCredits;

    /**
     * 变更积分的用户ID
     */
    private String userUid;

    /**
     * 更新备注
     */
    private String remark;

    /**
     * 是否开启站内信触达
     */
    private String openMessagePush;
}
