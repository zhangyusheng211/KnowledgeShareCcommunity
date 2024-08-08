package com.moxi.mougblog.base.enums;

/**
 * 热搜类型枚举类
 *
 * @author: 陌溪
 * @date: 2022年8月4日23:39:36
 */
public enum EHotSearchType {

    /**
     * 微博
     */
    WEI_BO("weibo", "https://www.coderutil.com/api/resou/v1/weibo"),

    /**
     * 知乎
     */
    ZHI_HU("zhihu", "https://www.coderutil.com/api/resou/v1/zhihu"),

    /**
     * 百度
     */
    BAI_DU("baidu", "https://www.coderutil.com/api/resou/v1/baidu"),

    /**
     * CSDN
     */
    CSDN("csdn", "https://www.coderutil.com/api/resou/v1/csdn"),

    /**
     * 头条
     */
    TOU_TIAO("toutiao", "https://www.coderutil.com/api/resou/v1/toutiao");

    private final String type;
    private final String url;

    EHotSearchType(String type, String url) {
        this.type = type;
        this.url = url;
    }

    // 根据type返回枚举类型
    public static EHotSearchType getType(String type) {
        for (EHotSearchType hotSearchType : EHotSearchType.values()) {
            if (hotSearchType.getType().equals(type)) {
                return hotSearchType;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
