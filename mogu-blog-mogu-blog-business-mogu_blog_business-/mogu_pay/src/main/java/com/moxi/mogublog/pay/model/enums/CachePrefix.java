package com.moxi.mogublog.pay.model.enums;

/**
 * 缓存前缀
 * Created by kingapex on 2018/3/19.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/19
 */
public enum CachePrefix {
    /**
     * 以用户Uid为后缀的结算
     */
    CHECKOUT_PARAM_ID_PREFIX;

    public String getPrefix() {
        return this.name() + ":";
    }
}
