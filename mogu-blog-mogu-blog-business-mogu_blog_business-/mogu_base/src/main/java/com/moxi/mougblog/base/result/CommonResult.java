package com.moxi.mougblog.base.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @auth: wly
 * @date: 2021/11/30
 * @descriptor:
 */
@Data
@Accessors(chain = true)
public class CommonResult<T> implements Serializable {

    /**
     * 返回码
     **/
    private int code;

    /**
     * 返回消息
     **/
    private String msg;

    /**
     * 返回数据对象
     **/
    private T data;

    private long total;

    public static <T> CommonResult<T> ok() {
        return ok(null);
    }

    public static <T> CommonResult<T> ok(String msg) {
        return ok(msg, null);
    }

    public static <T> CommonResult<T> ok(T data) {
        return ok(null, data);
    }

    public static <T> CommonResult<T> ok(String msg, T data) {
        return new CommonResult<T>().setCode(200).setMsg(msg).setData(data);
    }

    public static <T> CommonResult<T> error() {
        return error(500);
    }

    public static <T> CommonResult<T> error(int code) {
        return new CommonResult<T>().setCode(code);
    }

    public static <T> CommonResult<T> error(String msg) {
        return new CommonResult<T>().setCode(500).setMsg(msg);
    }

    public static <T> CommonResult<T> error(int code, String msg) {
        return new CommonResult<T>().setCode(code).setMsg(msg);
    }

}
