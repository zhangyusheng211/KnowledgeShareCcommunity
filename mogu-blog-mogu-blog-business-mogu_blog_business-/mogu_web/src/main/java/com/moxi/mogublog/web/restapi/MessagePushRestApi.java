package com.moxi.mogublog.web.restapi;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.entity.MessagePush;
import com.moxi.mogublog.commons.vo.CollectVO;
import com.moxi.mogublog.commons.vo.MessagePushVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.CollectService;
import com.moxi.mogublog.xo.service.MessagePushService;
import com.moxi.mougblog.base.enums.EBehavior;
import com.moxi.mougblog.base.enums.EPushMethod;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 博客收藏 RestApi
 *
 * @author 陌溪
 * @date 2023年5月22日23:28:29
 */
@RestController
@RequestMapping("/messagePush")
@Api(value = "消息推送相关接口", tags = {"消息推送相关接口"})
@Slf4j
public class MessagePushRestApi {
    @Autowired
    private MessagePushService messagePushService;

    @ApiOperation(value = "获取网站通知", notes = "获取网站通知")
    @PostMapping("/getWebNotice")
    public String getWebNotice() {
        log.info("通过业务id获取收藏");
        MessagePushVO messagePushVO = new MessagePushVO();
        messagePushVO.setPushMethod(String.valueOf(EPushMethod.ANNOUNCEMENT.getType()));
        messagePushVO.setPageSize(1L);
        messagePushVO.setCurrentPage(1L);
        IPage<MessagePush> messagePushIPage = messagePushService.getPageList(messagePushVO);
        List<MessagePush> messagePushList = messagePushIPage.getRecords();
        if (messagePushList.size() > 0) {
            return ResultUtil.successWithData(messagePushList.get(0));
        }
        return ResultUtil.errorWithMessage(MessageConf.ENTITY_NOT_EXIST);
    }
}

