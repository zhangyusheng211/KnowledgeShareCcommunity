package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 密钥配置表
 *
 * @author 陌溪
 * @since 2024年2月16日22:57:53
 */
@Data
@TableName("t_secret_config")
public class SecretConfig extends SuperEntity<SecretConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 密钥类型：第三方登录、支付配置、对象存储配置、语言大模型
     */
    private String secretType;

    /**
     * 密钥子类型：第三方登录【QQ登录、Gitee登录、Github登录】、支付配置【阿里、微信、支付宝】
     */
    private String subSecretType;

    /**
     * 业务ID【APPID、ClientID、MchID】
     */
    private String bizId;

    /**
     * 业务密钥
     */
    private String bizSecret;

    /**
     * 业务令牌
     */
    private String bizKey;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 额外配置
     */
    private String extra;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;

    /**
     * 是否发布
     */
    private Integer isPublish;

}
