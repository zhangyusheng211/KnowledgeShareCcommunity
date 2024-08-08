package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.internal.LinkedTreeMap;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.commons.schema.VisitAuthExtra;
import com.moxi.mogublog.commons.vo.*;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.event.blogEvent.BlogAddEvent;
import com.moxi.mogublog.xo.event.blogEvent.BlogAuditEvent;
import com.moxi.mogublog.xo.event.blogEvent.BlogDeleteEvent;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.*;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mogublog.xo.utils.RabbitMqUtil;
import com.moxi.mogublog.xo.utils.WebUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.exception.exceptionType.UpdateException;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 博客表 服务实现类
 *
 * @author 陌溪
 * @date 2018-09-08
 */
@Service
@Slf4j
public class BlogServiceImpl extends SuperServiceImpl<BlogMapper, Blog> implements BlogService {

    @Resource
    ApplicationContext context;
    @Resource
    private WebUtil webUtil;
    @Resource
    private CommentService commentService;
    @Resource
    private TagService tagService;
    @Resource
    private PictureService pictureService;
    @Resource
    private BlogSortService blogSortService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private BlogSortMapper blogSortMapper;
    @Resource
    private BlogMapper blogMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private UserPraiseRecordMapper userPraiseRecordMapper;
    @Resource
    private UserWatchMapper userWatchMapper;
    @Resource
    private UserMomentMapper userMomentMapper;
    @Autowired
    private AdminService adminService;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private SysParamsService sysParamsService;
    @Autowired
    private BlogService blogService;
    @Resource
    private SubjectItemService subjectItemService;
    @Resource
    private SubjectService subjectService;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private NoticeService noticeService;
    @Resource
    private UserService userService;
    @Resource
    private RabbitMqUtil rabbitMqUtil;
    @Resource
    private FileFeignUtil FileFeignUtil;
    @Resource
    private UserWatchService userWatchService;
    @Resource
    UserMomentTopicService userMomentTopicService;
    @Resource
    FileFeignUtil fileFeignUtil;
    @Resource
    UserMomentService userMomentService;
    @Resource
    UserPraiseRecordService userPraiseRecordService;
    @Resource
    CollectService collectService;
    @Autowired
    private CommonService commonService;
    @Resource
    private UserSubscribeService userSubscribeService;

    @Override
    public List<Blog> convertBlogInfoList(List<Blog> list) {
        if (list.size() == 0) {
            return new ArrayList<>();
        }
        return this.setBlog(list);
    }

