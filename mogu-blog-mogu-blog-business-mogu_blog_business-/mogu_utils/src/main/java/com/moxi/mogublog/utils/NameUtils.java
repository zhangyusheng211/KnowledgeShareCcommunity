package com.moxi.mogublog.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

public class NameUtils {

    public static String GetPinyinName(String name) {
        if (StringUtils.isEmpty(name)) {
            return "";
        }

        try {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            char[] chars = name.toCharArray();
            StringBuilder sb = new StringBuilder();
            for (char ch: chars) {
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch, format);
                for (String pinyin : pinyinArray) {
                    sb.append(pinyin);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(GetPinyinName("陌溪"));
    }
}
