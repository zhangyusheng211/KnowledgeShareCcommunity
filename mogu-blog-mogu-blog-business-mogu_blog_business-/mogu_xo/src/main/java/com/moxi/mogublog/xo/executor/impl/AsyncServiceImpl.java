package com.moxi.mogublog.xo.executor.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.Notice;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.feign.SearchFeignClient;
import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.commons.vo.NoticeVO;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.SpringUtils;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.AdminService;
import com.moxi.mogublog.xo.service.NoticeService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import com.moxi.mougblog.base.global.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 异步执行器
 *
 * @author 遇见
 */
@Slf4j
@Service("asyncService")
public class AsyncServiceImpl implements AsyncService {
    /**
     * 通知模块
     */
    @Resource
    private NoticeService noticeService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private AdminService adminService;
    @Resource
    private UserService userService;

    @Resource
    private WebFeignClient webFeignClient;

    private SearchFeignClient searchFeignClient;

    @Override
    public void executeAsyncBusinessNotice(NoticeVO noticeVO) {
        noticeService.addNotice(noticeVO);
    }

    @Override
    public void executeAsyncBusinessNotice(String userUid, String content) {
        NoticeVO noticeVO = new NoticeVO();
        noticeVO.setUserUid(userUid);
        noticeVO.setContent(content);
        noticeVO.setNoticeType(ENoticeType.SYSTEM.getCode());
        noticeVO.setIsBlack(Constants.NUM_ZERO);
        this.executeAsyncBusinessNotice(noticeVO);
    }

    @Override
    @Async("asyncServiceExecutor")
    public void executeAsyncBusinessNotice(Boolean isAdmin, String uid, String businessUid, Integer businessTypeCode) {
        // 向关注该作者的用户发送通知
        NoticeVO noticeVO = new NoticeVO();
        if (isAdmin) {
            noticeVO.setAdminUid(uid);
        } else {
            noticeVO.setUserUid(uid);
        }
        noticeVO.setBusinessUid(businessUid);
        noticeVO.setBusinessType(businessTypeCode);
        noticeVO.setNoticeType(ENoticeType.SYSTEM.getCode());
        noticeService.batchAddNoticeByWatch(noticeVO);
    }

