package com.moxi.mogublog.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mogublog.commons.entity.LuckyAwardItem;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 密钥配置表
 *
 * @author 陌溪
 */
@Data
public class SecretConfigVO extends BaseVO<SecretConfigVO> {

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
