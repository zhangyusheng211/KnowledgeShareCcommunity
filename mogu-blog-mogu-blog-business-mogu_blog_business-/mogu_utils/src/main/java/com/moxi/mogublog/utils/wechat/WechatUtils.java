package com.moxi.mogublog.utils.wechat;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.moxi.mogublog.utils.HttpClientUtil;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 请求校验工具
 *
 * @author: 陌溪
 * @create: 2021-02-21-18:13
 */
public class WechatUtils {

    /**
     * 获取SessionKey
     * @param code
     * @return
     */
    public static JSONObject getSessionKeyOrOpenId(String appID, String secret, String code) {
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, String> requestUrlParam = new HashMap<>();
        // https://mp.weixin.qq.com/wxopen/devprofile?action=get_profile&token=164113089&lang=zh_CN
        //小程序appId
        requestUrlParam.put("appid", appID);
        //小程序secret
        requestUrlParam.put("secret", secret);
        //小程序端返回的code
        requestUrlParam.put("js_code", code);
        //默认参数
        requestUrlParam.put("grant_type", "authorization_code");
        //发送post请求读取调用微信接口获取openid用户唯一标识
        String result = HttpClientUtil.doPost(requestUrl, requestUrlParam);
        JSONObject jsonObject = JSON.parseObject(result);
        return jsonObject;
    }

    /**
     * 获取用户令牌
     * https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/mp-access-token/getAccessToken.html
     * @param appID
     * @param secret
     * @return
     */
    public static String getAccessToken(String appID, String secret) {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appID + "&secret=" + secret;
        String result = HttpRequest.get(url).execute().body();
        //##############################  开始  ############################
        Map<String, Object> data = JsonUtils.jsonToMap(result);
        if (data.get("access_token") != null) {
            return  data.get("access_token").toString();
        }
        return "";
    }

    /**
     * 生成访问链接
     * https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/qrcode-link/url-link/generateUrlLink.html
     * @return
     */
    public static String generateUrlLink(String accessToken, String pagePath, String ticket) {
        String body = null;
        String apiUrl = "https://api.weixin.qq.com/wxa/generate_urllink?access_token=" + accessToken;
        Map<String, String> map = new HashMap<>();
//        map.put("access_token", accessToken);
        map.put("path", pagePath);
//        map.put("query", "ticket=" + ticket);

        body = HttpClientUtil.doPost(apiUrl, map);
        if (StringUtils.isNotEmpty(body)) {
            JSONObject jsonObject = JSON.parseObject(body);
            if (jsonObject.getInteger("errcode") != 0) {
                return "";
            }
            String url_link = jsonObject.getString("url_link");
            return url_link;
        }
        return "";
    }


}