    @Override
    public List<Blog> setTagAndSortByBlogList(List<Blog> list) {
        List<String> sortUids = new ArrayList<>();
        List<String> tagUids = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                sortUids.add(item.getBlogSortUid());
            }
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(), BaseSysConf.FILE_SEGMENTATION);
                for (String itemTagUid : tagUidsTemp) {
                    tagUids.add(itemTagUid);
                }
            }
        });
        Collection<BlogSort> sortList = new ArrayList<>();
        Collection<Tag> tagList = new ArrayList<>();
        if (sortUids.size() > 0) {
            sortList = blogSortMapper.selectBatchIds(sortUids);
        }
        if (tagUids.size() > 0) {
            tagList = tagMapper.selectBatchIds(tagUids);
        }
        Map<String, BlogSort> sortMap = new HashMap<>();
        Map<String, Tag> tagMap = new HashMap<>();
        sortList.forEach(item -> {
            sortMap.put(item.getUid(), item);
        });
        tagList.forEach(item -> {
            tagMap.put(item.getUid(), item);
        });
        for (Blog item : list) {

            //设置分类
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                item.setBlogSort(sortMap.get(item.getBlogSortUid()));
            }
            //获取标签
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(), BaseSysConf.FILE_SEGMENTATION);
                List<Tag> tagListTemp = new ArrayList<Tag>();
                tagUidsTemp.forEach(tag -> {
                    tagListTemp.add(tagMap.get(tag));
                });
                item.setTagList(tagListTemp);
            }
        }

        return list;
    }

    @Override
    public List<Blog> setTagAndSortAndPictureByBlogList(List<Blog> list) {

        List<String> sortUids = new ArrayList<>();
        List<String> tagUids = new ArrayList<>();
        Set<String> fileUidSet = new HashSet<>();

        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUidSet.add(item.getFileUid());
            }
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                sortUids.add(item.getBlogSortUid());
            }
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                // tagUid有多个，还需要切分
                if (StringUtils.isNotEmpty(item.getTagUid())) {
                    List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(), BaseSysConf.FILE_SEGMENTATION);
                    for (String itemTagUid : tagUidsTemp) {
                        tagUids.add(itemTagUid);
                    }
                }
            }
        });

        String pictureList = null;
        StringBuffer fileUids = new StringBuffer();
        List<Map<String, Object>> picList = new ArrayList<>();
        // feign分页查询图片数据
        if (fileUidSet.size() > 0) {
            int count = 1;
            for (String fileUid : fileUidSet) {
                fileUids.append(fileUid + ",");
                if (count % 10 == 0) {
                    pictureList = this.pictureFeignClient.getPicture(fileUids.toString(), ",");
                    List<Map<String, Object>> tempPicList = webUtil.getPictureMap(pictureList);
                    picList.addAll(tempPicList);
                    fileUids = new StringBuffer();
                }
                count++;
            }
            // 判断是否存在图片需要获取
            if (fileUids.length() >= Constants.NUM_32) {
                pictureList = this.pictureFeignClient.getPicture(fileUids.toString(), Constants.SYMBOL_COMMA);
                List<Map<String, Object>> tempPicList = webUtil.getPictureMap(pictureList);
                picList.addAll(tempPicList);
            }
        }

        Collection<BlogSort> sortList = new ArrayList<>();
        Collection<Tag> tagList = new ArrayList<>();
        if (sortUids.size() > 0) {
            sortList = blogSortService.listByIds(sortUids);
        }
        if (tagUids.size() > 0) {
            tagList = tagService.listByIds(tagUids);
        }
        Map<String, BlogSort> sortMap = new HashMap<>();
        Map<String, Tag> tagMap = new HashMap<>();
        Map<String, String> pictureMap = new HashMap<>();

        sortList.forEach(item -> {
            sortMap.put(item.getUid(), item);
        });

        tagList.forEach(item -> {
            tagMap.put(item.getUid(), item);
        });

        picList.forEach(item -> {
            pictureMap.put(item.get(SysConf.UID).toString(), item.get(SysConf.URL).toString());
        });

        for (Blog item : list) {
            //设置分类
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {

                item.setBlogSort(sortMap.get(item.getBlogSortUid()));
                if (sortMap.get(item.getBlogSortUid()) != null) {
                    item.setBlogSortName(sortMap.get(item.getBlogSortUid()).getSortName());
                }
            }

            //获取标签
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(), ",");
                List<Tag> tagListTemp = new ArrayList<Tag>();

                tagUidsTemp.forEach(tag -> {
                    tagListTemp.add(tagMap.get(tag));
                });
                item.setTagList(tagListTemp);
            }

            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileUid(), Constants.SYMBOL_COMMA);
                List<String> pictureListTemp = new ArrayList<String>();

                pictureUidsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
                // 只设置一张标题图
                if (pictureListTemp.size() > 0) {
                    item.setPhotoUrl(pictureListTemp.get(0));
                } else {
                    item.setPhotoUrl("");
                }
            }
        }
        return list;
    }

    @Override
    public Blog setTagByBlog(Blog blog) {
        String tagUid = blog.getTagUid();
        if (!StringUtils.isEmpty(tagUid)) {
            String[] uids = tagUid.split(SysConf.FILE_SEGMENTATION);
            List<Tag> tagList = new ArrayList<>();
            for (String uid : uids) {
                Tag tag = tagMapper.selectById(uid);
                if (tag != null && tag.getStatus() != EStatus.DISABLED) {
                    tagList.add(tag);
                }
            }
            blog.setTagList(tagList);
        }
        return blog;
    }

    @Override
    public Blog setBlogInfo(Blog blog) {
        List<Blog> blogList = new ArrayList<>();
        blogList.add(blog);
        this.setBlog(blogList);
        return blogList.get(0);
    }

    @Override
    public Blog setSortByBlog(Blog blog) {

        if (blog != null && !StringUtils.isEmpty(blog.getBlogSortUid())) {
            BlogSort blogSort = blogSortMapper.selectById(blog.getBlogSortUid());
            blog.setBlogSort(blogSort);
        }
        return blog;
    }

    @Override
    public List<Blog> getBlogListByLevel(Integer level) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.LEVEL, level);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);

        List<Blog> list = blogMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public IPage<Blog> getBlogPageByLevel(Page<Blog> page, Integer level, Integer useSort) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.LEVEL, level);
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);

        if (useSort == 0) {
            queryWrapper.orderByDesc(BaseSQLConf.CREATE_TIME);
        } else {
            queryWrapper.orderByDesc(BaseSQLConf.SORT);
        }

        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SysConf.CONTENT));

        return blogMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Integer getBlogCount(Integer status) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        return blogMapper.selectCount(queryWrapper);
    }

    @Override
    public Integer getBlogCount(BlogVO blogVO) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(blogVO.getAdminUid())) {
            queryWrapper.eq(SQLConf.ADMINUID, blogVO.getAdminUid());
        }
        if (StringUtils.isNotEmpty(blogVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, blogVO.getUserUid());
        }
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Integer blogCount = blogService.count(queryWrapper);
        return blogCount;
    }

    @Override
    public List<Map<String, Object>> getBlogCountByTag() {
        // 从Redis中获取标签下包含的博客数量
        String jsonArrayList = redisUtil.get(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_TAG);
        if (StringUtils.isNotEmpty(jsonArrayList)) {
            ArrayList jsonList = JsonUtils.jsonArrayToArrayList(jsonArrayList);
            return jsonList;
        }

        List<Map<String, Object>> blogCoutByTagMap = blogMapper.getBlogCountByTag();
        Map<String, Integer> tagMap = new HashMap<>();
        for (Map<String, Object> item : blogCoutByTagMap) {
            String tagUid = String.valueOf(item.get(SQLConf.TAG_UID));
            // java.lang.Number是Integer,Long的父类
            Number num = (Number) item.get(SysConf.COUNT);
            Integer count = num.intValue();
            //如果只有一个UID的情况
            if (tagUid.length() == 32) {
                //如果没有这个内容的话，就设置
                if (tagMap.get(tagUid) == null) {
                    tagMap.put(tagUid, count);
                } else {
                    Integer tempCount = tagMap.get(tagUid) + count;
                    tagMap.put(tagUid, tempCount);
                }
            } else {
                //如果长度大于32，说明含有多个UID
                if (StringUtils.isNotEmpty(tagUid)) {
                    List<String> strList = StringUtils.changeStringToString(tagUid, ",");
                    for (String strItem : strList) {
                        if (tagMap.get(strItem) == null) {
                            tagMap.put(strItem, count);
                        } else {
                            Integer tempCount = tagMap.get(strItem) + count;
                            tagMap.put(strItem, tempCount);
                        }
                    }
                }
            }
        }

        //把查询到的Tag放到Map中
        Set<String> tagUids = tagMap.keySet();
        Collection<Tag> tagCollection = new ArrayList<>();
        if (tagUids.size() > 0) {
            tagCollection = tagMapper.selectBatchIds(tagUids);
        }

        Map<String, String> tagEntityMap = new HashMap<>();
        for (Tag tag : tagCollection) {
            if (StringUtils.isNotEmpty(tag.getContent())) {
                tagEntityMap.put(tag.getUid(), tag.getContent());
            }
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
            String tagUid = entry.getKey();
            if (tagEntityMap.get(tagUid) != null) {
                String tagName = tagEntityMap.get(tagUid);
                Integer count = entry.getValue();
                Map<String, Object> itemResultMap = new HashMap<>();
                itemResultMap.put(SysConf.TAG_UID, tagUid);
                itemResultMap.put(SysConf.NAME, tagName);
                itemResultMap.put(SysConf.VALUE, count);
                resultList.add(itemResultMap);
            }
        }
        // 将 每个标签下文章数目 存入到Redis【过期时间2小时】
        if (resultList.size() > 0) {
            redisUtil.setEx(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_TAG, JsonUtils.objectToJson(resultList), 2, TimeUnit.HOURS);
        }
        return resultList;
    }

    @Override
    public List<Map<String, Object>> getBlogCountByBlogSort() {
        // 从Redis中获取博客分类下包含的博客数量
        String jsonArrayList = redisUtil.get(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_SORT);
        if (StringUtils.isNotEmpty(jsonArrayList)) {
            ArrayList jsonList = JsonUtils.jsonArrayToArrayList(jsonArrayList);
            return jsonList;
        }
        List<Map<String, Object>> blogCoutByBlogSortMap = blogMapper.getBlogCountByBlogSort();
        Map<String, Integer> blogSortMap = new HashMap<>();
        for (Map<String, Object> item : blogCoutByBlogSortMap) {

            String blogSortUid = String.valueOf(item.get(SQLConf.BLOG_SORT_UID));
            // java.lang.Number是Integer,Long的父类
            Number num = (Number) item.get(SysConf.COUNT);
            Integer count = 0;
            if (num != null) {
                count = num.intValue();
            }
            blogSortMap.put(blogSortUid, count);
        }

        //把查询到的BlogSort放到Map中
        Set<String> blogSortUids = blogSortMap.keySet();
        Collection<BlogSort> blogSortCollection = new ArrayList<>();

        if (blogSortUids.size() > 0) {
            blogSortCollection = blogSortMapper.selectBatchIds(blogSortUids);
        }

        Map<String, String> blogSortEntityMap = new HashMap<>();
        for (BlogSort blogSort : blogSortCollection) {
            if (StringUtils.isNotEmpty(blogSort.getSortName())) {
                blogSortEntityMap.put(blogSort.getUid(), blogSort.getSortName());
            }
        }

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, Integer> entry : blogSortMap.entrySet()) {

            String blogSortUid = entry.getKey();

            if (blogSortEntityMap.get(blogSortUid) != null) {
                String blogSortName = blogSortEntityMap.get(blogSortUid);
                Integer count = entry.getValue();
                Map<String, Object> itemResultMap = new HashMap<>();
                itemResultMap.put(SysConf.BLOG_SORT_UID, blogSortUid);
                itemResultMap.put(SysConf.NAME, blogSortName);
                itemResultMap.put(SysConf.VALUE, count);
                resultList.add(itemResultMap);
            }
        }
        // 将 每个分类下文章数目 存入到Redis【过期时间2小时】
        if (resultList.size() > 0) {
            redisUtil.setEx(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_SORT, JsonUtils.objectToJson(resultList), 2, TimeUnit.HOURS);
        }
        return resultList;
    }

    @Override
    public Map<String, Object> getBlogContributeCount() {
        // 从Redis中获取博客分类下包含的博客数量
        String jsonMap = redisUtil.get(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_CONTRIBUTE_COUNT);
        if (StringUtils.isNotEmpty(jsonMap)) {
            Map<String, Object> resultMap = JsonUtils.jsonToMap(jsonMap);
            return resultMap;
        }

        // 获取今天结束时间
        String endTime = DateUtils.getNowTime();
        // 获取365天前的日期
        Date temp = DateUtils.getDate(endTime, -365);
        String startTime = DateUtils.dateTimeToStr(temp);
        List<Map<String, Object>> blogContributeMap = blogMapper.getBlogContributeCount(startTime, endTime);
        List<String> dateList = DateUtils.getDayBetweenDates(startTime, endTime);
        Map<String, Object> dateMap = new HashMap<>();
        for (Map<String, Object> itemMap : blogContributeMap) {
            dateMap.put(itemMap.get("DATE").toString(), itemMap.get("COUNT"));
        }

        List<List<Object>> resultList = new ArrayList<>();
        for (String item : dateList) {
            Integer count = 0;
            if (dateMap.get(item) != null) {
                count = Integer.valueOf(dateMap.get(item).toString());
            }
            List<Object> objectList = new ArrayList<>();
            objectList.add(item);
            objectList.add(count);
            resultList.add(objectList);
        }

        Map<String, Object> resultMap = new HashMap<>(Constants.NUM_TWO);
        List<String> contributeDateList = new ArrayList<>();
        contributeDateList.add(startTime);
        contributeDateList.add(endTime);
        resultMap.put(SysConf.CONTRIBUTE_DATE, contributeDateList);
        resultMap.put(SysConf.BLOG_CONTRIBUTE_COUNT, resultList);
        // 将 全年博客贡献度 存入到Redis【过期时间2小时】
        redisUtil.setEx(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_CONTRIBUTE_COUNT, JsonUtils.objectToJson(resultMap), 2, TimeUnit.HOURS);
        return resultMap;
    }

    @Override
    public Blog getBlogByUid(String uid) {
        Blog blog = blogMapper.selectById(uid);
        if (blog != null && blog.getStatus() != EStatus.DISABLED) {
            blog = setTagByBlog(blog);
            blog = setSortByBlog(blog);
            return blog;
        }
        return null;
    }

    @Override
    public List<Blog> getSameBlogByBlogUid(String blogUid) {
        Blog blog = blogService.getById(blogUid);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Page<Blog> page = new Page<>();
        page.setCurrent(1);
        page.setSize(10);
        // 通过分类来获取相关博客
        String blogSortUid = blog.getBlogSortUid();
        queryWrapper.eq(SQLConf.BLOG_SORT_UID, blogSortUid);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = blogService.setTagAndSortByBlogList(list);

        //过滤掉当前的博客
        List<Blog> newList = new ArrayList<>();
        for (Blog item : list) {
            if (item.getUid().equals(blogUid)) {
                continue;
            }
            newList.add(item);
        }
        return newList;
    }

    @Override
    public List<Blog> getBlogListByTop(Integer top) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Page<Blog> page = new Page<>();
        page.setCurrent(1);
        page.setSize(top);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.orderByDesc(SQLConf.SORT);
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = blogService.setTagAndSortAndPictureByBlogList(list);
        return list;
    }

    @Override
    public IPage<Blog> getPageList(BlogVO blogVO) {
        //分页
        Page<Blog> page = new Page<>();
        page.setCurrent(blogVO.getCurrentPage());
        page.setSize(blogVO.getPageSize());

        if (StringUtils.isNotEmpty(blogVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(blogVO.getOrderByAscColumn())).toString();
            blogVO.setOrderBy(column + " asc");
        } else if (StringUtils.isNotEmpty(blogVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(blogVO.getOrderByDescColumn())).toString();
            blogVO.setOrderBy(column + " desc");
        } else {
            // 是否启动排序字段
            if (blogVO.getUseSort() == 0) {
                // 未使用，默认按时间倒序
                blogVO.setOrderBy(SQLConf.CREATE_TIME + " desc");
            } else {
                blogVO.setOrderBy(SQLConf.SORT + " desc");
            }
        }

        if (!Objects.equals(blogVO.getIsBlack(), Constants.NUM_ONE) || blogVO.getIsBlack() == null) {
            // 前台  查看当前用户 是否是特权用户
            double userTag = RequestHolder.getUserTag();

            // 如果是普通用户 只能查看普通文章 不能查看特权文章
            if (userTag <= EUserTag.NORMAL_USER.getValue().intValue()) {
                blogVO.setIsVip(String.valueOf(EStatus.DISABLED));
            }
        }

        Page<Blog> pageList = blogMapper.queryPage(page, blogVO);
        List<Blog> list = pageList.getRecords();

        if (list.size() == 0) {
            return pageList;
        }

        final StringBuffer fileUids = new StringBuffer();
        List<String> sortUids = new ArrayList<>();
        List<String> tagUids = new ArrayList<>();
        List<String> blogUids = new ArrayList<>();
        List<String> userUidList = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUids.append(item.getFileUid() + SysConf.FILE_SEGMENTATION);
            }
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                sortUids.add(item.getBlogSortUid());
            }
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(), SysConf.FILE_SEGMENTATION);
                for (String itemTagUid : tagUidsTemp) {
                    tagUids.add(itemTagUid);
                }
            }
            userUidList.add(item.getUserUid());
            blogUids.add(item.getUid());
        });

        String pictureList = null;
        if (fileUids != null) {
            pictureList = this.pictureFeignClient.getPicture(fileUids.toString(), SysConf.FILE_SEGMENTATION);
        }

        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureList);
        Collection<BlogSort> sortList = new ArrayList<>();
        Collection<Tag> tagList = new ArrayList<>();

        if (sortUids.size() > 0) {
            sortList = blogSortService.listByIds(sortUids);
        }
        if (tagUids.size() > 0) {
            tagList = tagService.listByIds(tagUids);
        }

        // 获取文章专题信息
        Map<String, List<Subject>> subjectItemMap = subjectService.getSubjectByBizList(blogUids);

        Map<String, BlogSort> sortMap = new HashMap<>();
        Map<String, Tag> tagMap = new HashMap<>();
        Map<String, String> pictureMap = new HashMap<>();
        Map<String, User> userMap = userService.getUserAvatarMapByIds(userUidList);

        sortList.forEach(item -> {
            sortMap.put(item.getUid(), item);
        });

        tagList.forEach(item -> {
            tagMap.put(item.getUid(), item);
        });

        picList.forEach(item -> {
            pictureMap.put(item.get(SQLConf.UID).toString(), item.get(SQLConf.URL).toString());
        });

        for (Blog item : list) {
            //设置分类
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                item.setBlogSort(sortMap.get(item.getBlogSortUid()));
            }
            //获取标签
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(), SysConf.FILE_SEGMENTATION);
                List<Tag> tagListTemp = new ArrayList<Tag>();

                tagUidsTemp.forEach(tag -> {
                    tagListTemp.add(tagMap.get(tag));
                });
                item.setTagList(tagListTemp);
            }
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileUid(), SysConf.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();

                pictureUidsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
            }
            item.setUser(userMap.get(item.getUserUid()));
            item.setSubjectList(subjectItemMap.get(item.getUid()));
            // 反序列化
            if (StringUtils.isNotEmpty(item.getVisitAuthExtra())) {
                VisitAuthExtra visitAuthExtra = JsonUtils.jsonToPojo(item.getVisitAuthExtra(), VisitAuthExtra.class);
                item.setVisitAuthExtraVo(visitAuthExtra);
            } else {
                item.setVisitAuthExtraVo(new VisitAuthExtra());
            }
        }
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String editBatch(List<BlogVO> blogVOList) {
        if (blogVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> blogUidList = new ArrayList<>();
        Map<String, BlogVO> blogVOMap = new HashMap<>();
        blogVOList.forEach(item -> {
            blogUidList.add(item.getUid());
            blogVOMap.put(item.getUid(), item);
        });

        Collection<Blog> blogList = blogService.listByIds(blogUidList);
        blogList.forEach(blog -> {
            BlogVO blogVO = blogVOMap.get(blog.getUid());
            if (blogVO != null) {
                blog.setAuthor(blogVO.getAuthor());
                blog.setArticlesPart(blogVO.getArticlesPart());
                blog.setTitle(blogVO.getTitle());
                blog.setSummary(blogVO.getSummary());
                blog.setContent(blogVO.getContent());
                blog.setTagUid(blogVO.getTagUid());
                blog.setBlogSortUid(blogVO.getBlogSortUid());
                blog.setFileUid(blogVO.getFileUid());
                blog.setLevel(blogVO.getLevel());
                blog.setIsOriginal(blogVO.getIsOriginal());
                blog.setIsPublish(blogVO.getIsPublish());
                blog.setSort(blogVO.getSort());
                blog.setType(blogVO.getType());
                blog.setOutsideLink(blogVO.getOutsideLink());
                blog.setStatus(EStatus.ENABLE);
            }
        });
        Boolean save = blogService.updateBatchById(blogList);
        //保存成功后，需要发送消息到solr 和 redis
        if (save) {
            Map<String, Object> map = new HashMap<>();
            map.put(SysConf.COMMAND, SysConf.EDIT_BATCH);
            //发送到RabbitMq
            rabbitTemplate.convertAndSend(SysConf.EXCHANGE_DIRECT, SysConf.MOGU_BLOG, map);
        }

        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchBlog(List<BlogVO> blogVoList) {
        if (blogVoList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uidList = new ArrayList<>();
        StringBuffer uidSbf = new StringBuffer();
        blogVoList.forEach(item -> {
            uidList.add(item.getUid());
            uidSbf.append(item.getUid() + SysConf.FILE_SEGMENTATION);
        });
        Collection<Blog> blogList = blogService.listByIds(uidList);

        blogList.forEach(item -> {
            item.setStatus(EStatus.DISABLED);
        });

        Boolean save = blogService.updateBatchById(blogList);
        // 批量发送删除事件
        if (save) {
            for (Blog blog : blogList) {
                // 发布删除事件
                context.publishEvent(new BlogDeleteEvent(blog, true));
            }
        }


        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String batchAuditBlog(List<BlogVO> blogVoList) {
        if (blogVoList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uidList = new ArrayList<>();
        String auditStatus = EAuditStatus.WAIT;
        String rejectReason = "";
        for (BlogVO item : blogVoList) {
            auditStatus = item.getAuditStatus();
            rejectReason = item.getRejectReason();
            uidList.add(item.getUid());
        }

        Collection<Blog> blogList = blogService.listByIds(uidList);
        Admin admin = adminService.getById(RequestHolder.getAdminUid());
        String finalAuditStatus = auditStatus;
        String finalRejectReason = rejectReason;
        blogList.forEach(item -> {
            item.setAuditName(admin.getNickName());
            item.setAuditTime(new Date());
            if (EAuditStatus.AGREE.equals(item.getAuditStatus())) {
                throw new UpdateException("审批失败，存在已完成审批的博客");
            }
            if (EAuditStatus.AGREE.equals(finalAuditStatus)) {
                // 审批通过，自动上架
                item.setAuditStatus(EAuditStatus.AGREE);
                item.setIsPublish(EPublish.PUBLISH);
                // 增加用户积分
                userService.addUserCredits(ECreditType.Blog.getAction(), null, item.getUid(), item.getUserUid());
            } else if (EAuditStatus.REJECT.equals(finalAuditStatus)) {
                // 审批通过，自动下架
                item.setIsPublish(EPublish.NO_PUBLISH);
                item.setAuditStatus(EAuditStatus.REJECT);
                item.setRejectReason(finalRejectReason);
            } else {
                throw new UpdateException("审批失败");
            }
        });
        /**
         * 获取审核博客列表中
         */
        Set<String> userUids = blogList.stream().map(blog -> blog.getUserUid()).collect(Collectors.toSet());
        /**
         * 查询这些博客博主中开启了通知且邮箱不为空的博主
         */
        List<User> userList = userService.list(
                new LambdaQueryWrapper<User>()
                        .in(User::getUid, userUids)
                        .eq(User::getStartEmailNotification, 1)
                        .isNotNull(User::getEmail)
                        .apply(" email !='' ")
        );
        if (!userList.isEmpty()) {

            userList.forEach(user -> {
                AtomicReference<Integer> index = new AtomicReference<>(0);
                List<Blog> ownBlogList = blogList.stream().filter(blog -> {
                    return blog.getUserUid().equals(user.getUid());
                }).collect(Collectors.toList());
                StringBuilder text = new StringBuilder();
                text.append(String.format("<p>可爱的博主%s,您的博客稿件</p>", user.getNickName()));
                ownBlogList.forEach(blog -> {
                    index.getAndSet(index.get() + 1);
                    String message = "";
                    if (EAuditStatus.REJECT.equals(blog.getAuditStatus())) {
                        message = "审核未通过,审核原因为" + blog.getRejectReason();
                    }
                    if (EAuditStatus.AGREE.equals(blog.getAuditStatus())) {
                        message = "审核已通过";
                    }
                    text.append(String.format("\n<p>%s.%s%s</p>", index.get(), blog.getTitle(), message));
                });
                rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
            });
        }
        blogService.updateBatchById(blogList);
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public String auditBlog(BlogVO blogVO) {
        Blog blog = getById(blogVO.getUid());
        if (blog == null) {
            throw new QueryException("未查询到该文章");
        }
        Admin admin = adminService.getById(RequestHolder.getAdminUid());
        blog.setAuditStatus(blogVO.getAuditStatus());
        blog.setRejectReason(blogVO.getRejectReason());
        blog.setAuditName(admin.getNickName());
        blog.setAuditTime(new Date());
        blogService.updateById(blog);
        context.publishEvent(new BlogAuditEvent(blogService.setBlogInfo(blog), true));
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public String uploadLocalBlog(List<MultipartFile> filedatas) {
        // 获取当前管理员
        Admin admin = adminService.getMe();

        // 判断是否附身用户
        if (StringUtils.isEmpty(admin.getUserUid())) {
            return ResultUtil.errorWithMessage("您暂未附身门户用户，无法发表文章，请到 管理员管理-> 编辑, 选择要附身用户 再重新发表");
        }
        SystemConfig systemConfig = systemConfigService.getConfig();
        if (systemConfig == null) {
            return ResultUtil.errorWithMessage(MessageConf.SYSTEM_CONFIG_NOT_EXIST);
        } else {
            if (EOpenStatus.OPEN.equals(systemConfig.getUploadQiNiu()) && (StringUtils.isEmpty(systemConfig.getQiNiuPictureBaseUrl()) || StringUtils.isEmpty(systemConfig.getQiNiuAccessKey())
                    || StringUtils.isEmpty(systemConfig.getQiNiuSecretKey()) || StringUtils.isEmpty(systemConfig.getQiNiuBucket()) || StringUtils.isEmpty(systemConfig.getQiNiuArea()))) {
                return ResultUtil.errorWithMessage(MessageConf.PLEASE_SET_QI_NIU);
            }

            if (EOpenStatus.OPEN.equals(systemConfig.getUploadLocal()) && StringUtils.isEmpty(systemConfig.getLocalPictureBaseUrl())) {
                return ResultUtil.errorWithMessage(MessageConf.PLEASE_SET_LOCAL);
            }
        }

        List<MultipartFile> fileList = new ArrayList<>();
        List<String> fileNameList = new ArrayList<>();
        for (MultipartFile file : filedatas) {
            String fileOriginalName = file.getOriginalFilename();
            if (FileUtils.isMarkdown(fileOriginalName)) {
                fileList.add(file);
                // 获取文件名
                fileNameList.add(FileUtils.getFileName(fileOriginalName));
            } else {
                return ResultUtil.errorWithMessage("目前仅支持Markdown文件");
            }
        }

        if (fileList.size() == 0) {
            return ResultUtil.errorWithMessage("请选中需要上传的文件");
        }

        // 文档解析
        List<String> fileContentList = new ArrayList<>();
        for (MultipartFile multipartFile : fileList) {
            try {
                Reader reader = new InputStreamReader(multipartFile.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(reader);
                String line;
                StringBuilder content = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    content.append(line).append("\n");
                }
                // 将Markdown转换成html
                String blogContent = FileUtils.markdownToHtml(content.toString());
                fileContentList.add(blogContent);
            } catch (Exception e) {
                log.error("文件解析出错");
                log.error(e.getMessage());
            }
        }

        HttpServletRequest request = RequestHolder.getRequest();
        String pictureList = request.getParameter(SysConf.PICTURE_LIST);
        List<LinkedTreeMap<String, String>> list = (List<LinkedTreeMap<String, String>>) JsonUtils.jsonArrayToArrayList(pictureList);
        Map<String, String> pictureMap = new HashMap<>();
        for (LinkedTreeMap<String, String> item : list) {

            if (EFilePriority.QI_NIU.equals(systemConfig.getContentPicturePriority())) {
                // 获取七牛云上的图片
                pictureMap.put(item.get(SysConf.FILE_OLD_NAME), item.get(SysConf.QI_NIU_URL));
            } else if (EFilePriority.LOCAL.equals(systemConfig.getContentPicturePriority())) {
                // 获取本地的图片
                pictureMap.put(item.get(SysConf.FILE_OLD_NAME), item.get(SysConf.PIC_URL));
            } else if (EFilePriority.MINIO.equals(systemConfig.getContentPicturePriority())) {
                // 获取MINIO的图片
                pictureMap.put(item.get(SysConf.FILE_OLD_NAME), item.get(SysConf.MINIO_URL));
            }
        }
        // 需要替换的图片Map
        Map<String, String> matchUrlMap = new HashMap<>();
        for (String blogContent : fileContentList) {
            List<String> matchList = RegexUtils.match(blogContent, "<img\\s+(?:[^>]*)src\\s*=\\s*([^>]+)>");
            for (String matchStr : matchList) {
                String[] splitList = matchStr.split("\"");
                // 取出中间的图片
                if (splitList.length >= 5) {
                    // alt 和 src的先后顺序
                    // 得到具体的图片路径
                    String pictureUrl = "";
                    if (matchStr.indexOf("alt") > matchStr.indexOf("src")) {
                        pictureUrl = splitList[1];
                    } else {
                        pictureUrl = splitList[3];
                    }

                    // 判断是网络图片还是本地图片
                    if (!pictureUrl.startsWith(SysConf.HTTP)) {
                        // 那么就需要遍历全部的map和他匹配
                        for (Map.Entry<String, String> map : pictureMap.entrySet()) {
                            // 查看Map中的图片是否在需要替换的key中
                            if (pictureUrl.indexOf(map.getKey()) > -1) {
                                if (EFilePriority.QI_NIU.equals(systemConfig.getContentPicturePriority())) {
                                    // 获取七牛云上的图片
                                    matchUrlMap.put(pictureUrl, systemConfig.getQiNiuPictureBaseUrl() + map.getValue());
                                } else if (EFilePriority.LOCAL.equals(systemConfig.getContentPicturePriority())) {
                                    // 获取本地的图片
                                    matchUrlMap.put(pictureUrl, systemConfig.getLocalPictureBaseUrl() + map.getValue());
                                } else if (EFilePriority.MINIO.equals(systemConfig.getContentPicturePriority())) {
                                    // 获取MINIO的图片
                                    matchUrlMap.put(pictureUrl, systemConfig.getMinioPictureBaseUrl() + map.getValue());
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        // 获取一个排序最高的博客分类和标签
        BlogSort blogSort = blogSortService.getTopOne();
        Tag tag = tagService.getTopTag();

        // 获取任意博客封面
        Picture picture = pictureService.getTopOne();
        if (blogSort == null || tag == null || picture == null) {
            return ResultUtil.errorWithMessage("使用本地上传，请先确保博客分类，博客标签，博客图片中含有数据");
        }

        // 存储需要上传的博客
        List<Blog> blogList = new ArrayList<>();
        // 开始进行图片替换操作
        Integer count = 0;
        String projectName = sysParamsService.getSysParamsValueByKey(SysConf.PROJECT_NAME_);


        // 判断该管理员是否进行了权限附身
        Boolean isUserOp = false;
        User user = null;
        if (StringUtils.isNotEmpty(admin.getUserUid())) {
            user = userService.getById(admin.getUserUid());
            if (user != null) {
                isUserOp = true;
            }
        }

        for (String content : fileContentList) {
            // 循环替换里面的图片
            for (Map.Entry<String, String> map : matchUrlMap.entrySet()) {
                content = content.replace(map.getKey(), map.getValue());
            }
            Blog blog = new Blog();
            blog.setCharCount(content.length());
            blog.setBlogSortUid(blogSort.getUid());
            blog.setTagUid(tag.getUid());
            blog.setAdminUid(admin.getUid());
            blog.setArticlesPart(projectName);
            blog.setLevel(ELevel.NORMAL);
            blog.setTitle(fileNameList.get(count));
            blog.setCharCount(content.length());

            if (user != null) {
                blog.setAuthor(user.getNickName());
                blog.setUserUid(user.getUid());
                // 被权限附身的管理员，改成以用户投稿的方式上传文章
                blog.setArticleSource(EContributeSource.USER_PUBLISH);
                isUserOp = true;
            } else {
                blog.setArticleSource(EContributeSource.ADMIN_PUBLISH);
            }
            if (!isUserOp) {
                if (StringUtils.isNotEmpty(admin.getNickName())) {
                    blog.setAuthor(admin.getNickName());
                } else {
                    blog.setAuthor(admin.getUserName());
                }
            }
            // 后端上架，自动审批完成
            blog.setAuditStatus(EAuditStatus.AGREE);
            // 默认开启加载校验
            blog.setOpenLoadingValid(EOpenStatus.OPEN);
            blog.setVisitAuth(EVisitAuthType.PUBLIC.getType());

            // 设置简介
            String summary = StringUtils.dealContent(content);
            if (summary.length() < 190) {
                blog.setSummary(summary);
            } else {
                blog.setSummary(summary.substring(0, 190) + "...");
            }
            blog.setContent(content);
            blog.setFileUid(picture.getFileUid());
            blog.setIsOriginal(EOriginal.ORIGINAL);
            blog.setIsPublish(EPublish.NO_PUBLISH);
            blog.setOpenComment(EOpenStatus.OPEN);
            blog.setType(Constants.STR_ZERO);
            blogList.add(blog);
            count++;

        }
        // 批量添加博客
        blogService.saveBatch(blogList);

        for (Blog blog : blogList) {
            /**
             * 发布新增事件
             */
            context.publishEvent(new BlogAddEvent(blog, true));
        }

        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public void deleteRedisByBlogSort() {
        // 删除Redis中博客分类下的博客数量
        redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_SORT);
        // 删除博客相关缓存
        deleteRedisByBlog();
    }

    @Override
    public void deleteRedisByBlogTag() {
        // 删除Redis中博客分类下的博客数量
        redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_TAG);
        // 删除博客相关缓存
        deleteRedisByBlog();
    }

    @Override
    public void deleteRedisByBlog() {
        // 删除博客相关缓存
        redisUtil.delete(RedisConf.NEW_BLOG);
        redisUtil.delete(RedisConf.HOT_BLOG);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + ELevel.FIRST);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + ELevel.SECOND);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + ELevel.THIRD);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + ELevel.FOURTH);
    }

    @Override
    public IPage<Blog> getBlogPageByLevel(Integer level, Long currentPage, Integer useSort) {

        //从Redis中获取内容
        String jsonResult = redisUtil.get(RedisConf.BLOG_LEVEL + RedisConf.SEGMENTATION + level);

        //判断redis中是否有文章
        if (StringUtils.isNotEmpty(jsonResult)) {
            List jsonResult2List = JsonUtils.jsonArrayToArrayList(jsonResult);
            IPage pageList = new Page();
            pageList.setRecords(jsonResult2List);
            return pageList;
        }
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        String blogCount = null;
        switch (level) {
            case ELevel.NORMAL: {
                blogCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_NEW_COUNT);
            }
            break;
            case ELevel.FIRST: {
                blogCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_FIRST_COUNT);
            }
            break;
            case ELevel.SECOND: {
                blogCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_SECOND_COUNT);
            }
            break;
            case ELevel.THIRD: {
                blogCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_THIRD_COUNT);
            }
            break;
            case ELevel.FOURTH: {
                blogCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_FOURTH_COUNT);
            }
            break;
        }
        if (StringUtils.isEmpty(blogCount)) {
            log.error(MessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS);
        } else {
            page.setSize(Long.valueOf(blogCount));
        }

        IPage<Blog> pageList = blogService.getBlogPageByLevel(page, level, useSort);
        List<Blog> list = pageList.getRecords();

        // 一级推荐或者二级推荐没有内容时，自动把top5填充至一级推荐和二级推荐中 【暂时关闭自动填充】
//        if ((level == SysConf.ONE || level == SysConf.TWO) && list.size() == 0) {
//            QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
//            Page<Blog> hotPage = new Page<>();
//            hotPage.setCurrent(1);
//            String blogHotCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_HOT_COUNT);
//            String blogSecondCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_SECOND_COUNT);
//            if (StringUtils.isEmpty(blogHotCount) || StringUtils.isEmpty(blogSecondCount)) {
//                log.error(MessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS);
//            } else {
//                hotPage.setSize(Long.valueOf(blogHotCount));
//            }
//            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
//            queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
//            queryWrapper.orderByDesc(SQLConf.CLICK_COUNT);
//            queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
//            IPage<Blog> hotPageList = blogService.page(hotPage, queryWrapper);
//            List<Blog> hotBlogList = hotPageList.getRecords();
//            List<Blog> secondBlogList = new ArrayList<>();
//            List<Blog> firstBlogList = new ArrayList<>();
//            for (int a = 0; a < hotBlogList.size(); a++) {
//                // 当推荐大于两个的时候
//                if ((hotBlogList.size() - firstBlogList.size()) > Long.valueOf(blogSecondCount)) {
//                    firstBlogList.add(hotBlogList.get(a));
//                } else {
//                    secondBlogList.add(hotBlogList.get(a));
//                }
//            }
//
//            firstBlogList = setBlog(firstBlogList);
//            secondBlogList = setBlog(secondBlogList);
//
//            // 将从数据库查询的数据缓存到redis中，设置1小时后过期 [避免 list 中没有数据而保存至 redis 的情况]
//            if (firstBlogList.size() > 0) {
//                redisUtil.setEx(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_ONE, JsonUtils.objectToJson(firstBlogList), 1, TimeUnit.HOURS);
//            }
//            if (secondBlogList.size() > 0) {
//                redisUtil.setEx(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_TWO, JsonUtils.objectToJson(secondBlogList), 1, TimeUnit.HOURS);
//            }
//
//            switch (level) {
//                case SysConf.ONE: {
//                    pageList.setRecords(firstBlogList);
//                }
//                break;
//                case SysConf.TWO: {
//                    pageList.setRecords(secondBlogList);
//                }
//                break;
//            }
//            return pageList;
//        }


        list = setBlog(list);
        pageList.setRecords(list);

        // 将从数据库查询的数据缓存到redis中 [避免 list 中没有数据而保存至 redis 的情况]
        if (list.size() > 0) {
            redisUtil.setEx(SysConf.BLOG_LEVEL + SysConf.REDIS_SEGMENTATION + level, JsonUtils.objectToJson(list).toString(), 1, TimeUnit.HOURS);
        }
        return pageList;
    }

    @Override
    public IPage<Blog> getHotBlog() {
        // 前台  查看当前用户 是否是特权用户
        double userTag = RequestHolder.getUserTag();

        //从Redis中获取内容
        String jsonResult = redisUtil.get(RedisConf.HOT_BLOG);
        //判断redis中是否有文章
        if (StringUtils.isNotEmpty(jsonResult)) {
            List jsonResult2List = JsonUtils.jsonArrayToArrayList(jsonResult);
            IPage pageList = new Page();
            pageList.setRecords(jsonResult2List);
            return pageList;
        }
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(0);
        String blogHotCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_HOT_COUNT);
        if (StringUtils.isEmpty(blogHotCount)) {
            log.error(MessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS);
        } else {
            page.setSize(Long.valueOf(blogHotCount));
        }
        // 如果是普通用户 只能查看普通文章 不能查看特权文章
        if (userTag <= EUserTag.NORMAL_USER.getValue().intValue()) {
            queryWrapper.eq(SQLConf.IS_VIP, EStatus.DISABLED);
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SQLConf.CLICK_COUNT);
        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = setBlog(list);
        pageList.setRecords(list);
        // 将从数据库查询的数据缓存到redis中[避免list中没有数据而保存至redis的情况]
        if (list.size() > 0) {
            redisUtil.setEx(RedisConf.HOT_BLOG, JsonUtils.objectToJson(list), 1, TimeUnit.HOURS);
        }
        return pageList;
    }

    @Override
    public IPage<Blog> getNewBlog(BlogVO blogVO) {
        String userUid = RequestHolder.getUserUid();
        String blogNewCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_NEW_COUNT);
        if (StringUtils.isEmpty(blogNewCount)) {
            log.error(MessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS);
        }


//        // 判断Redis中是否缓存了第一页的内容
//        if (currentPage == 1L) {
//            //从Redis中获取内容
//            String jsonResult = redisUtil.get(RedisConf.NEW_BLOG);
//            //判断redis中是否有文章
//            if (StringUtils.isNotEmpty(jsonResult)) {
//                IPage pageList = JsonUtils.jsonToPojo(jsonResult, Page.class);
//                return pageList;
//            }
//        }

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(blogVO.getCurrentPage());
        page.setSize(Long.valueOf(blogNewCount));

        // 判断是否仅专栏显示
        if (blogVO.getIsOnlySubjectShow() != null) {
            queryWrapper.eq(SQLConf.IS_ONLY_SUBJECT_SHOW, blogVO.getIsOnlySubjectShow());
        }

        // 判断是否按分类查找
        if (StringUtils.isNotEmpty(blogVO.getBlogSortUid())) {
            queryWrapper.eq(SQLConf.BLOG_SORT_UID, blogVO.getBlogSortUid());
        }

//        // 前台  查看当前用户 是否是特权用户
//        double userTag = RequestHolder.getUserTag();
//        // 如果是普通用户 只能查看普通文章 不能查看特权文章
//        if (userTag <= EUserTag.NORMAL_USER.getValue().intValue()) {
//            queryWrapper.eq(SQLConf.IS_VIP, EStatus.DISABLED);
//        }

        // 判断是否只查看关注用户的文章
        if (StringUtils.isNotEmpty(blogVO.getOrderBy()) && SysConf.USER_WATCH.equals(blogVO.getOrderBy())) {
            // 获取用户是否登录
            if (StringUtils.isNotEmpty(userUid)) {
                // 获取用户关注的用户
                List<UserWatch> userWatchList = userWatchService.list(new LambdaQueryWrapper<UserWatch>()
                        .eq(UserWatch::getUserUid, userUid)
                        .eq(UserWatch::getStatus, EStatus.ENABLE));
                List<String> toUserUidList = userWatchList.stream().map(UserWatch::getToUserUid).collect(Collectors.toList());
                if (toUserUidList.size() > 0) {
                    queryWrapper.in(SQLConf.USER_UID, toUserUidList);
                } else {
                    IPage<Blog> emptyPage = new Page<>();
                    return emptyPage;
                }
            }
        }

        //因为首页并不需要显示内容，所以需要排除掉内容字段
//        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        // 判断是否需要排序
        if (StringUtils.isNotEmpty(blogVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(blogVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(blogVO.getOrderByDescColumn())) {
            if (SQLConf.RANDOM.equals(blogVO.getOrderByDescColumn())) {
                queryWrapper.last(" order by RAND() desc");
            } else {
                // 将驼峰转换成下划线
                String column = StringUtils.underLine(new StringBuffer(blogVO.getOrderByDescColumn())).toString();
                queryWrapper.orderByDesc(column);
            }
        } else {
            // 没有传递任何参数，走默认推荐排序逻辑
            // "(5 * click_count + comment_count * 200 + praise_count * 100 - tread_count * 200 + 1 ) / POWER((DATEDIFF(NOW(), create_time) + 2), 1.8) AS score"
            String recommendSQL = sysParamsService.getSysParamsValueByKey(SysConf.SYS_RECOURCE_RECOMMEND);
            queryWrapper.select("*", recommendSQL);
            queryWrapper.orderByDesc(SQLConf.IS_TOP);
            queryWrapper.orderByDesc(SQLConf.SCORE);
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }

        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);

        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();

        if (list.size() == 0) {
            return pageList;
        }

        list = setBlog(list);

        for (Blog blog : list) {
            // 设置点赞相关信息
            UserPraiseRecordVO userPraiseRecordVO = new UserPraiseRecordVO();
            userPraiseRecordVO.setResourceUid(blog.getUid());
            userPraiseRecordVO.setResourceType(EResourceType.BLOG.getType());
            Map<String, Object> praiseMap = userPraiseRecordService.praisedCount(userPraiseRecordVO);
            blog.setPraiseInfo(praiseMap);

            // 设置收藏相关信息
            CollectVO collectVO = new CollectVO();
            collectVO.setBizUid(blog.getUid());
            collectVO.setCollectType(EResourceType.BLOG.getType());
            Map<String, Object> collectInfo = collectService.getCollectCount(collectVO);
            blog.setCollectInfo(collectInfo);
            blog.setContent("");
        }

        pageList.setRecords(list);

        //将从最新博客缓存到redis中
//        if (currentPage == 1L) {
//            redisUtil.setEx(RedisConf.NEW_BLOG, JsonUtils.objectToJson(pageList), 1, TimeUnit.HOURS);
//        }
        return pageList;
    }

    @Override
    public IPage<Blog> getBlogBySearch(Long currentPage, Long pageSize) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        String blogNewCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_NEW_COUNT);
        if (StringUtils.isEmpty(blogNewCount)) {
            log.error(MessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS);
        } else {
            page.setSize(Long.valueOf(blogNewCount));
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        if (list.size() <= 0) {
            return pageList;
        }
        list = setBlog(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public IPage<Blog> getBlogByTime(Long currentPage, Long pageSize) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = setBlog(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public Integer getBlogPraiseCountByUid(String uid) {
        Integer pariseCount = 0;
        if (StringUtils.isEmpty(uid)) {
            log.error("传入的UID为空");
            return pariseCount;
        }
        //从Redis取出用户点赞数据
        String pariseJsonResult = redisUtil.get(RedisConf.BLOG_PRAISE + RedisConf.SEGMENTATION + uid);
        if (!StringUtils.isEmpty(pariseJsonResult)) {
            pariseCount = Integer.parseInt(pariseJsonResult);
        }
        return pariseCount;
    }

    @Override
    public String praiseBlogByUid(String uid) {
        if (StringUtils.isEmpty(uid)) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }

        HttpServletRequest request = RequestHolder.getRequest();
        // 如果用户登录了
        if (request.getAttribute(SysConf.USER_UID) != null) {
            String userUid = request.getAttribute(SysConf.USER_UID).toString();
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConf.USER_UID, userUid);
            queryWrapper.eq(SQLConf.BLOG_UID, uid);
            queryWrapper.eq(SQLConf.TYPE, ECommentType.PRAISE);
            queryWrapper.last(SysConf.LIMIT_ONE);
            Comment praise = commentService.getOne(queryWrapper);
            if (praise != null) {
                return ResultUtil.errorWithMessage(MessageConf.YOU_HAVE_BEEN_PRISE);
            }
        } else {
            return ResultUtil.errorWithMessage(MessageConf.PLEASE_LOGIN_TO_PRISE);
        }
        Blog blog = blogService.getById(uid);
        String pariseJsonResult = redisUtil.get(RedisConf.BLOG_PRAISE + RedisConf.SEGMENTATION + uid);
        if (StringUtils.isEmpty(pariseJsonResult)) {
            //给该博客点赞数
            redisUtil.set(RedisConf.BLOG_PRAISE + RedisConf.SEGMENTATION + uid, "1");
            blog.setCollectCount(1);
            blog.updateById();

        } else {
            Integer count = blog.getCollectCount() + 1;
            //给该博客点赞 +1
            redisUtil.set(RedisConf.BLOG_PRAISE + RedisConf.SEGMENTATION + uid, count.toString());
            blog.setCollectCount(count);
            blog.updateById();
        }
        // 已登录用户，向评论表添加点赞数据
        if (request.getAttribute(SysConf.USER_UID) != null) {
            String userUid = request.getAttribute(SysConf.USER_UID).toString();
            Comment comment = new Comment();
            comment.setUserUid(userUid);
            comment.setBlogUid(uid);
            comment.setSource(ECommentSource.BLOG_INFO.getCode());
            comment.setType(ECommentType.PRAISE);
            comment.insert();

            // 当是门户投稿添加的，需要向号主发送评论通知
            if (EContributeSource.USER_PUBLISH.equals(blog.getArticleSource())) {
                Notice notice = new Notice();
                notice.setUserUid(blog.getUserUid());
                notice.setNoticeType(ENoticeType.PRAISE.getCode());
                notice.setBusinessType(EBusinessType.BLOG_PRAISE.getCode());
                notice.setBusinessUid(comment.getUid());
                notice.insert();

                // 同时向号主Redis发送通知
                String redisKey = RedisConf.USER_RECEIVE_PRAISE_COUNT + Constants.SYMBOL_COLON + blog.getUserUid();
                String count = redisUtil.get(redisKey);
                if (StringUtils.isNotEmpty(count)) {
                    redisUtil.incrBy(redisKey, Constants.NUM_ONE);
                } else {
                    redisUtil.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
                }
            }
        }
        return ResultUtil.successWithData(blog.getCollectCount());
    }

    @Override
    public IPage<Blog> getSameBlogByTagUid(String tagUid) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(1);
        page.setSize(10);
        queryWrapper.like(SQLConf.TAG_UID, tagUid);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = blogService.setTagAndSortByBlogList(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public IPage<Blog> getListByBlogSortUid(String blogSortUid, Long currentPage, Long pageSize) {
        //分页
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(SQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(SQLConf.BLOG_SORT_UID, blogSortUid);

        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        IPage<Blog> pageList = blogService.page(page, queryWrapper);

        //给博客增加标签和分类
        List<Blog> list = blogService.setTagAndSortAndPictureByBlogList(pageList.getRecords());
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public Map<String, Object> getBlogByKeyword(String keywords, Long currentPage, Long pageSize) {
        final String keyword = keywords.trim();
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.like(SQLConf.TITLE, keyword).or().like(SQLConf.SUMMARY, keyword).or().like(SQLConf.AUTHOR, keyword));
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        queryWrapper.orderByDesc(SQLConf.CLICK_COUNT);
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);

        IPage<Blog> iPage = blogService.page(page, queryWrapper);
        List<Blog> blogList = iPage.getRecords();
        List<String> blogSortUidList = new ArrayList<>();
        List<String> fileUidList = new ArrayList<>();
        blogList.forEach(item -> {
            // 获取图片uid
            blogSortUidList.add(item.getBlogSortUid());
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUidList.add(item.getFileUid());
            }
            // 给标题和简介设置高亮
            item.setTitle(StringUtils.getHitCode(item.getTitle(), keyword));
            item.setSummary(StringUtils.getHitCode(item.getSummary(), keyword));
            item.setAuthorName(StringUtils.getHitCode(item.getAuthor(), keyword));
        });

        Map<String, String> pictureMap = FileFeignUtil.fileUidToFileUrlMap(fileUidList);

        Collection<BlogSort> blogSortList = new ArrayList<>();
        if (blogSortUidList.size() > 0) {
            blogSortList = blogSortService.listByIds(blogSortUidList);
        }

        Map<String, String> blogSortMap = new HashMap<>();
        blogSortList.forEach(item -> {
            blogSortMap.put(item.getUid(), item.getSortName());
        });

        // 设置分类名 和 图片
        blogList.forEach(item -> {
            if (blogSortMap.get(item.getBlogSortUid()) != null) {
                item.setBlogSortName(blogSortMap.get(item.getBlogSortUid()));
            }

            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileUid(), SysConf.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();

                pictureUidsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                // 只设置一张标题图
                if (pictureListTemp.size() > 0) {
                    item.setPhotoUrl(pictureListTemp.get(0));
                } else {
                    item.setPhotoUrl("");
                }
            }
        });

        Map<String, Object> map = new HashMap<>();
        // 返回总记录数
        map.put(SysConf.TOTAL, iPage.getTotal());
        // 返回总页数
        map.put(SysConf.TOTAL_PAGE, iPage.getPages());
        // 返回当前页大小
        map.put(SysConf.PAGE_SIZE, pageSize);
        // 返回当前页
        map.put(SysConf.CURRENT_PAGE, iPage.getCurrent());
        // 返回数据
        map.put(SysConf.BLOG_LIST, blogList);
        return map;
    }

    @Override
    public IPage<Blog> searchBlogByTag(String tagUid, Long currentPage, Long pageSize) {
        Tag tag = tagService.getById(tagUid);
        if (tag != null) {
            HttpServletRequest request = RequestHolder.getRequest();
            String ip = IpUtils.getIpAddr(request);
            //从Redis取出数据，判断该用户24小时内，是否点击过该标签
            String jsonResult = redisUtil.get(RedisConf.TAG_CLICK + RedisConf.SEGMENTATION + ip + "#" + tagUid);
            if (StringUtils.isEmpty(jsonResult)) {
                //给标签点击数增加
                int clickCount = tag.getClickCount() + 1;
                tag.setClickCount(clickCount);
                tag.updateById();
                //将该用户点击记录存储到redis中, 24小时后过期
                redisUtil.setEx(RedisConf.TAG_CLICK + RedisConf.SEGMENTATION + ip + RedisConf.WELL_NUMBER + tagUid, clickCount + "",
                        24, TimeUnit.HOURS);
            }
        }
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);

        queryWrapper.like(SQLConf.TAG_UID, tagUid);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SysConf.CONTENT));
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = blogService.setTagAndSortAndPictureByBlogList(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public IPage<Blog> searchBlogByBlogSort(String blogSortUid, Long currentPage, Long pageSize) {
        BlogSort blogSort = blogSortService.getById(blogSortUid);
        if (blogSort != null) {
            HttpServletRequest request = RequestHolder.getRequest();
            String ip = IpUtils.getIpAddr(request);

            //从Redis取出数据，判断该用户24小时内，是否点击过该分类
            String jsonResult = redisUtil.get(RedisConf.TAG_CLICK + RedisConf.SEGMENTATION + ip + RedisConf.WELL_NUMBER + blogSortUid);
            if (StringUtils.isEmpty(jsonResult)) {
                //给标签点击数增加
                int clickCount = blogSort.getClickCount() + 1;
                blogSort.setClickCount(clickCount);
                blogSort.updateById();
                //将该用户点击记录存储到redis中, 24小时后过期
                redisUtil.setEx(RedisConf.TAG_CLICK + RedisConf.SEGMENTATION + ip + RedisConf.WELL_NUMBER + blogSortUid, clickCount + "",
                        24, TimeUnit.HOURS);
            }
        }

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        queryWrapper.eq(SQLConf.BLOG_SORT_UID, blogSortUid);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        // 排除博客详情
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SysConf.CONTENT));
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = blogService.setTagAndSortAndPictureByBlogList(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public IPage<Blog> searchBlogByAuthor(String author, Long currentPage, Long pageSize) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        queryWrapper.eq(SQLConf.AUTHOR, author);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SysConf.CONTENT));
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = blogService.setTagAndSortAndPictureByBlogList(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String getBlogTimeSortList() {
        //从Redis中获取内容
        String monthResult = redisUtil.get(SysConf.MONTH_SET);
        //判断redis中时候包含归档的内容
        if (StringUtils.isNotEmpty(monthResult)) {
            List list = JsonUtils.jsonArrayToArrayList(monthResult);
            return ResultUtil.successWithData(list);
        }
        // 第一次启动的时候归档
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        List<Blog> list = blogService.list(queryWrapper);

        //给博客增加标签、分类、图片
        list = blogService.setTagAndSortAndPictureByBlogList(list);

        Map<String, List<Blog>> map = new HashMap<>();
        Iterator iterable = list.iterator();
        Set<String> monthSet = new TreeSet<>();
        while (iterable.hasNext()) {
            Blog blog = (Blog) iterable.next();
            Date createTime = blog.getCreateTime();

            if (createTime == null) {
                continue;
            }
            String month = new SimpleDateFormat("yyyy年MM月").format(createTime);
            monthSet.add(month);
            if (map.get(month) == null) {
                List<Blog> blogList = new ArrayList<>();
                blogList.add(blog);
                map.put(month, blogList);
            } else {
                List<Blog> blogList = map.get(month);
                blogList.add(blog);
                map.put(month, blogList);
            }
        }

        // 缓存该月份下的所有文章  key: 月份   value：月份下的所有文章
        map.forEach((key, value) -> {
            redisUtil.set(SysConf.BLOG_SORT_BY_MONTH + SysConf.REDIS_SEGMENTATION + key, JsonUtils.objectToJson(value));
        });

        //将从数据库查询的数据缓存到redis中
        redisUtil.set(SysConf.MONTH_SET, JsonUtils.objectToJson(monthSet));
        return ResultUtil.successWithData(monthSet);
    }

    @Override
    public String getArticleByMonth(String monthDate) {
        if (StringUtils.isEmpty(monthDate)) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        //从Redis中获取内容
        String contentResult = redisUtil.get(SysConf.BLOG_SORT_BY_MONTH + SysConf.REDIS_SEGMENTATION + monthDate);

        //判断redis中时候包含该日期下的文章
        if (StringUtils.isNotEmpty(contentResult)) {
            List list = JsonUtils.jsonArrayToArrayList(contentResult);
            return ResultUtil.successWithData(list);
        }

        // 第一次启动的时候归档
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        List<Blog> list = blogService.list(queryWrapper);

        //给博客增加标签、分类、图片
        list = blogService.setTagAndSortAndPictureByBlogList(list);

        Map<String, List<Blog>> map = new HashMap<>();
        Iterator iterable = list.iterator();
        Set<String> monthSet = new TreeSet<>();
        while (iterable.hasNext()) {
            Blog blog = (Blog) iterable.next();
            Date createTime = blog.getCreateTime();

            String month = new SimpleDateFormat("yyyy年MM月").format(createTime).toString();

            monthSet.add(month);

            if (map.get(month) == null) {
                List<Blog> blogList = new ArrayList<>();
                blogList.add(blog);
                map.put(month, blogList);
            } else {
                List<Blog> blogList = map.get(month);
                blogList.add(blog);
                map.put(month, blogList);
            }
        }

        // 缓存该月份下的所有文章  key: 月份   value：月份下的所有文章
        map.forEach((key, value) -> {
            redisUtil.set(SysConf.BLOG_SORT_BY_MONTH + SysConf.REDIS_SEGMENTATION + key, JsonUtils.objectToJson(value).toString());
        });
        //将从数据库查询的数据缓存到redis中
        redisUtil.set(SysConf.MONTH_SET, JsonUtils.objectToJson(monthSet));
        return ResultUtil.successWithData(map.get(monthDate));
    }

    @Override
    public String flushBlogSummary() {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<Blog> blogList = blogService.list(queryWrapper);
        blogList.forEach(item -> {
            // 设置简介
            String summary = StringUtils.dealContent(item.getContent());
            if (summary.length() < 190) {
                item.setSummary(summary);
            } else {
                item.setSummary(summary.substring(0, 190) + "...");
            }
        });
        Boolean isSave = blogService.saveOrUpdateBatch(blogList);
        if (isSave) {
            return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.UPDATE_FAIL);
        }
    }

    @Override
    public Blog randomBlog() {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Integer blogCount = blogService.count(queryWrapper);
        Integer currentPage = RandomUtil.randomInt(blogCount);
        Page<Blog> page = new Page<>();
        page.setSize(1);
        page.setCurrent(currentPage);
        IPage<Blog> problemIPage = blogService.page(page, queryWrapper);
        if (problemIPage.getRecords().size() > 0) {
            // 返回第一个元素
            Blog blog = problemIPage.getRecords().get(0);
            blog.setContent("");
            return blog;
        }
        return null;
    }

    /**
     * 添加时校验
     *
     * @param count
     * @param level
     * @return
     */
    private String addVerdict(Integer count, Integer level) {

        //添加的时候进行判断
        switch (level) {
            case ELevel.FIRST: {
                Long blogFirstCount = Long.valueOf(sysParamsService.getSysParamsValueByKey(SysConf.BLOG_FIRST_COUNT));
                if (count > blogFirstCount) {
                    return ResultUtil.errorWithMessage("一级推荐不能超过" + blogFirstCount + "个");
                }
            }
            break;

            case ELevel.SECOND: {
                Long blogSecondCount = Long.valueOf(sysParamsService.getSysParamsValueByKey(SysConf.BLOG_SECOND_COUNT));
                if (count > blogSecondCount) {
                    return ResultUtil.errorWithMessage("二级推荐不能超过" + blogSecondCount + "个");
                }
            }
            break;

            case ELevel.THIRD: {
                Long blogThirdCount = Long.valueOf(sysParamsService.getSysParamsValueByKey(SysConf.BLOG_THIRD_COUNT));
                if (count > blogThirdCount) {
                    return ResultUtil.errorWithMessage("三级推荐不能超过" + blogThirdCount + "个");
                }
            }
            break;

            case ELevel.FOURTH: {
                Long blogFourthCount = Long.valueOf(sysParamsService.getSysParamsValueByKey(SysConf.BLOG_FOURTH_COUNT));
                if (count > blogFourthCount) {
                    return ResultUtil.errorWithMessage("四级推荐不能超过" + blogFourthCount + "个");
                }
            }
            break;
            default: {

            }
        }
        return null;
    }

    /**
     * 保存成功后，需要发送消息到Es 和 redis
     *
     * @param isSave
     * @param blog
     */
    private void updateEsAndRedis(Boolean isSave, Blog blog) {
        // 保存操作，并且文章已设置发布
        if (isSave && EPublish.PUBLISH.equals(blog.getIsPublish())) {
            Map<String, Object> map = new HashMap<>();
            map.put(SysConf.COMMAND, SysConf.ADD);
            map.put(SysConf.BLOG, blog);
            map.put(SysConf.CREATE_TIME, blog.getCreateTime());

            //发送到RabbitMq
            rabbitTemplate.convertAndSend(SysConf.EXCHANGE_DIRECT, SysConf.MOGU_BLOG, map);

        } else if (EPublish.NO_PUBLISH.equals(blog.getIsPublish())) {

            //这是需要做的是，是删除redis中的该条博客数据
            Map<String, Object> map = new HashMap<>();
            map.put(SysConf.COMMAND, SysConf.EDIT);
            map.put(SysConf.BLOG, blog);
            map.put(SysConf.CREATE_TIME, blog.getCreateTime());
            //发送到RabbitMq
            rabbitTemplate.convertAndSend(SysConf.EXCHANGE_DIRECT, SysConf.MOGU_BLOG, map);
        }
    }

    /**
     * 设置博客的分类标签和内容
     *
     * @param list
     * @return
     */
    private List<Blog> setBlog(List<Blog> list) {
        List<String> fileUidList = new ArrayList<>();
        List<String> sortUids = new ArrayList<>();
        List<String> tagUids = new ArrayList<>();
        Set<String> userUidList = new HashSet<>();
        Set<String> adminUidList = new HashSet<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUidList.add(item.getFileUid());
            }
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                sortUids.add(item.getBlogSortUid());
            }
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(), SysConf.FILE_SEGMENTATION);
                tagUidsTemp.forEach(tag -> {
                    tagUids.add(tag);
                });
            }

            // 判断用户投稿，还是后台管理员添加
            if (EContributeSource.USER_PUBLISH.equals(item.getArticleSource())) {
                if (StringUtils.isNotEmpty(item.getUserUid())) {
                    userUidList.add(item.getUserUid());
                }
            } else {
                if (StringUtils.isNotEmpty(item.getAdminUid())) {
                    adminUidList.add(item.getAdminUid());
                }
            }
        });
        Collection<BlogSort> sortList = new ArrayList<>();
        Collection<Tag> tagList = new ArrayList<>();
        if (sortUids.size() > 0) {
            sortList = blogSortService.listByIds(sortUids);
        }
        if (tagUids.size() > 0) {
            tagList = tagService.listByIds(tagUids);
        }

        Map<String, BlogSort> sortMap = new HashMap<>();
        Map<String, Tag> tagMap = new HashMap<>();
        Map<String, String> pictureMap = FileFeignUtil.fileUidToFileUrlMap(fileUidList);

        sortList.forEach(item -> {
            sortMap.put(item.getUid(), item);
        });

        tagList.forEach(item -> {
            tagMap.put(item.getUid(), item);
        });


        // 获取用户信息
        List<User> userList = new ArrayList<>();
        if (userUidList.size() > 0) {
            userList = userService.getUserListAndAvatarByIds(userUidList);
        }
        List<Admin> adminList = new ArrayList<>();
        if (adminUidList.size() > 0) {
            adminList = adminService.getAdminListByUid(adminUidList);
        }
        Map<String, User> userMap = new HashMap<>();
        Map<String, Admin> adminMap = new HashMap<>();
        userList.forEach(item -> {
            userMap.put(item.getUid(), item);
        });
        adminList.forEach(item -> {
            adminMap.put(item.getUid(), item);
        });

        for (Blog item : list) {

            //设置分类
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                item.setBlogSort(sortMap.get(item.getBlogSortUid()));
            }

            //获取标签
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(), SysConf.FILE_SEGMENTATION);
                List<Tag> tagListTemp = new ArrayList<>();
                tagUidsTemp.forEach(tag -> {
                    if (tagMap.get(tag) != null) {
                        tagListTemp.add(tagMap.get(tag));
                    }
                });
                item.setTagList(tagListTemp);
            }

            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileUid(), SysConf.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();

                pictureUidsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
            }

            //获取用户【判断是用户投稿，还是后台添加】
            if (EContributeSource.USER_PUBLISH.equals(item.getArticleSource())) {
                if (StringUtils.isNotEmpty(item.getUserUid())) {
                    item.setUser(userMap.get(item.getUserUid()));
                }
            } else {
                if (StringUtils.isNotEmpty(item.getAdminUid())) {
                    User user = new User();
                    Admin admin = adminMap.get(item.getAdminUid());
                    if (admin != null) {
                        user.setAvatar(admin.getAvatar());
                        user.setUid(admin.getUid());
                        user.setOccupation(admin.getOccupation());
                        user.setGender(admin.getGender());
                        user.setSummary(admin.getSummary());
                        user.setNickName(admin.getNickName());
                        user.setPhotoUrl(admin.getPhotoUrl());
                        item.setUser(user);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 通过用户名查询相关用户信息
     *
     * @param keywords
     * @param currentPage
     * @param pageSize
     * @return
     */
    public Map<String, Object> getByUser(String keywords, Long currentPage, Long pageSize) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        queryWrapper.and(wrapper -> wrapper.like(SQLConf.NICK_NAME, keywords).or().like(SQLConf.SUMMARY, keywords).or().like("occupation", keywords));
        queryWrapper.like(SQLConf.NICK_NAME, keywords);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc("credits");
        IPage<User> pageList = userService.page(page, queryWrapper);
        List<User> users = pageList.getRecords();

        // 敏感信息过滤
        users = userService.convertUserList(users);

        List<Map<String, String>> blogList = blogMapper.getUsersBolgCountByUserId(users);
        Map<String, String> blogMap = webUtil.listCovertMap(blogList);
        List<Map<String, String>> commentList = commentMapper.getCommentCountByUserId(users);
        Map<String, String> commentMap = webUtil.listCovertMap(commentList);
        List<Map<String, String>> userPraiseList = userPraiseRecordMapper.getUserPraiseRecordCountByUserId(users);
        Map<String, String> userPraiseMap = webUtil.listCovertMap(userPraiseList);
        List<Map<String, String>> userWatchList = userWatchMapper.getUserWatchCountByUserId(users);
        Map<String, String> userWatchMap = webUtil.listCovertMap(userWatchList);
        List<Map<String, String>> userMomentList = userMomentMapper.getUserMomentCountByUserId(users);
        Map<String, String> userMomentMap = webUtil.listCovertMap(userMomentList);
        for (User user : users) {
            // 通过头像uid获取图片
            String pictureList = this.pictureFeignClient.getPicture(user.getAvatar(), SysConf.FILE_SEGMENTATION);
            List<String> photoList = webUtil.getPicture(pictureList);
            Map<String, Object> picMap = (Map<String, Object>) JsonUtils.jsonToObject(pictureList, Map.class);

            // 判断该用户是否含有头像信息
            if (SysConf.SUCCESS.equals(picMap.get(SysConf.CODE)) && photoList.size() > 0) {
                user.setPhotoUrl(photoList.get(0));
            }

            //  设置博客发表数
            String blogVisitCountJson = redisUtil.get(RedisConf.BLOG_PUBLISH_COUNT + Constants.SYMBOL_COLON + user.getUid());
            if (StringUtils.isNotEmpty(blogVisitCountJson)) {
                user.setBlogPublishCount(Integer.valueOf(blogVisitCountJson));
            } else {
                Integer blogPublishCount = Integer.valueOf(blogMap.get(user.getUid()) != null ? blogMap.get(user.getUid()) : "0");
                user.setBlogPublishCount(blogPublishCount);
                redisUtil.setEx(RedisConf.BLOG_PUBLISH_COUNT + Constants.SYMBOL_COLON + user.getUid(), JsonUtils.objectToJson(blogPublishCount), 10, TimeUnit.MINUTES);
            }

            // 设置评论数
            String commentPublishCountJson = redisUtil.get(RedisConf.COMMENT_PUBLISH_COUNT + Constants.SYMBOL_COLON + user.getUid());
            if (StringUtils.isNotBlank(commentPublishCountJson)) {
                user.setCommentPublishCount(Integer.valueOf(commentPublishCountJson));
            } else {
                Integer commentPublishCount = Integer.valueOf(commentMap.get(user.getUid()) != null ? commentMap.get(user.getUid()) : "0");
                user.setCommentPublishCount(commentPublishCount);
                redisUtil.setEx(RedisConf.COMMENT_PUBLISH_COUNT + Constants.SYMBOL_COLON + user.getUid(), JsonUtils.objectToJson(commentPublishCount), 10, TimeUnit.MINUTES);
            }

            // 设置点赞数
            String userPraiseRecordCountJson = redisUtil.get(RedisConf.USER_PRAISE_RECORD_COUNT + Constants.SYMBOL_COLON + user.getUid());
            if (StringUtils.isNotBlank(userPraiseRecordCountJson)) {
                user.setPraiseCount(Integer.valueOf(userPraiseRecordCountJson));
            } else {
                Integer praiseCount = Integer.valueOf(userPraiseMap.get(user.getUid()) != null ? userPraiseMap.get(user.getUid()) : "0");
                user.setPraiseCount(praiseCount);
                redisUtil.setEx(RedisConf.USER_PRAISE_RECORD_COUNT + Constants.SYMBOL_COLON + user.getUid(), JsonUtils.objectToJson(praiseCount), 10, TimeUnit.MINUTES);
            }

            // 设置关注数
            String userWatchCountJson = redisUtil.get(RedisConf.USER_WATCH_COUNT + Constants.SYMBOL_COLON + user.getUid());
            if (StringUtils.isNotBlank(userWatchCountJson)) {
                user.setPraiseCount(Integer.valueOf(userWatchCountJson));
            } else {
                Integer watchCount = Integer.valueOf(userWatchMap.get(user.getUid()) != null ? userWatchMap.get(user.getUid()) : "0");
                user.setPraiseCount(watchCount);
                redisUtil.setEx(RedisConf.USER_WATCH_COUNT + Constants.SYMBOL_COLON + user.getUid(), JsonUtils.objectToJson(watchCount), 10, TimeUnit.MINUTES);
            }

            //设置是否关注状态
            UserWatchVO userWatchVO = new UserWatchVO();
            userWatchVO.setToUserUid(user.getUid());
            Integer isWatch = userWatchService.checkCurrentUserWatch(userWatchVO);
            if (isWatch == 0) {
                //未关注
                user.setUserWatchStatus(0);
            } else if (isWatch == 1) {
                //单项关注
                user.setUserWatchStatus(1);
            } else {
                //互相关注
                user.setUserWatchStatus(2);
            }

            //设置动态发布数
            String userMomentCountJson = redisUtil.get(RedisConf.USER_MOMENT_COUNT + Constants.SYMBOL_COLON + user.getUid());
            if (StringUtils.isNotBlank(userMomentCountJson)) {
                user.setUserMomentCount(Integer.valueOf(userMomentCountJson));
            } else {
                Integer momentCount = Integer.valueOf(userMomentMap.get(user.getUid()) != null ? userMomentMap.get(user.getUid()) : "0");
                user.setUserMomentCount(momentCount);
                redisUtil.setEx(RedisConf.USER_MOMENT_COUNT + Constants.SYMBOL_COLON + user.getUid(), JsonUtils.objectToJson(momentCount), 10, TimeUnit.MINUTES);
            }

        }

        Map<String, Object> map = new HashMap<>();
        // 返回总记录数
        map.put(SysConf.TOTAL, pageList.getTotal());
        // 返回总页数
        map.put(SysConf.TOTAL_PAGE, pageList.getPages());
        // 返回当前页大小
        map.put(SysConf.PAGE_SIZE, pageList.getSize());
        // 返回当前页
        map.put(SysConf.CURRENT_PAGE, pageList.getCurrent());
        // 返回数据
        map.put(SysConf.BLOG_LIST, users);
        return map;

    }

    /**
     * 查询动态排行榜
     *
     * @return
     */
    @Override
    public List<Blog> getLeaderBlog(Boolean refresh) {

        String rankBlogListJson = redisUtil.get(RedisConf.RANK_LIST + Constants.SYMBOL_COLON + RedisConf.RANK_BLOG_LIST);
        List<Blog> leaders;
        if (StringUtils.isNotEmpty(rankBlogListJson) && refresh) {
            leaders = JsonUtils.jsonToList(rankBlogListJson, Blog.class);
        } else {
            QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("user_uid, count(uid) as sumCredits ");
            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            queryWrapper.eq(SQLConf.IS_PUBLISH, EStatus.ENABLE);
            queryWrapper.isNotNull(SQLConf.USER_UID);
            queryWrapper.groupBy(SQLConf.USER_UID);
            queryWrapper.orderByDesc(SQLConf.SUM_CREDITS);
            queryWrapper.last("limit 10");
            leaders = blogService.list(queryWrapper);

            List<String> userIds = new ArrayList<>();
            leaders.forEach((item) -> {
                userIds.add(item.getUserUid());
            });

            // 获取用户列表
            Map<String, User> userMap = this.usersConvert(userIds);

            leaders.forEach((item) -> {
                item.setUser(userMap.get(item.getUserUid()));
            });
            redisUtil.setEx(RedisConf.RANK_LIST + Constants.SYMBOL_COLON + RedisConf.RANK_BLOG_LIST, JsonUtils.objectToJson(leaders), 24, TimeUnit.HOURS);
        }
        return leaders;
    }

    @Override
    public Blog downloadBlog(String blogUid) {
        Blog blog = blogService.getById(blogUid);
        if (blog == null || EStatus.ENABLE != blog.getStatus()) {
            throw new QueryException("博客不存在");
        }
        String userUid = RequestHolder.getUserUid();
        if (StringUtils.isNotEmpty(blog.getUserUid()) && !blog.getUserUid().equals(userUid)) {
            throw new QueryException("无法下载其它用户的文章");
        }
        // 将html转换成markdown
        String blogContent = FileUtils.htmlToMarkdown(blog.getContent());
        blog.setContent(blogContent);
        return blog;
    }

    @Override
    public Integer getContinuousPublishBlogCount(String userUid) {
        // 获取连续发表文章数
        ContinuousDayVO continuousDayVO = blogMapper.getPublishContinuousDays(userUid, DateUtils.getToday());
        return continuousDayVO == null ? 0 : continuousDayVO.getContinuousDays();
    }

    @Override
    public Integer getBlogClickCount(String userUid) {

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.select("sum(click_count) as sum");
        Map<String, Object> map = blogService.getMap(queryWrapper);
        if (map.get("sum") != null) {
            return Integer.valueOf(map.get("sum").toString());
        }
        return 0;
    }

    @Override
    public void checkSubjectVisitAuth(Subject subject, Blog blog, boolean skip) {
        // 存储博客内容
        String blogContent = blog.getContent();
        // 进行校验
        checkSubjectVisitAuthSuccess(subject, blog, skip);
        // 先将博客内容还原回去，然后根据校验解决判断是否要隐藏
        blog.setContent(blogContent);
        // 如果校验通过，直接返回
        if (blog.getVisitAuthSuccess()) {
            return;
        }
        // 校验没通过，判断是否是全部隐藏
        if (blog.getLoadingArea() == 2) {
            blog.setContent("");
        }
    }

    public void checkSubjectVisitAuthSuccess(Subject subject, Blog blog, boolean skip) {
        // 将Subject中的VisitAuth写入到文章中【当文章归属专栏时，优先以专栏的访问权限为准】
        blog.setVisitAuth(subject.getVisitAuth());
        blog.setVisitAuthExtra(subject.getVisitAuthExtra());

        if (StringUtils.isNotEmpty(subject.getVisitAuthExtra())) {
            VisitAuthExtra visitAuthExtraVO = JsonUtils.jsonToPojo(subject.getVisitAuthExtra(), VisitAuthExtra.class);
            blog.setVisitAuthExtraVo(visitAuthExtraVO);
            if (visitAuthExtraVO != null) {
                blog.setLoadingArea(visitAuthExtraVO.getLoadingArea());
            }
            blog.setPrice(subject.getPrice());
            blog.setPayType(subject.getPayType());
        }

        // 判断是否需要进行权限豁免
        if (skip) {
            blog.setVisitAuthSuccess(true);
            return;
        }

        // 初始化Set存储访问类型【当配置多个访问类型时生效】
        Set<String> visitAuthSet = new HashSet<>(Arrays.asList(subject.getVisitAuth().split(",")));
        // 判断该校验是否执行
        Map<String, Boolean> visitAuthCheckMap = new HashMap<>();
        for (String s : visitAuthSet) {
            visitAuthCheckMap.put(s, false);
        }

        // 如果是公开可见，那么直接跳过
        if (EVisitAuthType.PUBLIC.getType().equals(subject.getVisitAuth())) {
            blog.setVisitAuthSuccess(true);
            return;
        }

        // 如果是输入验证后可见，需要判断Cookie中是否携带了校验code
        String userUid = RequestHolder.getUserUid();
        if (visitAuthSet.contains(EVisitAuthType.CODE.getType())) {
            HttpServletRequest request = RequestHolder.getRequest();
            assert request != null;
            String validCode = request.getHeader("validCode");
            // cookie中携带了验证码，校验通过
            if (StringUtils.isNotEmpty(validCode)) {
                checkOrSetAuth(blog, true);
                return;
            }
            // 判断Redis是否有访问记录
            String ip = IpUtils.getIpAddr(request);
            String value = redisUtil.get(RedisConf.LOADING_VALID + Constants.SYMBOL_COLON + ip);
            if (StringUtils.isNotEmpty(value)) {
                checkOrSetAuth(blog, true);
                return;
            }

            // 用户没登录，直接退出登录
            if (StringUtils.isEmpty(userUid)) {
                checkOrSetAuth(blog, false);
                return;
            }
            User user = userService.getById(userUid);
            boolean visitAuthSuccess = (user != null && user.getLoadingValid() == 1);
            checkOrSetAuth(blog, visitAuthSuccess);
            // 判断校验是否都通过，如果都通过，那么直接返回
            if (checkVisitAuthReturn(visitAuthCheckMap, visitAuthSuccess, EVisitAuthType.CODE)) {
                return;
            }
        }

        // 获取一下订单数
        if (visitAuthSet.contains(EVisitAuthType.CASH.getType())) {
            // 判断支付订单数
            PayOrderVO payOrderVO = new PayOrderVO();
            payOrderVO.setResourceUid(subject.getUid());
            payOrderVO.setOrderStatus(EOrderStatus.Finish);
            int payOrderCount = commonService.getPayOrderCount(payOrderVO);
            // 文章支付数需要替换
            blog.setPayOrderCount(payOrderCount);
            // 专栏支付数也需要替换
            subject.setPayOrderCount(payOrderCount);
        }

        // 判断是否是登录后可见，需要校验用户是否登录【如果没登录，后面的校验都通过不了，直接返回】
        if (!StringUtils.isNotEmpty(userUid)) {
            checkOrSetAuth(blog, false);
            return;
        }

        // 登录后可见
        if (visitAuthSet.contains(EVisitAuthType.LOGIN.getType())) {
            // 能到这里肯定是已登录了，直接跳过
            checkOrSetAuth(blog, true);
            return;
        }

        // 如果当前文章的作者是自己，直接放行
        boolean isAuthor = blog.getUserUid().equals(userUid);
        // 如果是作者可见，判断是否是作者
        if (visitAuthSet.contains(EVisitAuthType.AUTHOR.getType())) {
            checkOrSetAuth(blog, isAuthor);
            if (checkVisitAuthReturn(visitAuthCheckMap, isAuthor, EVisitAuthType.AUTHOR)) {
                return;
            }
        }

        // 如果文章作者是自己，不用再走后续校验
        if (isAuthor) {
            checkOrSetAuth(blog, true);
            return;
        }

        // 关注后可见
        if (visitAuthSet.contains(EVisitAuthType.WATCH.getType())) {
            UserWatchVO userWatchVO = new UserWatchVO();
            userWatchVO.setUserUid(userUid);
            userWatchVO.setToUserUid(blog.getUserUid());
            int count = userWatchService.getUserWatchCount(userWatchVO);
            checkOrSetAuth(blog, count > 0);
            if (checkVisitAuthReturn(visitAuthCheckMap, count > 0, EVisitAuthType.WATCH)) {
                return;
            }
        }

        // 判断是否是评论后可见
        if (visitAuthSet.contains(EVisitAuthType.COMMENT.getType())) {
            CommentVO commentVO = new CommentVO();
            commentVO.setBlogUid(blog.getUid());
            commentVO.setUserUid(userUid);
            int count = commentService.getCommentCount(commentVO);
            checkOrSetAuth(blog, count > 0);
            if (checkVisitAuthReturn(visitAuthCheckMap, count > 0, EVisitAuthType.COMMENT)) {
                return;
            }
        }

        // 收藏后可以阅读
        if (visitAuthSet.contains(EVisitAuthType.COLLECT.getType())) {
            CollectVO collectVO = new CollectVO();
            collectVO.setBizUid(blog.getUid());
            collectVO.setUserUid(userUid);
            int count = collectService.getUserCollectCount(collectVO);
            checkOrSetAuth(blog, count > 0);
            if (checkVisitAuthReturn(visitAuthCheckMap, count > 0, EVisitAuthType.COLLECT)) {
                return;
            }
        }

        // 点赞后可以阅读
        if (visitAuthSet.contains(EVisitAuthType.PRAISE.getType())) {
            UserPraiseRecordVO userPraiseRecordVO = new UserPraiseRecordVO();
            userPraiseRecordVO.setResourceUid(blog.getUid());
            userPraiseRecordVO.setUserUid(userUid);
            userPraiseRecordVO.setPraiseType(EPraiseType.PRAISE.getType());
            int count = userPraiseRecordService.getPraiseCount(userPraiseRecordVO);
            checkOrSetAuth(blog, count > 0);
            if (checkVisitAuthReturn(visitAuthCheckMap, count > 0, EVisitAuthType.PRAISE)) {
                return;
            }
        }

        // 判断是否会员可见
        if (visitAuthSet.contains(EVisitAuthType.VIP.getType())) {
            Integer userTag = RequestHolder.getUserTag();
            checkOrSetAuth(blog, userTag > 0);
            if (checkVisitAuthReturn(visitAuthCheckMap, userTag > 0, EVisitAuthType.VIP)) {
                return;
            }
        }

        // 判断是否是付费可见
        if (visitAuthSet.contains(EVisitAuthType.CASH.getType())) {
            // 判断是否支付
            boolean isPay = commonService.checkWhetherPay(subject.getUid());
            checkOrSetAuth(blog, isPay);
            if (checkVisitAuthReturn(visitAuthCheckMap, isPay, EVisitAuthType.CASH)) {
                return;
            }
        }

        // 判断是否是订阅可见
        if (visitAuthSet.contains(EVisitAuthType.SUBJECT.getType())) {
            UserSubscribeVO userSubscribeVO = new UserSubscribeVO();
            userSubscribeVO.setUserUid(userUid);
            userSubscribeVO.setResourceUid(subject.getUid());
            userSubscribeVO.setResourceType(EResourceType.SUBJECT.getType());
            boolean isSubscribe = userSubscribeService.checkUserSubscribe(userSubscribeVO);
            checkOrSetAuth(blog, isSubscribe);
            if (checkVisitAuthReturn(visitAuthCheckMap, isSubscribe, EVisitAuthType.SUBJECT)) {
                return;
            }
        }

        // 以下将校验访问的额外信息，如果博客中没有携带额外信息直接允许访问;
        String visitAuthExtra = subject.getVisitAuthExtra();
        if (StringUtils.isEmpty(visitAuthExtra)) {
            checkOrSetAuth(blog, true);
            return;
        }
        VisitAuthExtra visitAuthExtraVO = JsonUtils.jsonToPojo(visitAuthExtra, VisitAuthExtra.class);
        if (visitAuthExtraVO == null) {
            checkOrSetAuth(blog, true);
            return;
        }

        // 判断是否是输入密码可见
        if (visitAuthSet.contains(EVisitAuthType.PASSWORD.getType())) {
            // 判断这个用户有没有前置校验通过
            String checkText = redisUtil.get(RedisConf.VISIT_AUTH + Constants.SYMBOL_COLON + subject.getUid() + Constants.SYMBOL_COLON + userUid);
            checkOrSetAuth(blog, StringUtils.isNotEmpty(checkText));
            if (checkVisitAuthReturn(visitAuthCheckMap, StringUtils.isNotEmpty(checkText), EVisitAuthType.PASSWORD)) {
                return;
            }
        }

        // 判断是否是标签用户可见
        if (visitAuthSet.contains(EVisitAuthType.TAG.getType())) {
            // 获取用户标签
            String userTag = RequestHolder.getUserTag().toString();
            boolean visitAuthSuccess = visitAuthExtraVO.getUserTagList().contains(userTag);
            checkOrSetAuth(blog, visitAuthSuccess);
            if (checkVisitAuthReturn(visitAuthCheckMap, visitAuthSuccess, EVisitAuthType.TAG)) {
                return;
            }
        }

        // 判断是否是指定用户可见
        if (visitAuthSet.contains(EVisitAuthType.USER.getType())) {
            boolean visitAuthSuccess = visitAuthExtraVO.getUserUidList().contains(userUid);
            checkOrSetAuth(blog, visitAuthExtraVO.getUserUidList().contains(userUid));
            if (checkVisitAuthReturn(visitAuthCheckMap, visitAuthSuccess, EVisitAuthType.USER)) {
                return;
            }
        }
    }

    /**
     * 多个访问校验，如果一个都没通过，那么返回false
     * @param visitAuthCheckMap
     * @param visitAuthSuccess
     * @param authType
     * @return
     */
    private static boolean checkVisitAuthReturn(Map<String, Boolean> visitAuthCheckMap, boolean visitAuthSuccess,EVisitAuthType authType) {
        // 如果本次校验都通过了，直接可以返回
        if (visitAuthSuccess) {
            return true;
        }
        // 首先将本次校验类型push
        visitAuthCheckMap.put(authType.getType(), true);
        // 然后判断所有校验是否都走了一遍，如果都走了那么也可以返回
        for (String key : visitAuthCheckMap.keySet()) {
            if (!visitAuthCheckMap.get(key)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void checkVisitAuth(Blog blog) {
        String blogContent = blog.getContent();
        // 进行校验
        checkVisitAuthSuccess(blog);
        // 先将博客内容还原回去，然后根据校验解决判断是否要隐藏
        blog.setContent(blogContent);
        // 如果校验通过，直接返回
        if (blog.getVisitAuthSuccess()) {
            return;
        }
        // 校验没通过，并且是隐藏
        if (blog.getLoadingArea() == 2) {
            blog.setContent("");
        }
    }

    public void checkVisitAuthSuccess(Blog blog) {
        // 可能脏数据
        if (StringUtils.isEmpty(blog.getVisitAuth())) {
            blog.setVisitAuthSuccess(true);
            return;
        }
        // 初始化Set存储访问类型
        Set<String> visitAuthSet = new HashSet<>(Arrays.asList(blog.getVisitAuth().split(",")));
        // 判断该校验是否执行
        Map<String, Boolean> visitAuthCheckMap = new HashMap<>();
        for (String s : visitAuthSet) {
            visitAuthCheckMap.put(s, false);
        }

        // 如果包括公开可见，那么直接跳过
        if (visitAuthSet.contains(EVisitAuthType.PUBLIC.getType())) {
            blog.setVisitAuthSuccess(true);
            return;
        }

        // 如果是输入验证后可见，需要判断Cookie中是否携带了校验code
        String userUid = RequestHolder.getUserUid();
        if (visitAuthSet.contains(EVisitAuthType.CODE.getType())) {
            HttpServletRequest request = RequestHolder.getRequest();
            assert request != null;
            String validCode = request.getHeader("validCode");
            // cookie中携带了验证码，校验通过
            if (StringUtils.isNotEmpty(validCode)) {
                checkOrSetAuth(blog, true);
                return;
            }
            // 用户没登录，直接退出登录
            if (StringUtils.isEmpty(userUid)) {
                checkOrSetAuth(blog, false);
                return;
            }
            User user = userService.getById(userUid);
            boolean visitAuthSuccess = (user != null && user.getLoadingValid() == 1);
            checkOrSetAuth(blog, visitAuthSuccess);
            // 判断校验是否都通过，如果都通过，那么直接返回
            if (checkVisitAuthReturn(visitAuthCheckMap, visitAuthSuccess, EVisitAuthType.CODE)) {
                return;
            }
        }

        // 获取一下订单数
        if (visitAuthSet.contains(EVisitAuthType.CASH.getType())) {
            // 判断支付订单数
            PayOrderVO payOrderVO = new PayOrderVO();
            payOrderVO.setResourceUid(blog.getUid());
            payOrderVO.setOrderStatus(EOrderStatus.Finish);
            int payOrderCount = commonService.getPayOrderCount(payOrderVO);
            blog.setPayOrderCount(payOrderCount);
        }

        // 判断是否是登录后可见，需要校验用户是否登录【如果没登录，后面的校验都通过不了，直接返回】
        if (!StringUtils.isNotEmpty(userUid)) {
            checkOrSetAuth(blog, false);
            return;
        }

        // 登录后可见
        if (visitAuthSet.contains(EVisitAuthType.LOGIN.getType())) {
            // 能到这里肯定是已登录了，直接跳过
            checkOrSetAuth(blog, true);
            checkVisitAuthReturn(visitAuthCheckMap, true, EVisitAuthType.LOGIN);
            return;
        }

        // 如果当前文章的作者是自己，直接放行
        boolean isAuthor = blog.getUserUid().equals(userUid);
        // 如果是作者可见，判断是否是作者
        if (visitAuthSet.contains(EVisitAuthType.AUTHOR.getType())) {
            checkOrSetAuth(blog, isAuthor);
            if (checkVisitAuthReturn(visitAuthCheckMap, isAuthor, EVisitAuthType.AUTHOR)) {
                return;
            }
        }

        // 如果文章作者是自己，不用再走后续校验
        if (isAuthor) {
            checkOrSetAuth(blog, true);
            return;
        }

        // 关注后可见
        if (visitAuthSet.contains(EVisitAuthType.WATCH.getType())) {
            UserWatchVO userWatchVO = new UserWatchVO();
            userWatchVO.setUserUid(userUid);
            userWatchVO.setToUserUid(blog.getUserUid());
            int count = userWatchService.getUserWatchCount(userWatchVO);
            checkOrSetAuth(blog, count > 0);
            if (checkVisitAuthReturn(visitAuthCheckMap, count > 0, EVisitAuthType.WATCH)) {
                return;
            }
        }

        // 判断是否是评论后可见
        if (visitAuthSet.contains(EVisitAuthType.COMMENT.getType())) {
            CommentVO commentVO = new CommentVO();
            commentVO.setBlogUid(blog.getUid());
            commentVO.setUserUid(userUid);
            int count = commentService.getCommentCount(commentVO);
            checkOrSetAuth(blog, count > 0);
            if (checkVisitAuthReturn(visitAuthCheckMap, count > 0, EVisitAuthType.COMMENT)) {
                return;
            }
        }

        // 判断是否是收藏可见
        if (visitAuthSet.contains(EVisitAuthType.COLLECT.getType())) {
            CollectVO collectVO = new CollectVO();
            collectVO.setBizUid(blog.getUid());
            collectVO.setUserUid(userUid);
            int count = collectService.getUserCollectCount(collectVO);
            checkOrSetAuth(blog, count > 0);
            if (checkVisitAuthReturn(visitAuthCheckMap, count > 0, EVisitAuthType.COLLECT)) {
                return;
            }
        }

        // 点赞后可以阅读
        if (visitAuthSet.contains(EVisitAuthType.PRAISE.getType())) {
            UserPraiseRecordVO userPraiseRecordVO = new UserPraiseRecordVO();
            userPraiseRecordVO.setResourceUid(blog.getUid());
            userPraiseRecordVO.setUserUid(userUid);
            userPraiseRecordVO.setPraiseType(EPraiseType.PRAISE.getType());
            int count = userPraiseRecordService.getPraiseCount(userPraiseRecordVO);
            checkOrSetAuth(blog, count > 0);
            if (checkVisitAuthReturn(visitAuthCheckMap, count > 0, EVisitAuthType.PRAISE)) {
                return;
            }
        }

        // 判断是否会员可见
        if (visitAuthSet.contains(EVisitAuthType.VIP.getType())) {
            Integer userTag = RequestHolder.getUserTag();
            checkOrSetAuth(blog, userTag > 0);
            if (checkVisitAuthReturn(visitAuthCheckMap, userTag > 0, EVisitAuthType.VIP)) {
                return;
            }
        }

        // 判断是否是付费可见
        if (visitAuthSet.contains(EVisitAuthType.CASH.getType())) {
            // 判断是否支付
            boolean isPay = commonService.checkWhetherPay(blog.getUid());
            checkOrSetAuth(blog, isPay);
            if (checkVisitAuthReturn(visitAuthCheckMap, isPay, EVisitAuthType.CASH)) {
                return;
            }
        }

        // 以下将校验访问的额外信息，如果博客中没有携带额外信息直接允许访问【可能是脏数据，避免脏数据导致访问不可见】;
        String visitAuthExtra = blog.getVisitAuthExtra();
        if (StringUtils.isEmpty(visitAuthExtra)) {
            checkOrSetAuth(blog, true);
            return;
        }
        VisitAuthExtra visitAuthExtraVO = JsonUtils.jsonToPojo(visitAuthExtra, VisitAuthExtra.class);
        if (visitAuthExtraVO == null) {
            checkOrSetAuth(blog, true);
            return;
        }

        // 判断是否是输入密码可见
        if (visitAuthSet.contains(EVisitAuthType.PASSWORD.getType())) {
            // 判断这个用户有没有前置校验通过
            String checkText = redisUtil.get(RedisConf.VISIT_AUTH + Constants.SYMBOL_COLON + blog.getUid() + Constants.SYMBOL_COLON + userUid);
            checkOrSetAuth(blog, StringUtils.isNotEmpty(checkText));
            if (checkVisitAuthReturn(visitAuthCheckMap, StringUtils.isNotEmpty(checkText), EVisitAuthType.PASSWORD)) {
                return;
            }
        }

        // 判断是否是标签用户可见
        if (visitAuthSet.contains(EVisitAuthType.TAG.getType())) {
            // 获取用户标签
            String userTag = RequestHolder.getUserTag().toString();
            boolean visitAuthCheckSuccess = visitAuthExtraVO.getUserTagList().contains(userTag);
            checkOrSetAuth(blog, visitAuthExtraVO.getUserTagList().contains(userTag));
            if (checkVisitAuthReturn(visitAuthCheckMap, visitAuthCheckSuccess, EVisitAuthType.TAG)) {
                return;
            }
        }

        // 判断是否是指定用户可见
        if (visitAuthSet.contains(EVisitAuthType.USER.getType())) {
            boolean visitAuthCheckSuccess = visitAuthExtraVO.getUserUidList().contains(userUid);
            checkOrSetAuth(blog, visitAuthExtraVO.getUserUidList().contains(userUid));
            if (checkVisitAuthReturn(visitAuthCheckMap, visitAuthCheckSuccess, EVisitAuthType.USER)) {
                return;
            }
        }
    }


    private static void checkOrSetAuth(Blog blog, boolean checkSuccess) {
        if (checkSuccess) {
            blog.setVisitAuthSuccess(true);
        } else {
            blog.setVisitAuthSuccess(false);
//            // 如果是全不可见，需要隐藏内容
//            if (blog.getLoadingArea() == 2) {
//                blog.setContent("");
//            }
        }
    }

    /**
     * List<userIds> 转换为 Map<userId , User>
     *
     * @param userIds
     * @return
     */
    private Map<String, User> usersConvert(List<String> userIds) {
        Map<String, User> userMap = new HashMap<>();
        if (userIds.size() > 0) {
            Collection<User> collection = userService.listByIds(userIds);
            // 设置头像
            userService.setUserAvatar(collection);
            // 过滤敏感信息
            List<User> userList = userService.convertUserList(collection);
            for (User user : userList) {
                userMap.put(user.getUid(), user);
            }
        }
        return userMap;
    }

    public enum AdminOption {
        /**
         * 修改
         */
        EDIT(),
        /**
         * 删除
         */
        DELETE(),
        /**
         * 审核/拒审
         */
        AUDIT(),
        /**
         * 发布/下架
         */
        PUBLISH();
    }

}
