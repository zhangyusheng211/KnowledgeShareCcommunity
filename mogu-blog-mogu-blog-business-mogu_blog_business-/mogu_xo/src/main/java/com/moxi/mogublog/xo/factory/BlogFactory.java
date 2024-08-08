package com.moxi.mogublog.xo.factory;

import com.moxi.mogublog.utils.SpringUtils;
import com.moxi.mogublog.xo.manager.BlogManager;
import com.moxi.mougblog.base.enums.EContributeSource;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;

/**
 * @author 遇见
 */
public class BlogFactory {

    /**
     * 根据source获取BlogManager
     *
     * @param source
     * @return
     */
    public static BlogManager build(String source) {
        if (EContributeSource.ADMIN_PUBLISH.equals(source)) {
            return SpringUtils.getBean("adminBlogManager", BlogManager.class);
        } else if (EContributeSource.USER_PUBLISH.equals(source)) {
            return SpringUtils.getBean("userBlogManager", BlogManager.class);
        } else {
            throw new BusinessException("服务端标识异常");
        }
    }
}
