package com.moxi.mogublog.commons.feign;

import com.moxi.mogublog.commons.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 后台服务feign远程调用
 *
 * @author 陌溪
 * @date 2020年1月21日22:19:10
 */

@FeignClient(name = "mogu-admin", configuration = FeignConfiguration.class)
public interface AdminFeignClient {


    /**
     * 获取系统配置信息
     */
    @RequestMapping(value = "/systemConfig/getSystemConfig", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getSystemConfig();


    /**
     * 根据用户名，获取能够访问的菜单栏
     */
    @RequestMapping(value = "/categoryMenu/getCategoryMenuByUserName", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getCategoryMenuByUserName(@RequestParam("userName") String userName);

}