package com.moxi.mogublog.utils;

import com.moxi.mogublog.utils.text.StrFormatter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.AntPathMatcher;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对字符串转换的一些操作
 *
 * @author 陌溪
 * @date 2020年9月20日10:56:45
 */
@Slf4j
public class StringUtils {

    private final static int NUM_32 = 32;
    //集群号
    private static int machineId = 1;
    private static final Pattern CAMLE_PATTERN = Pattern.compile("_(\\w)");
    private static final Pattern UNDER_LINE_PATTERN = Pattern.compile("[A-Z]");

    public static final String SPACE = " ";

    public static final String EMPTY = "";

    public static final String LF = "\n";

    public static final String CR = "\r";

    private static final String NULLSTR = "";

    /**
     * 下划线
     */
    private static final char SEPARATOR = '_';


    /**
     * 下划线转驼峰
     * @param str
     * @return
     */
    public static StringBuffer camel(StringBuffer str) {
        //利用正则删除下划线，把下划线后一位改成大写
        Matcher matcher = CAMLE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if (matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb);
        } else {
            return sb;
        }
        return camel(sb);
    }

    /**
     * 驼峰转下划线
     * @param str
     * @return
     */
    public static StringBuffer underLine(StringBuffer str) {
        Matcher matcher = UNDER_LINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if (matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb);
        } else {
            return sb;
        }
        return underLine(sb);
    }

    /**
     * 正则预编译
     */
    private static final Pattern pattern = Pattern.compile("(<.+?>)|(</.+?>)");

    /**
     * @param content 富文本
     * @Description: 去掉富文本标签
     */
    public static String dealContent(String content) {
        // 匹配标签
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            // 替换图片
            content = matcher.replaceAll("").replace(" ", "");
        }
        return content;
    }

    /**
     * 过滤富文本中的标签数据，只返回文本内容
     * @param htmlStr 富文本字符串
     * @return 纯文本内容
     */
    public static String filterHtmlTags(String htmlStr) {
        if (htmlStr == null || htmlStr.trim().length() == 0) {
            return "";
        }
        // 定义正则表达式，匹配HTML标签
        String regEx_html = "<[^>]+>";
        // 去掉所有的HTML标签
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        String textStr = m_html.replaceAll("");
        // 去掉空格、换行符等特殊字符
        textStr = textStr.replaceAll("&nbsp;", "");
        textStr = textStr.replaceAll("\\s*", "");
        return textStr;
    }

    /**
     * 把String 转换为 long
     *
     * @param str
     * @param defaultData
     * @return
     */
    public static long getLong(String str, Long defaultData) {
        Long lnum = defaultData;

        if (isEmpty(str)) {
            return lnum;
        }
        try {
            lnum = Long.valueOf(str.trim()).longValue();
        } catch (NumberFormatException e) {
            log.warn("把String 转换为 long======== " + str);
        }
        return lnum;

    }

    /**
     * 转换成Boolean类型
     *
     * @param str
     * @param defaultData
     * @return
     */
    public static Boolean getBoolean(String str, Boolean defaultData) {
        Boolean lnum = defaultData;

        if (isEmpty(str)) {
            return lnum;
        }
        try {
            lnum = Boolean.valueOf(str.trim()).booleanValue();
        } catch (NumberFormatException e) {
            log.warn("把String 转换为 long======== " + str);
        }
        return lnum;

    }

    /**
     * 把String转换成int数据
     *
     * @param str
     * @param defaultData
     * @return
     */
    public static int getInt(String str, Integer defaultData) {
        int inum = defaultData;
        if (isEmpty(str)) {
            return inum;
        }
        try {
            inum = Integer.valueOf(str.trim()).intValue();
        } catch (NumberFormatException e) {
            log.warn("把String转换成int数据========== " + str);
        }
        return inum;
    }

    /**
     * 把String转换成double数据
     *
     * @param str
     * @param defaultData
     * @return
     */
    public static double getDouble(String str, Double defaultData) {
        double dnum = defaultData;
        if (isEmpty(str)) {
            return dnum;
        }
        try {
            dnum = Double.valueOf(str.trim()).doubleValue();
        } catch (NumberFormatException e) {
            log.error("把String转换成double数据: {}", str);
        }
        return dnum;
    }

    /**
     * 把String转换成float数据
     *
     * @param str
     * @param defaultData
     * @return
     */
    public static float getFloat(String str, Float defaultData) {
        float dnum = defaultData;
        if (isEmpty(str)) {
            return dnum;
        }
        try {
            dnum = Float.valueOf(str.trim()).floatValue();
        } catch (NumberFormatException e) {
            log.error("把String转换成float数据: {}", str);
        }
        return dnum;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static Boolean isEmpty(String str) {
        return isNull(str) || str.length() <= 0;
    }

    /**
     * * 判断一个字符串是否为非空串
     *
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }


    /**
     * * 判断一个Collection是否为空， 包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Collection<?> coll) {
        return isNull(coll) || coll.isEmpty();
    }

    /**
     * * 判断一个Collection是否非空，包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * * 判断一个对象数组是否为空
     *
     * @param objects 要判断的对象数组
     *                * @return true：为空 false：非空
     */
    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
    }

    /**
     * * 判断一个对象数组是否非空
     *
     * @param objects 要判断的对象数组
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }


    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * * 判断一个对象是否非空
     *
     * @param object Object
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * * 判断一个对象是否是数组类型（Java基本型别的数组）
     *
     * @param object 对象
     * @return true：是数组 false：不是数组
     */
    public static boolean isArray(Object object) {
        return isNotNull(object) && object.getClass().isArray();
    }

    /**
     * 去空格
     */
    public static String trim(String str) {
        return (str == null ? "" : str.trim());
    }


    /**
     * 按code截取字符串
     *
     * @return
     */
    public static String[] split(String str, String code) {
        String[] split;
        if (isEmpty(str)) {
            split = null;
        } else {
            split = str.split(code);
        }
        return split;
    }

    /**
     * 切割字符串
     *
     * @param str
     * @return
     */
    public static List<String> split(String str) {
        List<String> list = new ArrayList<>();
        String[] splits = split(str, ",");
        for (int i = 0; i < splits.length; i++) {
            if (StringUtils.isNotEmpty(splits[i])) {
                list.add(splits[i]);
            }
        }
        return list;
    }

    /**
     * 把字符串按code 转换为List<Long>
     *
     * @param str
     * @return
     */
    public static List<Long> changeStringToLong(String str, String code) {
        String[] split = split(str, code);
        List<Long> lnums = new ArrayList<>();
        for (String s : split) {
            if (!isEmpty(s)) {
                long lnum = getLong(s, 0L);
                lnums.add(lnum);
            }

        }
        return lnums;
    }

    /**
     * 把字符串按code 转换为List<String>
     *
     * @param str
     * @return
     */
    public static List<String> changeStringToString(String str, String code) {
        List<String> lnums = new ArrayList<>();
        if (StringUtils.isEmpty(str)) {
            return lnums;
        }
        String[] split = split(str, code);
        for (String s : split) {
            //long lnum = getLong(s, 0l);
            if (StringUtils.isNotEmpty(s)) {
                lnums.add(s);
            }
        }
        return lnums;
    }


    /**
     * url 编码
     * @param url
     * @return
     */
    public static String encode(String url) {

        try {
            String encodeURL = URLEncoder.encode( url, "UTF-8" );
            return encodeURL;
        } catch (UnsupportedEncodingException e) {
            return "Issue while encoding" +e.getMessage();
        }
    }

    /**
     * 把字符串按code 转换为List<Long>
     *
     * @param str
     * @return
     */
    public static List<Integer> changeStringToInteger(String str, String code) {
        String[] split = split(str, code);
        List<Integer> inums = new ArrayList<>();
        for (String s : split) {
            int inum = getInt(s, 0);
            inums.add(inum);
        }
        return inums;
    }


    /**
     * 生成唯一订单号
     *
     * @return
     */
    public static String getOrderNumberByUUID() {

        int hashCodeV = UUID.randomUUID().toString().hashCode();
        //有可能是负数
        if (hashCodeV < 0) {
            hashCodeV = -hashCodeV;
        }
        String orderNumber = machineId + String.format("%015d", hashCodeV);
        return orderNumber;
    }

    /**
     * 生成唯一商户退款单号
     *
     * @return
     */
    public static String getOutRefundNoByUUID() {

        int hashCodeV = UUID.randomUUID().toString().hashCode();
        //有可能是负数
        if (hashCodeV < 0) {
            hashCodeV = -hashCodeV;
        }
        String out_refund_no = "BACK" + machineId + String.format("%015d", hashCodeV);
        return out_refund_no;

    }

    /**
     * 获取UUID，去掉了-
     *
     * @return
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        log.debug("获取32位的UUID的调试日志-->>" + uuid);
        return uuid;
    }

    /**
     * 获取雪花UID
     *
     * @return
     */
    public static Long getSnowflakeId() {
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0, 0);
        return snowflakeIdWorker.nextId();
    }

    /**
     * list小于0的数据就过滤了
     * 把list的数组变成1，3，4，5，6
     *
     * @param list
     * @param code
     * @return
     */
    public static String listToString(List<Long> list, String code) {
        String s = "";
        if (list == null || list.size() <= 0) {
            return s;
        }
        for (Long l : list) {
            if (l.longValue() > 0) {
                s = s + l + code;
            }
        }
        return s;
    }

    /**
     * 按code把list的数组转换成字符串
     *
     * @param list
     * @param code
     * @return
     */
    public static String listTranformString(List<String> list, String code) {
        String s = "";
        if (list == null || list.size() <= 0) {
            return s;
        }
        s = String.join(code, list);
        return s;
    }

    /**
     * 判断是否为非空字符串
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 校验uid列表，检查里面元素是否满足限定长度为32
     *
     * @param collection
     * @return
     */
    public static boolean checkUidList(Collection<String> collection) {
        if (collection.size() == 0) {
            return false;
        }
        for (String uid : collection) {
            if (uid.trim().length() != NUM_32) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否为空字符串
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断一个字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        try {
            //把字符串强制转换为数字
            Integer.valueOf(str);
            //如果是数字，返回True
            return true;
        } catch (Exception e) {
            //如果抛出异常，返回False
            return false;
        }
    }

    /**
     * 某个子串是否在字符串内
     *
     * @param str
     * @param searchChar
     * @return
     */
    public static boolean contains(String str, String searchChar) {
        if (isEmpty(str)) {
            return false;
        }
        return str.indexOf(searchChar) >= 0;
    }

    /**
     * 切割字符串
     *
     * @param str   字符串
     * @param start 开始
     * @return
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }
        // handle negatives, which means last n characters
        if (start < 0) {
            start = str.length() + start;
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return NULLSTR;
        }
        return str.substring(start);
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @param end   结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return NULLSTR;
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return NULLSTR;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }


    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params   参数值
     * @return 格式化后的文本
     */
    public static String format(String template, Object... params) {
        if (isEmpty(params) || isEmpty(template)) {
            return template;
        }
        return StrFormatter.format(template, params);
    }


    /**
     * 判断评论是否为垃圾评论（仅通过单一字符重复出现来判断，以后可以扩展更多的检测方法）
     *
     * @param content
     * @return
     */
    public static Boolean isCommentSpam(String content) {
        if (content == null) {
            return true;
        }
        char[] chars = content.toCharArray();
        // 最大重复次数
        Integer maxCount = 4;
        for (int a = 0; a < chars.length; a++) {
            Integer count = 1;
            for (int b = a; b < chars.length - 1; b++) {
                if (chars[b + 1] == chars[b]) {
                    count++;
                    // 判断字符重复的次数是否大于阈值
                    if (count >= maxCount) {
                        return true;
                    }
                    continue;
                } else {
                    break;
                }
            }
        }
        return false;
    }


    /**
     * 添加高亮
     * todo 分词粒度不够 导致部分符合keyword的词不被加红
     *
     * @param str
     * @param keyword
     * @return
     */
    public static String getHitCode(String str, String keyword) {
        if (StringUtils.isEmpty(keyword) || StringUtils.isEmpty(str)) {
            return str;
        }
        String startStr = "<span style = 'color:red'>";
        String endStr = "</span>";
        String lowerCaseStr = str.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();
        String[] lowerCaseArray = lowerCaseStr.split(lowerKeyword);
        /**
         * 1.判断关键字是否直接是搜索的内容，否者直接返回
         * 2.判断分割后收尾是否为空  当首尾为空 说明 关键词为标题  或关键词为标题的重复复制
         */
        if (str.equals(keyword) || lowerCaseArray.length == 0) {
            return startStr + str + endStr;
        }

        Boolean isEndWith = lowerCaseStr.endsWith(lowerKeyword);

        // 计算分割后的字符串位置
        Integer count = 0;
        List<Map<String, Integer>> list = new ArrayList<>();
        List<Map<String, Integer>> keyList = new ArrayList<>();
        for (int a = 0; a < lowerCaseArray.length; a++) {
            // 将切割出来的存储map
            Map<String, Integer> map = new HashMap<>();
            Map<String, Integer> keyMap = new HashMap<>();
            map.put("startIndex", count);
            Integer len = lowerCaseArray[a].length();
            count += len;
            map.put("endIndex", count);
            list.add(map);
            if (a < lowerCaseArray.length - 1 || isEndWith) {
                // 将keyword存储map
                keyMap.put("startIndex", count);
                count += keyword.length();
                keyMap.put("endIndex", count);
                keyList.add(keyMap);
            }
        }
        // 截取切割对象
        List<String> arrayList = new ArrayList<>();
        for (Map<String, Integer> item : list) {
            Integer start = item.get("startIndex");
            Integer end = item.get("endIndex");
            String itemStr = str.substring(start, end);
            arrayList.add(itemStr);
        }
        // 截取关键字
        List<String> keyArrayList = new ArrayList<>();
        for (Map<String, Integer> item : keyList) {
            Integer start = item.get("startIndex");
            Integer end = item.get("endIndex");
            String itemStr = str.substring(start, end > str.length() ? str.length() : end);
            keyArrayList.add(itemStr);
        }

        StringBuffer sb = new StringBuffer();
        for (int a = 0; a < arrayList.size(); a++) {
            sb.append(arrayList.get(a));
            if (a < arrayList.size() - 1 || isEndWith) {
                sb.append(startStr);
                sb.append(keyArrayList.get(a));
                sb.append(endStr);
            }
        }
        return sb.toString();
    }


    /**
     * 从html中提取文字
     *
     * @param inputString
     * @return
     */
    public static String html2Text(String inputString) {
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;
        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签
            textStr = htmlStr;
        } catch (Exception e) {
            log.error("Html2Text: " + e.getMessage());
        }
        //剔除空格行
        textStr = textStr.replaceAll("[ ]+", " ");
        textStr = textStr.replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "");
        textStr = textStr.replaceAll(" +", "");
        return textStr;// 返回文本字符串
    }


    /**
     * 校验一个字符是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字
     */
    public static boolean isChineseChar(char c) {
        try {
            return String.valueOf(c).getBytes("UTF-8").length > 1;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 验证字符串内容是否包含下列非法字符<br>
     * `~!#%^&*=+\\|{};:'\",<>/?○●★☆☉♀♂※¤╬の〆
     *
     * @param content 字符串内容
     * @return 't'代表不包含非法字符，otherwise代表包含非法字符。
     */
    public static char validateLegalString(String content) {
        String illegal = "`~!#%^&*=+\\|{};:'\",<>/?○●★☆☉♀♂※¤╬の〆";
        char isLegalChar = 't';
        Code1:
        for (int i = 0; i < content.length(); i++) {//L1定义代码块L1
            for (int j = 0; j < illegal.length(); j++) {
                if (content.charAt(i) == illegal.charAt(j)) {
                    isLegalChar = content.charAt(i);
                    break Code1;
                }
            }
        }
        return isLegalChar;
    }

    /**
     * 验证是否是汉字或者0-9、a-z、A-Z
     *
     * @param c 被验证的char
     * @return true代表符合条件
     */
    public static boolean isRightChar(char c) {
        return isChinese(c) || isWord(c);
    }

    /**
     * 校验某个字符是否是a-z、A-Z、_、0-9
     *
     * @param c 被校验的字符
     * @return true代表符合条件
     */
    public static boolean isWord(char c) {
        String regEx = "[\\w]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher("" + c);
        return m.matches();
    }

    /**
     * 判定输入的是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 校验String是否全是中文
     *
     * @param name 被校验的字符串
     * @return true代表全是汉字
     */
    public static boolean checkNameChese(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();//转换为数组
        for (int i = 0; i < name.length(); i++) {
            if (!isChinese(cTemp[i])) {//逐个判断是否为中文
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * 校验是否是正确的字符串
     *
     * @param name
     * @return
     */
    public static boolean checkNormalStr(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();//转换为数组
        for (int i = 0; i < name.length(); i++) {
            // 校验是否是正确的字符串
            if (!isRightChar(cTemp[i])) {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * 字符串转set
     *
     * @param str 字符串
     * @param sep 分隔符
     * @return set集合
     */
    public static final Set<String> str2Set(String str, String sep) {
        return new HashSet<String>(str2List(str, sep, true, false));
    }

    /**
     * 字符串转list
     *
     * @param str         字符串
     * @param sep         分隔符
     * @param filterBlank 过滤纯空白
     * @param trim        去掉首尾空白
     * @return list集合
     */
    public static final List<String> str2List(String str, String sep, boolean filterBlank, boolean trim) {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isEmpty(str)) {
            return list;
        }

        // 过滤空白字符串
        if (filterBlank && StringUtils.isBlank(str)) {
            return list;
        }
        String[] split = str.split(sep);
        for (String string : split) {
            if (filterBlank && StringUtils.isBlank(string)) {
                continue;
            }
            if (trim) {
                string = string.trim();
            }
            list.add(string);
        }

        return list;
    }


    /**
     * 驼峰转下划线命名
     */
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase = true;
        // 当前字符是否大写
        boolean curreCharIsUpperCase = true;
        // 下一字符是否大写
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
                sb.append(SEPARATOR);
            } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。 例如：HELLO_WORLD->HelloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String convertToCamelCase(String name) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!name.contains("_")) {
            // 不含下划线，仅将首字母大写
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = name.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 首字母大写
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * 驼峰式命名法 例如：user_name->userName
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     *
     * @param str  指定字符串
     * @param strs 需要检查的字符串数组
     * @return 是否匹配
     */
    public static boolean matches(String str, List<String> strs) {
        if (isEmpty(str) || isEmpty(strs)) {
            return false;
        }
        for (String pattern : strs) {
            if (isMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断url是否与规则配置:
     * ? 表示单个字符;
     * * 表示一层路径内的任意字符串，不可跨层级;
     * ** 表示任意层路径;
     *
     * @param pattern 匹配规则
     * @param url     需要匹配的url
     * @return
     */
    public static boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    /**
     * 数字左边补齐0，使之达到指定长度。注意，如果数字转换为字符串后，长度大于size，则只保留 最后size个字符。
     *
     * @param num  数字对象
     * @param size 字符串指定长度
     * @return 返回数字的字符串格式，该字符串为指定长度。
     */
    public static final String padl(final Number num, final int size) {
        return padl(num.toString(), size, '0');
    }

    /**
     * 字符串左补齐。如果原始字符串s长度大于size，则只保留最后size个字符。
     *
     * @param s    原始字符串
     * @param size 字符串指定长度
     * @param c    用于补齐的字符
     * @return 返回指定长度的字符串，由原字符串左补齐或截取得到。
     */
    public static final String padl(final String s, final int size, final char c) {
        final StringBuilder sb = new StringBuilder(size);
        if (s != null) {
            final int len = s.length();
            if (s.length() <= size) {
                for (int i = size - len; i > 0; i--) {
                    sb.append(c);
                }
                sb.append(s);
            } else {
                return s.substring(len - size, len);
            }
        } else {
            for (int i = size; i > 0; i--) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 从文本中获取AT的用户
     * @param message
     * @return
     */
    public static String getAtUserUid(String message) {
        String regex = "<a\\s+href=\"([^\"]*userUid=([^\"]+))\"\\s+target=\"_blank\">([^<]*)</a>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(2);
        }
        return "";
    }


    /**
     * 切割html字符串
     * @param htmlText
     * @param lineNumber
     * @return
     */
    public static String splitHtmlContent(String htmlText, int lineNumber) {
        Document doc = Jsoup.parse(htmlText);
        Elements paragraphs = doc.select("p"); // 根据实际情况选择合适的标签

        StringBuilder result = new StringBuilder();
        int count = 0;
        for (Element paragraph : paragraphs) {
            if (count >= lineNumber) {
                break;
            }
            result.append(paragraph.outerHtml()); // 获取整个段落的HTML代码
            count++;
        }
        return result.toString();
    }


    public static <T> T convertEvery(Object value, Class<T> targetType) {
        if (value == null) {
            return null;
        }

        // 如果value本身就是目标类型，则直接返回
        if (targetType.isInstance(value)) {
            return (T) value;
        }

        // 获取目标类型的泛型参数类型
        Type[] genericInterfaces = targetType.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                for (Type typeArgument : typeArguments) {
                    if (typeArgument instanceof Class) {
                        Class<?> typeArgumentClass = (Class<?>) typeArgument;
                        if (typeArgumentClass.isInstance(value)) {
                            return (T) value;
                        }
                    }
                }
            }
        }

        // 在这里根据不同的泛型参数类型进行相应的转换逻辑
        if (targetType == Integer.class) {
            return (T) Integer.valueOf(value.toString());
        } else if (targetType == Double.class) {
            return (T) Double.valueOf(value.toString());
        } else if (targetType == String.class) {
            return (T) value.toString();
        } else if (targetType == Long.class) {
            return (T) Long.valueOf(value.toString());
        }
        // 添加其他泛型参数类型的转换逻辑

        // 如果无法转换，则返回null或抛出异常，取决于您的需求
        return null;
    }

}
