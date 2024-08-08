package com.moxi.mogublog.sms.restapi;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.annotion.FeignSecurity.FeignSecurity;
import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.SecretConfig;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.vo.AdminVO;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mogublog.sms.languageModel.LanguageModelService;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.AdminService;
import com.moxi.mogublog.xo.service.SecretConfigService;
import com.moxi.mogublog.xo.service.SysParamsService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mougblog.base.enums.EBusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用于feign操作的接口，只允许服务内部调用
 */

@RestController
@RequestMapping("/smsFeign")
@Slf4j
public class SmsFeignRestApi {
    @Resource
    private UserService userService;
    @Resource
    private AdminService adminService;
    @Resource
    private SysParamsService sysParamsService;
    @Resource
    private LanguageModelService languageModelService;
    @Resource
    private SecretConfigService secretConfigService;

    @FeignSecurity
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @PostMapping("/getUserListByPage")
    public List<User> getUserListByPage(@RequestBody UserVO userVO) {
        log.info("获取用户列表");
        IPage<User> userIPage = userService.getPageList(userVO);
        return userService.convertUserList(userIPage.getRecords());
    }

    @FeignSecurity
    @ApiOperation(value = "获取管理员列表", notes = "获取用户列表")
    @PostMapping("/getAdminListByPage")
    public List<Admin> getAdminListByPage(@RequestBody AdminVO adminVO) {
        log.info("获取用户列表");
        return adminService.getAdminListByUid(adminVO.getAdminUidList());
    }

    @FeignSecurity
    @ApiOperation(value = "获取用户钱包金额", notes = "获取用户钱包金额")
    @PostMapping("/getUserAmount")
    public String getUserAmount(@ApiParam(name = "userUid", value = "钱包余额消耗", required = true) @RequestParam(name = "userUid", required = true) String userUid) {
        log.info("获取用户钱包金额");
        return ResultUtil.successWithData(userService.getUserAmount(userUid));
    }


    /**
     * 更新用户钱包余额
     * @param userUid
     * @param amount
     * @return
     */
    @FeignSecurity
    @ApiOperation(value = "更新用户钱包余额", notes = "更新用户钱包余额")
    @PostMapping("/updateAmountByUserUid")
    public String updateAmountByUserUid(@ApiParam(name = "userUid", value = "钱包余额消耗", required = true) @RequestParam(name = "userUid", required = true) String userUid,
                                        @ApiParam(name = "amount", value = "钱包余额消耗", required = true) @RequestParam(name = "amount", required = true, defaultValue = "0") Long amount) {
        log.info("消耗钱包账户余额");
        userService.updateAmountByUserUid(userUid, amount);
        return ResultUtil.successWithMessage("更新用户余额成功");
    }

    @FeignSecurity
    @ApiOperation(value = "获取系统内置参数", notes = "获取系统内置参数")
    @PostMapping("/getSysParamsByKey")
    public String getSysParamsByKey(@ApiParam(name = "key", value = "键", required = true) @RequestParam(name = "key", required = true) String key) {
        log.info("获取系统内置参数");
        return sysParamsService.getSysParamsValueByKey(key);
    }

    @FeignSecurity
    @ApiOperation(value = "获取文本答案", notes = "获取文本答案")
    @PostMapping("/getTextAnswer")
    public String getTextAnswer(@ApiParam(name = "question", value = "问题", required = true) @RequestParam(name = "question", required = true) String question) {
        log.info("[getTextAnswer] 获取文本答案, question: {}", question);
        return languageModelService.getTextAnswer(question);
    }

    @FeignSecurity
    @ApiOperation(value = "获取密钥配置", notes = "获取密钥配置")
    @PostMapping("/getSecretConfig")
    public SecretConfig getSecretConfig(@ApiParam(name = "secretType", value = "密钥类型", required = true) @RequestParam(name = "secretType", required = true) String secretType,
                                        @ApiParam(name = "subSecretType", value = "子密钥类型", required = true) @RequestParam(name = "subSecretType", required = true) String subSecretType) {
        return secretConfigService.getSecretConfig(secretType, subSecretType);
    }
}
