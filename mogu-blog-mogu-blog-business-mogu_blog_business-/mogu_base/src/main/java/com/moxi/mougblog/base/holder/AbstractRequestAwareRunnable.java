package com.moxi.mougblog.base.holder;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * RequestAwareRunnable
 *
 * @author: 陌溪
 * @create: 2020-03-04-23:04
 */
public abstract class AbstractRequestAwareRunnable implements Runnable {

    private final RequestAttributes requestAttributes;
    private Thread thread;

    public AbstractRequestAwareRunnable() {
        // z 用于获取当前请求的上下文属性。它通常用于在非控制器类中获取当前 HTTP 请求的详细信息。
        this.requestAttributes = RequestContextHolder.getRequestAttributes();
        // z 当前线程
        this.thread = Thread.currentThread();
    }

    @Override
    public void run() {
        try {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            onRun();
        } finally {
            if (Thread.currentThread() != thread) {
                RequestContextHolder.resetRequestAttributes();
            }
            thread = null;
        }
    }

    /**
     * 启动
     */
    protected abstract void onRun();
}
