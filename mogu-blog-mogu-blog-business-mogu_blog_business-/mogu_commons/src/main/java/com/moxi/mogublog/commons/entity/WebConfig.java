package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mogublog.commons.schema.ComponentShowMap;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.List;

/**
 * 网站配置表
 *
 * @author 陌溪
 * @date 2018年11月11日14:54:12
 */
@Data
@TableName("t_web_config")
public class WebConfig extends SuperEntity<WebConfig> {

    private static final long serialVersionUID = 1L;


    /**
     * 网站Logo
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String logo;

    /**
     * 网站名称
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String name;

    /**
     * 标题
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String title;

    /**
     * 描述
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String summary;

    /**
     * 关键字
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String keyword;

    /**
     * 作者
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String author;

    /**
     * 备案号【备案号不能为空】
     */
    private String recordNum;

    /**
     * 支付宝收款码FileId
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String aliPay;

    /**
     * 微信收款码FileId
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String weixinPay;

    /**
     * 友链申请模板
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String linkApplyTemplate;

    /**
     * 是否开启网页端评论(0:否， 1:是)
     */
    private String openComment;

    /**
     * 是否开启移动端评论(0:否， 1:是)
     */
    private String openMobileComment;

    /**
     * 是否开启赞赏(0:否， 1:是)
     */
    private String openAdmiration;

    /**
     * 是否开启移动端赞赏(0:否， 1:是)
     */
    private String openMobileAdmiration;

    /**
     * 是否开启聊天(0:否， 1:是)
     */
    private String openChat;

    /**
     * github地址
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String github;

    /**
     * gitee地址
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String gitee;

    /**
     * QQ号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String qqNumber;

    /**
     * QQ群
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String qqGroup;

    /**
     * 微信号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String weChat;

    /**
     * 微信公众号二维码
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String wechatFileUid;

    /**
     * 邮箱
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String email;

    /**
     * 显示的列表（用于控制邮箱、QQ、QQ群、Github、Gitee、微信是否显示在前端）
     */
    private String showList;

    /**
     * 登录方式列表（用于控制前端登录方式，如账号密码,码云,Github,QQ,微信）
     */
    private String loginTypeList;

    /**
     * 支持的聊天类型列表（用于控制 表情、语音、图片、通话、视频 是否显示在前端）
     */
    private String chatTypeList;

    /**
     * 是否开启用户创作【0 关闭，1 开启】
     */
    private String openCreateBlog;

    /**
     * 是否开启问答【0 关闭，1 开启】
     */
    private String openCreateQuestion;

    /**
     * 是否开启加载校验
     */
    private String openLoadingValid;

    /**
     * 加载校验文件uid
     */
    private String loadingValidFileUid;

    /**
     * 微信小程序二维码【用于绑定小程序】
     */
    private String miniFileUid;

    /**
     * 加载校验文本
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String loadingValidText;

    /**
     * 现金支付方式列表：官方支付宝、官方微信、第三方支付宝、第三方微信
     */
    private String cashPayMethodList;

    /**
     * 登录优先级【控制首页】
     */
    private String loginPriority;

    /**
     * 展开登录方式
     */
    private String spreadLoginType;

    /**
     * 组件展示列表
     */
    private String componentShowList;

    // 以下字段不存入数据库，封装为了方便使用

    /**
     * 标题图
     */
    @TableField(exist = false)
    private List<String> photoList;

    /**
     * Logo图片
     */
    @TableField(exist = false)
    private String logoPhoto;


    /**
     * 支付宝付款码
     */
    @TableField(exist = false)
    private String aliPayPhoto;

    /**
     * 微信付款码
     */
    @TableField(exist = false)
    private String weixinPayPhoto;

    /**
     * 加载校验图
     */
    @TableField(exist = false)
    private String loadingValidPhoto;

    /**
     * 微信公众号
     */
    @TableField(exist = false)
    private String wechatPhoto;

    /**
     * 小程序二维码
     */
    @TableField(exist = false)
    private String miniPhoto;


    /**
     * 组件展示Map
     */
    @TableField(exist = false)
    private ComponentShowMap componentShowMap;
}
