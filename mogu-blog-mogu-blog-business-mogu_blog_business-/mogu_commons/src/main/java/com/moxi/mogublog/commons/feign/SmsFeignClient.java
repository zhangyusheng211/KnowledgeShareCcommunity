package com.moxi.mogublog.commons.feign;

import com.moxi.mogublog.commons.config.feign.FeignConfiguration;
import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.SecretConfig;
import com.moxi.mogublog.commons.entity.SysParams;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.schema.CreditsChangeRequest;
import com.moxi.mogublog.commons.schema.ProductRequest;
import com.moxi.mogublog.commons.vo.AdminVO;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mougblog.base.enums.EBusinessType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 后端服务feign远程调用
 *
 * @author 陌溪
 * @date 2023年6月20日23:27:24
 */

@FeignClient(name = "mogu-sms", configuration = FeignConfiguration.class)
public interface SmsFeignClient {


    /**
     * 获取用户信息列表
     * @param userVO
     * @return
     */
    @RequestMapping(value = "/smsFeign/getUserListByPage", method = RequestMethod.POST)
    List<User> getUserListByPage(UserVO userVO);

    /**
     * 获取管理员信息列表
     * @param adminVO
     * @return
     */
    @RequestMapping(value = "/smsFeign/getAdminListByPage", method = RequestMethod.POST)
    List<Admin> getAdminListByPage(AdminVO adminVO);

    /**
     * 获取用户钱包金额
     * @return
     */
    @RequestMapping(value = "/smsFeign/getUserAmount", method = RequestMethod.POST)
    String getUserAmount(@RequestParam(value = "userUid") String userUid);

    /**
     * 更新用户余额
     * @param userUid
     * @param amount
     * @return
     */
    @PostMapping("/smsFeign/updateAmountByUserUid")
    String updateAmountByUserUid(@RequestParam(value = "userUid")String userUid, @RequestParam(value = "amount") Long amount);

    /**
     * 根据key获取系统参数
     * @param key
     * @return
     */
    @PostMapping("/smsFeign/getSysParamsByKey")
    String getSysParamsByKey(@RequestParam(value = "key") String key);

    /**
     * 获取文本答案
     * @param question
     * @return
     */
    @PostMapping("/smsFeign/getTextAnswer")
    String getTextAnswer(@RequestParam(value = "question") String question);

    /**
     * 获取密钥配置
     * @return
     */
    @PostMapping("/smsFeign/getSecretConfig")
    SecretConfig getSecretConfig(@RequestParam(value = "secretType") String secretType, @RequestParam(value = "subSecretType") String subSecretType);
}