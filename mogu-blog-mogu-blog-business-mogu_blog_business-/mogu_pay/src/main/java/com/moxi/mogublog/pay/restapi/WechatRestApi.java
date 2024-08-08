package com.moxi.mogublog.pay.restapi;



import com.moxi.mogublog.commons.schema.WechatLoginUrlRequest;
import com.moxi.mogublog.commons.schema.WechatOauthInfoPkg.WechatOauthInfoResponse;
import com.moxi.mogublog.commons.schema.WechatOauthInfoPkg.WechatWebLoginResponse;
import com.moxi.mogublog.pay.global.SQLConf;
import com.moxi.mogublog.pay.model.bean.YunGouOSWxPayBean;
import com.moxi.mogublog.utils.HttpClientUtil;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;

import com.yungouos.pay.util.PaySignUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用的是YungouOs提供的微信登录能力
 */
@RestController
@RequestMapping("/wechat")
@Api(value = "微信登录模块", tags = {"微信登录模块"})
@Slf4j
public class WechatRestApi {

    @Resource
    private YunGouOSWxPayBean yunGouOSWxPayBean;

    @Value("${data.wechatCallbackUrl:''}")
    private String wechatCallbackUrl;

    @ApiOperation(value = "获取授权链接", notes = "获取授权链接")
    @PostMapping("/getOauthUrl")
    public String getOauthUrl(@RequestBody WechatLoginUrlRequest req) {
        if (StringUtils.isEmpty(wechatCallbackUrl)) {
            ResultUtil.errorWithMessage("后台暂未配置回调地址");
        }
        // 额外参数
        Map<String, Object> params = new HashMap<>();
        params.put("mch_id", yunGouOSWxPayBean.getMchId());
        params.put("callback_url", wechatCallbackUrl);
        params.put("sign", PaySignUtil.createSign(params, yunGouOSWxPayBean.getKey()));
        params.put("type", "open-url");
        Map<String, Object> otherParams = new HashMap<>();
        otherParams.put("state", req.getLoginKey());
        otherParams.put(SQLConf.SOURCE, req.getSource());

        params.put("params", JsonUtils.objectToJson(otherParams));
        String result = HttpClientUtil.DoPost("https://api.wx.yungouos.com/api/wx/getOauthUrl", params);
        Map<String, Object> resp = JsonUtils.jsonToMap(result);
        Object codeObj = resp.get("code");
        int code = ((Number) codeObj).intValue();
        if (code != 0) {
            return ResultUtil.errorWithMessage("获取登录链接失败");
        }
        String url = resp.get("data").toString();
        return ResultUtil.successWithData(url);
    }

    @ApiOperation(value = "获取微信登录链接", notes = "获取微信登录链接")
    @PostMapping(value="/getWeChatLoginUrl",produces=MediaType.APPLICATION_JSON_UTF8_VALUE,consumes = "application/json;charset=UTF-8")
    public String getWeChatLoginUrl(@RequestParam("code") String code) {
        if (StringUtils.isEmpty(wechatCallbackUrl)) {
            ResultUtil.errorWithMessage("后台暂未配置回调地址");
        }
        // 额外参数
        Map<String, Object> params = new HashMap<>();
        params.put("mch_id", yunGouOSWxPayBean.getMchId());
        params.put("callback_url", wechatCallbackUrl);
        params.put("sign", PaySignUtil.createSign(params, yunGouOSWxPayBean.getKey()));
        Map<String, Object> otherParams = new HashMap<>();
        otherParams.put("state", code);

        params.put("params", JsonUtils.objectToJson(otherParams));
        String result = HttpClientUtil.DoPost("https://api.wx.yungouos.com/api/wx/getWebLogin", params);
        WechatWebLoginResponse wechatWebLoginResponse = JsonUtils.jsonToPojo(result, WechatWebLoginResponse.class);
        if (wechatWebLoginResponse == null || wechatWebLoginResponse.getCode() != 0) {
            return ResultUtil.errorWithMessage("获取微信登录链接失败");
        }
        return ResultUtil.successWithData(wechatWebLoginResponse.getData());
    }

    @ApiOperation(value = "获取微信登录用户信息", notes = "获取微信登录用户信息")
    @PostMapping(value = "/getWechatOauthInfo",produces=MediaType.APPLICATION_JSON_UTF8_VALUE,consumes = "application/json;charset=UTF-8")
    public String getWechatOauthInfo(@RequestParam("code") String code) {
        if (StringUtils.isEmpty(code)) {
            ResultUtil.errorWithMessage("微信登录回调");
        }
        // 额外参数
        Map<String, Object> params = new HashMap<>();
        params.put("mch_id", yunGouOSWxPayBean.getMchId());
        params.put("code", code);
        params.put("sign", PaySignUtil.createSign(params, yunGouOSWxPayBean.getKey()));
        String result = HttpClientUtil.DoGet("https://api.wx.yungouos.com/api/wx/getOauthInfo", params);
        WechatOauthInfoResponse wechatOauthInfoResponse = JsonUtils.jsonToPojo(result, WechatOauthInfoResponse.class);
        if (wechatOauthInfoResponse == null || wechatOauthInfoResponse.getCode() != 0) {
            return ResultUtil.errorWithMessage("获取登录信息失败");
        }
        return ResultUtil.successWithData(wechatOauthInfoResponse.getData());
    }
}
