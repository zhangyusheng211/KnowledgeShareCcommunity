package com.moxi.mogublog.utils;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 *
 * @author 陌溪
 * @date 2020年2月27日08:44:28
 */
@Component
public class RegexUtils {

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    public static List<String> match(String str, String regex) {
        if (null == str) {
            return null;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        List<String> list = new LinkedList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }


    /**
     * 判断字符串是否为URL
     * @param urls 需要判断的String类型url
     * @return true:是URL；false:不是URL
     */
    public static boolean isHttpUrl(String urls) {
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式

        Pattern pat = Pattern.compile(regex.trim());//对比
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();//判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }

    public static boolean checkByRegex(String str, String regex) {
        if (null == str) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * 判断字符串是否全是数字或全为字母或全中文
     * @param content 需要判断的String类型content
     * @return
     */
    public boolean isUseLess(String content) {
        boolean result = false;
        String regexNumber = "[1-9]\\d*";//设置正则表达式  纯数字
        String regexLetter = "^[a-zA-Z]+$";//设置正则表达式  纯字母
        String regexChinese = "[\\u4e00-\\u9fa5]";//设置正则表达式  纯汉字

        Pattern pat = Pattern.compile(regexNumber.trim());//对比
        Matcher mat = pat.matcher(content.trim());
        result = mat.matches();//判断是否匹配
        if (result){
            return true;
        }
        Pattern pat1 = Pattern.compile(regexLetter.trim());//对比
        Matcher mat1 = pat1.matcher(content.trim());
        result = mat1.matches();//判断是否匹配
        if (result){
            return true;
        }
        Pattern pat2 = Pattern.compile(regexChinese.trim());//对比
        Matcher mat2 = pat2.matcher(content.trim());
        result = mat2.matches();//判断是否匹配
        if (result){
            return true;
        }
        return false;
    }
}  

