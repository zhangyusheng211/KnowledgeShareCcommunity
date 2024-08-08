package com.moxi.mogublog.commons.feign;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.config.feign.FeignConfiguration;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.schema.CreditsChangeRequest;
import com.moxi.mogublog.commons.schema.ProductRequest;
import com.moxi.mogublog.commons.vo.UserVO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后端服务feign远程调用
 *
 * @author 陌溪
 * @date 2020年1月21日22:19:10
 */

@FeignClient(name = "mogu-web", configuration = FeignConfiguration.class)
public interface WebFeignClient {

    /**
     * 获取系统配置信息
     */
    @RequestMapping(value = "/oauth/getSystemConfig", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getSystemConfig(@RequestParam("token") String token);

    /**
     * 获取搜索模式
     * @return
     */
    @RequestMapping(value = "/search/getSearchModel", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getSearchModel();

    @RequestMapping(value = "/content/getBlogByUid", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getBlogByUid(@RequestParam(name = "uid", required = false) String uid);

    @RequestMapping(value = "/content/getSameBlogByTagUid", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getSameBlogByTagUid(@RequestParam(name = "tagUid", required = true) String tagUid,
                                      @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                      @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize);

    @RequestMapping(value = "/content/getSameBlogByBlogUid", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getSameBlogByBlogUid(@RequestParam(name = "blogUid", required = true) String blogUid, Long currentPage, @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize);

    /**
     * 获取博客列表[包含内容]
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/index/getBlogBySearch", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getBlogBySearch(@RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                  @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize);


    /**
     * 微信公众号用户登录
     * @param openid
     * @param content
     * @param sign
     * @return
     */
    @RequestMapping(value = "/oauth/wechatUserLogin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String wechatUserLogin(@RequestParam(value = "openid") String openid, @RequestParam(value = "content") String content, @RequestParam(value = "sign") String  sign);

    /**
     * 公众号用户签到
     * @param openid
     * @param sign
     * @return
     */
    @RequestMapping(value = "/about/wechatUserSignIn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String wechatUserSignIn(@RequestParam(value = "openid") String openid, @RequestParam(value = "sign") String  sign);


    /**
     * 随机一题
     * @return
     */
    @RequestMapping(value = "/problem/randomProblem", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String randomProblem();

    /**
     * 随机一文
     * @return
     */
    @RequestMapping(value = "/createBlog/randomBlog", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String randomBlog();

    /**
     * 发送领域事件消息到前端
     * @param message
     * @param userUid
     * @return
     */
    @RequestMapping(value = "/index/sendDomainEventMessage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String sendDomainEventMessage(@RequestParam(value = "message")String message, @RequestParam(value = "userUid")String userUid);

    /**
     * 通过OpenID获取用户信息
     * @return
     */
    @RequestMapping(value = "/index/chatGPTReply", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String chatGPTReply(@RequestParam(value = "openid") String openid, @RequestParam(value = "message")String message);

    /**
     * 获取商品
     */
    @RequestMapping(value = "/webFeign/getProduct", method = RequestMethod.POST)
    String getProduct(ProductRequest productRequest);

    /**
     * 用户积分变更
     * @param creditsChangeRequest
     * @return
     */
    @RequestMapping(value = "/web/credits/creditsChange", method = RequestMethod.POST)
    String creditsChange(CreditsChangeRequest creditsChangeRequest);


    /**
     * 获取用户信息列表
     * @param userVO
     * @return
     */
    @RequestMapping(value = "/about/getUserListByPage", method = RequestMethod.POST)
    List<User> getUserListByPage(UserVO userVO);
}