    @Override
    @Async("asyncServiceExecutor")
    public void executeAsyncUpdateEsAndRedis(AggEsDoc aggEsDoc, EOperate operate) {

        //从Redis清空对应的数据
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_ONE);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_TWO);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_THREE);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_FOUR);
        redisUtil.delete(RedisConf.HOT_BLOG);
        redisUtil.delete(RedisConf.NEW_BLOG);
        redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_CONTRIBUTE_COUNT);
        redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_SORT);
        redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_TAG);

        // 若更新时没有用户信息，从库里查询
        if (aggEsDoc.getUser() == null && StringUtils.isNotEmpty(aggEsDoc.getUserUid())) {
            log.info("无用户信息，从库里查询:" + aggEsDoc.getUserUid());
            User user = userService.getById(aggEsDoc.getUserUid());
            if (user != null) {
                aggEsDoc.setUser(userService.convertUser(userService.setUserAvatar(user)));
            }
        }

        // 更新ES索引
        Map<String, String> map = new HashMap<>();
        switch (operate) {
            case ADD: {
                map.put(SysConf.COMMAND, SysConf.ADD);
            }
            break;
            case EDIT:
            case AUDIT: {
                map.put(SysConf.COMMAND, SysConf.EDIT);
            }
            break;
            case DELETE: {
                map.put(SysConf.COMMAND, SysConf.DELETE);
            }
            break;
            default: {
                throw new BusinessException("错误的操作类型：" + operate);
            }
        }
        map.put(SysConf.AGG_ES_DOC, JsonUtils.objectToJson(aggEsDoc));

        // 推送MQ消息进行索引同步
        // rabbitTemplate.convertAndSend(SysConf.EXCHANGE_DIRECT, SysConf.MOGU_BLOG, map);

        // 直接调用推送服务
        this.updateEs(map);
    }

    @Override
    public void executeAsyncNotice(String userUid, ENoticeType noticeType, EBusinessType businessType, String resourceUid) {
        // 给指定的用户推送通知
        NoticeVO noticeVO = new NoticeVO();
        noticeVO.setNoticeType(noticeType.getCode());
        noticeVO.setBusinessType(businessType.getCode());
        noticeVO.setUserUid(userUid);
        noticeVO.setBusinessUid(resourceUid);
        noticeVO.setIsBlack(0);
        noticeService.addNotice(noticeVO);
    }

    /**
     * 发送后台站内信通知
     *
     * @param isAdmin
     * @param uid
     * @param businessUid
     * @param businessTypeCode
     */
    @Override
    @Async("asyncServiceExecutor")
    public void executeAsyncBusinessBlackNotice(Boolean isAdmin, String uid, String businessUid, Integer businessTypeCode, String content) {
        NoticeVO noticeVO = new NoticeVO();
        if (isAdmin) {
            noticeVO.setAdminUid(uid);
        } else {
            noticeVO.setUserUid(uid);
        }
        noticeVO.setBusinessUid(businessUid);
        noticeVO.setBusinessType(businessTypeCode);
        noticeVO.setContent(content);
        noticeVO.setStatus(EStatus.ENABLE);
        noticeVO.setNoticeType(ENoticeType.SYSTEM.getCode());
        noticeVO.setIsBlack(1);
        sendBlackBachNotice(noticeVO);

    }

    /**
     * 批量向所有管理员发送通知
     *
     * @param noticeVO
     */
    private void sendBlackBachNotice(NoticeVO noticeVO) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<Admin> adminList = adminService.list(queryWrapper);
        ArrayList<Notice> noticeList = new ArrayList<>();
        adminList.forEach((item) -> {
            Notice vo = new Notice();
            try {
                BeanUtils.copyProperties(vo, noticeVO);
            } catch (Exception e) {
                e.printStackTrace();
            }
            vo.setAdminUid(item.getUid());
            // 发送后台站内信管理员小红点通知
            this.sendBlackNoticeByRedis(vo);
            noticeList.add(vo);
        });
        noticeService.saveBatch(noticeList);
    }


    /**
     * 通过redis发送后台站内信通知（小红点）
     *
     * @param notice
     */
    private void sendBlackNoticeByRedis(Notice notice) {
        String redisKey = RedisConf.ADMIN_NOTICE_POINT + Constants.SYMBOL_COLON + notice.getAdminUid();
        // 消息红点保留7天
        String count = redisUtil.get(redisKey);
        if (StringUtils.isNotEmpty(count)) {
            redisUtil.incrBy(redisKey, Constants.NUM_ONE);
        } else {
            redisUtil.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
        }
    }

    /**
     * 更新ES缓存
     *
     * @param map
     */
    public void updateEs(Map<String, String> map) {
        log.info("MQ消费");
        if (map != null) {
            String comment = map.get(SysConf.COMMAND);

            String searchModel = ESearchModel.SQL;
            String resultStr = webFeignClient.getSearchModel();
            Map<String, String> resultTempMap = (Map<String, String>) JsonUtils.jsonToMap(resultStr, String.class);
            if (resultTempMap.get(SysConf.CODE) != null && SysConf.SUCCESS.equals(resultTempMap.get(SysConf.CODE))) {
                searchModel = resultTempMap.get(SysConf.DATA);
            }

            AggEsDoc aggEsDoc = null;
            if (ESearchModel.ES.equals(searchModel)) {
                // 解析Json
                String aggEsDocJson = map.get(SysConf.AGG_ES_DOC);
                if (StringUtils.isNotEmpty(aggEsDocJson)) {
                    aggEsDoc = JsonUtils.jsonToPojo(aggEsDocJson, AggEsDoc.class);
                }

                try {
                    searchFeignClient = SpringUtils.getBean(SearchFeignClient.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                if (ESearchModel.ES.equals(searchModel) && aggEsDoc == null) {
                    return;
                }
                // 处理命令
                switch (comment) {
                    case SysConf.ADD: {
                        log.info("【mogu-sms】mogu-sms处理增加索引");
                        updateSearch(aggEsDoc);
                        if (ESearchModel.ES.equals(searchModel)) {
                            // 增加ES索引
                            List<AggEsDoc> aggEsDocList = new ArrayList<>();
                            aggEsDocList.add(aggEsDoc);
                            searchFeignClient.batchCreateEsIndex(aggEsDocList);
                        }
                    }
                    break;

                    case SysConf.EDIT: {
                        log.info("【mogu-sms】mogu-sms处理编辑索引");
                        updateSearch(aggEsDoc);
                        if (ESearchModel.ES.equals(searchModel)) {
                            // 增加ES索引
                            List<AggEsDoc> aggEsDocList = new ArrayList<>();
                            aggEsDocList.add(aggEsDoc);
                            searchFeignClient.batchUpdateEsIndex(aggEsDocList);
                        }
                    }
                    break;

                    case SysConf.DELETE: {
                        updateSearch(aggEsDoc);
                        if (ESearchModel.ES.equals(searchModel)) {
                            // 增加ES索引
                            log.info("【mogu-sms】mogu-sms处理删除索引:" + aggEsDoc.getTitle());
                            List<String> uidList = new ArrayList<>();
                            uidList.add(aggEsDoc.getUid());
                            searchFeignClient.batchDeleteEsIndex(uidList);
                        } else if (ESearchModel.SOLR.equals(searchModel)) {
                            // 增加solr索引
                            log.error("【mogu-sms】Solr搜索模式已下线");
                        }
                    }
                    break;
                    default: {
                        log.info("【mogu-sms】mogu-sms处理索引兜底方法");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("【mogu-sms】出现异常，请查看mogu-search是否启动！searchModel: " + searchModel);
            }

        }
    }

    /**
     * 更新缓存
     *
     * @param aggEsDoc
     */
    private void updateSearch(AggEsDoc aggEsDoc) {
        try {
            Date createTime = aggEsDoc.getCreateTime();
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM);
            String sd = sdf.format(createTime);
            String[] list = sd.split(Constants.SYMBOL_HYPHEN);
            String year = list[0];
            String month = list[1];
            String key = year + "年" + month + "月";
            redisUtil.delete(RedisConf.BLOG_SORT_BY_MONTH + Constants.SYMBOL_COLON + key);
            String jsonResult = redisUtil.get(RedisConf.MONTH_SET);
            ArrayList<String> monthSet = (ArrayList<String>) JsonUtils.jsonArrayToArrayList(jsonResult);
            Boolean haveMonth = false;
            if (monthSet != null) {
                for (String item : monthSet) {
                    if (item.equals(key)) {
                        haveMonth = true;
                        break;
                    }
                }
                if (!haveMonth) {
                    monthSet.add(key);
                    redisUtil.set(RedisConf.MONTH_SET, JsonUtils.objectToJson(monthSet));
                }
            }

        } catch (Exception e) {
            log.error("更新Redis失败");
        }
    }
}
