package com.moxi.mogublog.commons.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.moxi.mogublog.commons.entity.Blog;
import com.moxi.mogublog.commons.entity.BlogSort;
import com.moxi.mogublog.commons.entity.Subject;
import com.moxi.mogublog.commons.entity.Tag;
import com.moxi.mogublog.commons.schema.VisitAuthExtra;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.EOriginal;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.validator.annotion.IntegerNotNull;
import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.annotion.Range;
import com.moxi.mougblog.base.validator.group.Default;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * BlogVO
 *
 * @author: 陌溪
 * @create: 2019年12月4日12:26:36
 */
@Data
public class BlogVO extends BaseVO<BlogVO> {

    /**
     * 博客标题
     */
    @NotBlank(groups = {Insert.class, Update.class, Default.class})
    private String title;

    /**
     * 博客简介
     */
    @Range(groups = {Insert.class, Update.class}, max = 200)
    private String summary;

    /**
     * 博客内容
     */
    @NotBlank(groups = {Insert.class, Update.class, Default.class})
    private String content;

    /**
     * 标签uid
     */
    @NotBlank(groups = {Insert.class, Update.class, Default.class})
    private String tagUid;
    /**
     * 博客分类UID
     */
    @NotBlank(groups = {Insert.class, Update.class, Default.class})
    private String blogSortUid;
    /**
     * 标题图片UID
     */
    private String fileUid;
    /**
     * 管理员UID
     */
    private String adminUid;
    /**
     * 是否发布
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String isPublish;
    /**
     * 是否原创
     */
    @NotBlank(groups = {Insert.class, Update.class, Default.class})
    private String isOriginal;
    /**
     * 如果原创，作者为管理员名
     */
    @NotBlank(groups = {Update.class})
    private String author;
    /**
     * 文章出处
     */
    private String articlesPart;
    /**
     * 推荐级别，用于首页推荐
     * 0：正常
     * 1：一级推荐(轮播图)
     * 2：二级推荐(top)
     * 3：三级推荐 ()
     * 4：四级 推荐 (特别推荐)
     */
    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer level;

    /**
     * 类型【0 博客， 1：推广】
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String type;

    /**
     * 外链【如果是推广，那么将跳转到外链】
     */
    private String outsideLink;

    /**
     * 标签,一篇博客对应多个标签
     */
    private List<Tag> tagList;

    // 以下字段不存入数据库，封装为了方便使用
    /**
     * 标题图
     */
    private List<String> photoList;
    /**
     * 博客分类
     */
    private BlogSort blogSort;
    /**
     * 点赞数
     */
    private Integer praiseCount;
    /**
     * 版权申明
     */
    private String copyright;

    /**
     * 博客等级关键字，仅用于getList
     */
    private String levelKeyword;

    /**
     * 使用Sort字段进行排序 （0：不使用， 1：使用），默认为0
     */
    private Integer useSort;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 是否开启评论(0:否， 1:是)
     */
    private String openComment;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;
    /**
     * 依据排序
     */
    private String orderBy;
    /**
     * 投稿用户UID
     */
    private String userUid;

    /**
     * 文章来源【0 后台添加，1 用户投稿】
     */
    private String articleSource;

    /**
     * 审核状态【0：审核未通过，1：审核通过】
     */
    private String auditStatus;

    /**
     * 审核人
     */
    private String auditName;

    /**
     * 审批拒绝原因
     */
    private String rejectReason;

    /**
     * 是否开启加载校验
     */
    private String openLoadingValid;

    /**
     * 是否可以查看特权文章  '0' 普通用户只能查看普通文章， '特权用户可以查看特权文章'
     */
    private String isVip;

    /**
     * 是否前台通知 【0 前台 ，1 后台】
     */
    private Integer isBlack;

    /**
     * 是否仅专栏可见：0 否， 1 是
     */
    private Integer isOnlySubjectShow;

    /**
     * 阅读权限：1 公开、2 登录后可见、3 评论后可见、4 输入验证码可见、5 会员可见、6 付费可见 【对应数组】
     */
    private String visitAuth;

    /**
     * 访问可见扩展字段【如：存储哪些会员可见、密码可见对应的密码】 对应 VisitAuthExtra 序列化得到
     */
    private String visitAuthExtra;

