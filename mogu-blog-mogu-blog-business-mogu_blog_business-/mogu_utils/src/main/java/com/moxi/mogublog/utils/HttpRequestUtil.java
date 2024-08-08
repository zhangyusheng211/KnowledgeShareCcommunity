package com.moxi.mogublog.utils;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发送http请求的工具类
 *
 * @author 陌溪
 */
@Slf4j
public class HttpRequestUtil {

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("Content-type", "application/json; charset=UTF-8");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                log.info(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            result = new String(result.getBytes(), "UTF-8");
        } catch (Exception e) {
            log.info("发送GET请求出现异常！" + e);
            e.printStackTrace();
        } finally {
            // 使用finally块来关闭输入流
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        log.info("返回的结果：", result);
        return result;
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param, String token) {

        if (StringUtils.isEmpty(token)) {
            return sendGet(url, param);
        }

        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("Content-type", "application/x-java-serialized-object");
//            connection.setRequestProperty("accept", "*/*");
//            connection.setRequestProperty("connection", "Keep-Alive");
//            connection.setRequestProperty("user-agent",
//                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("orther-token", token);
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                log.info(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            result = new String(result.getBytes(), "UTF-8");
        } catch (Exception e) {
            log.info("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
//            conn.setRequestProperty("accept", "*/*");
//            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setRequestProperty("user-agent",
//                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            conn.setRequestMethod("POST");//默认get

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //post请求不能使用缓存
            conn.setUseCaches(false);

            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.info("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 访问OpenAI【需要到参数配置：ChatGPT配置 中设置对应的内容】
     * @param content
     * @return
     */
//    public static String askOpenAi(String content, String apiUrl, String apiKey) {
//        String body = null;
//        try {
//            Map<String, Object> map = new HashMap<>();
//            map.put("model", "gpt-3.5-turbo");
//            Map<String, String> messages = new HashMap<>();
//            messages.put("role", "user");
//            messages.put("content", content);
//            List<Map<String, String>> params = new ArrayList<>();
//            params.add(messages);
//            map.put("messages", params);
//            String json = JSON.toJSONString(map);
//
//            HttpResponse execute = HttpUtil.createPost(apiUrl).
//                    contentType("application/json")
//                    .body(json).timeout(200000)
//                    .header("Authorization", "Bearer " + apiKey)
//                    .execute();
//
//            body = execute.body();
//            if (StringUtils.isNotEmpty(body)) {
//                JSONObject jsonObject = JSON.parseObject(body);
//                JSONArray choices = jsonObject.getJSONArray("choices");
//                JSONObject textMap = choices.getJSONObject(0);
//                JSONObject contentMap = textMap.getJSONObject("message");
//                return contentMap.getString("content");
//            }
//        } catch (Exception e) {
//            log.error("askOpenAi error, body={}", body, e);
//        }
//        return null;
//    }

    public static String askOpenAi(String content, String apiUrl, String apiKey, String userUid) {
        String body = null;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("session_id", userUid);
            map.put("username", userUid);
            map.put("message", content);
            String json = JSON.toJSONString(map);

            HttpResponse execute = HttpUtil.createPost(apiUrl).
                    contentType("application/json")
                    .body(json).timeout(200000)
                    .execute();

            body = execute.body();
            if (StringUtils.isNotEmpty(body)) {
                JSONObject jsonObject = JSON.parseObject(body);
                if (!"DONE".equals(jsonObject.getString("result"))) {
                    return null;
                }
                JSONArray choices = jsonObject.getJSONArray("message");
                if (choices != null && choices.size() > 0) {
                    return choices.get(0).toString();
                }
            }
        } catch (Exception e) {
            log.error("askOpenAi error, body={}", body, e);
        }
        return null;
    }

//    public static String askOpenAi(String content) {
//        String body = null;
//        try {
//
//            // 先获取ChatID，然后再拿到答案
//            Map<String, Object> map = new HashMap<>();
//            map.put("user_fake_id", "sazbsorq6j7bs7i6");
//            map.put("session_id", StringUtils.getUUID());
//            map.put("question", content);
//            Map<String, Object> messages1 = new HashMap<>();
//            messages1.put("data", map);
//            String chatJson = JSON.toJSONString(messages1);
//            HttpResponse executeOne = HttpUtil.createPost("https://chatgptproxy.me/api/v1/chat/conversation").
//                    contentType("application/json")
//                    .body(chatJson).timeout(200000)
//                    .execute();
//            String chatBody = executeOne.body();
//            if (StringUtils.isEmpty(chatBody)) {
//                log.error("chatBody为空");
//                return "";
//            }
//            JSONObject jsonObject1 = JSON.parseObject(chatBody);
//            Object resp1 = jsonObject1.getJSONObject("resp_data");
//            Map<String, Object> result1 = JsonUtils.objectToMap(resp1);
//            if (result1 == null || result1.get("chat_id") == null) {
//                log.error("获取到ChatID为空: " + JsonUtils.objectToJson(chatBody));
//                return "";
//            }
//            int failCount = 0;
//            while (true) {
//                if (failCount > 15) {
//                    return "";
//                }
//                map.put("chat_id", result1.get("chat_id").toString());
//                Map<String, Object> messages = new HashMap<>();
//                messages.put("data", map);
//                String json = JSON.toJSONString(messages);
//                HttpResponse execute = HttpUtil.createPost("https://chatgptproxy.me/api/v1/chat/result").
//                        contentType("application/json")
//                        .body(json).timeout(200000)
//                        .execute();
//
//                body = execute.body();
//                if (StringUtils.isNotEmpty(body)) {
//                    JSONObject jsonObject = JSON.parseObject(body);
//                    Object resp = jsonObject.getJSONObject("resp_data");
//                    Map<String, Object> result = JsonUtils.objectToMap(resp);
//                    if (result == null) {
//                        return "";
//                    }
//                    // 继续请求
//                    Object status = result.get("status");
//                    if (status == null || Double.parseDouble(String.valueOf(status)) != 3) {
//                        failCount += 1;
//                        Thread.sleep(1000);
//                        continue;
//                    }
//                    if (result.get("answer") == null) {
//                        log.error("获取答案为空");
//                        return "";
//                    }
//                    return result.get("answer").toString();
//                }
//            }
//        } catch (Exception e) {
//            log.error("askOpenAi error, body={}", body, e);
//        }
//        log.error("获取答案失败");
//        return null;
//    }

  public static void main(String[] args) {
	   log.info(HttpRequestUtil.askOpenAi("你是谁呢", "http://150.158.156.198:8080/v1/chat", "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", "uid0000"));
	}
}
