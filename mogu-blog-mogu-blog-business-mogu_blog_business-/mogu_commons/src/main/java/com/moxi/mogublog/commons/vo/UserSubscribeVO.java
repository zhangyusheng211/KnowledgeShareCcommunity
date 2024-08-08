package com.moxi.mogublog.commons.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.moxi.mougblog.base.validator.annotion.IdValid;
import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.Update;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * 用户订阅表；代表用户订阅了什么资源【用于后续资源更新推送消息】
 *
 * @author 陌溪
 * @date 2023年12月23日10:15:38
 */
@Data
public class UserSubscribeVO extends BaseVO<UserSubscribeVO> {

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 资源UID
     */
    @NotBlank(groups = {Update.class, Delete.class})
    private String resourceUid;

    /**
     * 资源类型
     */
    @NotBlank(groups = {Update.class, Delete.class})
    private String resourceType;

    /**
     * 备注
     */
    private String remark;


    /**
     * 资源ID列表
     */
    @TableField(exist = false)
    private List<String> resourceUidList;
}