    /**
     *  访问可见扩展字段
     */
    @TableField(exist = false)
    private VisitAuthExtra visitAuthExtraVo;

    /**
     * 包含专栏 0 否 1 是 【未启用】
     */
    private String containsSubject;

    /**
     * 价格
     */
    private Integer price;

    /**
     * 支付类型：1:积分支付，2: 现金支付
     */
    private Integer payType;

    /**
     * 加载范围： 0 都不可见，1 部分可见
     */
    private Integer loadingArea;

    /**
     * 博客所属专题列表【1个博客可能属于多个专题】
     */
    private List<Subject> subjectList;


    /**
     *  博客内容复制权限：false表示可复制博客内容，true表示不可复制博客内容
     */
    private Integer isCopy;

    /**
     * 无参构造方法，初始化默认值
     */
    public BlogVO() {
        this.level = 0;
        this.useSort = 0;
    }

    /**
     * 根据VO创建DO对象
     *
     * @param isAdmin     是否是管理员
     * @param uid         用户Uid
     * @param userName
     * @param projectName
     * @return
     */
    public Blog buildBlog(Blog blog, Boolean isAdmin, String uid, String userName, String projectName) {
        // 编辑操作时候，会携带blog过来
        if (blog == null) {
            blog = new Blog();
        }

        if (isAdmin) {
            blog.setAdminUid(uid);
        } else {
            blog.setUserUid(uid);
        }

        /**
         * 检测原创
         */
        if (EOriginal.ORIGINAL.equals(this.getIsOriginal())) {
            blog.setAuthor(userName);
            blog.setArticlesPart(projectName);
        } else {
            blog.setAuthor(this.getAuthor());
            blog.setArticlesPart(this.getArticlesPart());
        }
        /**
         * 设置标题 内容 简介
         */
        blog.setTitle(this.getTitle());
        if (StringUtils.isNotEmpty(this.getContent())) {
            blog.setContent(this.getContent());
        }
        if (StringUtils.isNotEmpty(this.getSummary())) {
            blog.setSummary(this.getSummary());
        } else {
            String summary = StringUtils.dealContent(this.getContent());
            if (summary.length() < 190) {
                blog.setSummary(summary);
            } else {
                blog.setSummary(summary.substring(0, 190) + "...");
            }
        }
        // 是否仅专栏可见
        blog.setIsOnlySubjectShow(this.getIsOnlySubjectShow());
        blog.setTagUid(this.getTagUid());
        blog.setBlogSortUid(this.getBlogSortUid());
        blog.setFileUid(this.getFileUid());
        blog.setLevel(this.getLevel());
        blog.setIsOriginal(this.getIsOriginal());
        blog.setIsPublish(this.getIsPublish());
        blog.setType(this.getType());
        blog.setOutsideLink(this.getOutsideLink());
        blog.setStatus(EStatus.ENABLE);
        blog.setOpenComment(this.getOpenComment());
        blog.setArticleSource(this.getArticleSource());
        blog.setAuditStatus(this.getAuditStatus());
        blog.setIsVip(this.getIsVip());
        blog.setIsTop(this.getIsTop());
        blog.setSort(this.getSort());
        blog.setCharCount(this.content.length());
        blog.setPayType(this.getPayType());
        blog.setPrice(this.getPrice());
        blog.setVisitAuth(this.getVisitAuth());
        blog.setIsCopy(this.getIsCopy());
        // 设置访问扩展信息
        if (this.visitAuthExtraVo != null) {
            blog.setVisitAuthExtra(JsonUtils.objectToJson(this.visitAuthExtraVo));
        } else {
            // 初始化一个空的
            VisitAuthExtra emptyVisitAuth = new VisitAuthExtra();
            blog.setVisitAuthExtra(JsonUtils.objectToJson(emptyVisitAuth));
        }
        // 用户投稿，默认都不开启校验
        blog.setOpenLoadingValid(this.getOpenLoadingValid());
        // 设置专题信息
        blog.setSubjectList(this.subjectList);
        blog.setLoadingArea(this.loadingArea);

        return blog;
    }
}
