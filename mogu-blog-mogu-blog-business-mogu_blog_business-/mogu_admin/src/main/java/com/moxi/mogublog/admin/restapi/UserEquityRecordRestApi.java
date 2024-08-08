package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.global.MessageConf;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.UserEquityRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户权益表 RestApi
 *
 * @author 陌溪
 * @date 2021年12月23日23:51:02
 */

@RestController
@RequestMapping("/userEquityRecord")
@Api(value = "用户权益相关接口", tags = {"用户权益相关接口"})
@Slf4j
public class UserEquityRecordRestApi {

    @Autowired
    private UserEquityRecordService userEquityRecordService;

    @AuthorityVerify
    @OperationLogger(value = "手动发放签到卡")
    @ApiOperation(value = "手动发放签到卡", notes = "手动发放签到卡", response = String.class)
    @PostMapping("/sendSignInCard")
    public String sendSignInCard() {
        Boolean isSuccess = userEquityRecordService.batchSendSignInCard();
        if (isSuccess) {
            return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
        } else {
            return ResultUtil.successWithMessage(MessageConf.INSERT_FAIL);
        }
    }
}
