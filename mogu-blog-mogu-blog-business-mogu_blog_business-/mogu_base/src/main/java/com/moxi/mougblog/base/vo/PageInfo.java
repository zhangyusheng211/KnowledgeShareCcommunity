package com.moxi.mougblog.base.vo;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.utils.SqlUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.validator.Messages;
import com.moxi.mougblog.base.validator.annotion.LongNotNull;
import com.moxi.mougblog.base.validator.group.GetList;
import lombok.Data;

/**
 * PageVO  用于分页
 *
 * @author: 陌溪
 * @create: 2019-12-03-22:38
 */
@Data
public class PageInfo<T> {

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 当前页
     */
    @LongNotNull(groups = {GetList.class}, message = Messages.PAGE_NOT_NULL)
    private Long currentPage;

    /**
     * 页大小
     */
    @LongNotNull(groups = {GetList.class}, message = Messages.SIZE_NOT_NULL)
    private Long pageSize;

    /**
     * 排序列
     */
    private String orderByColumn;

    /**
     * 排序的方向desc或者asc
     */
    private String isAsc = "asc";

    public Boolean isAsc() {
        if (Constants.ASC.equals(isAsc)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * mybatis-plus 分页插件排序对象创建
     */
    public OrderItem creatOrderBy() {
        if (StringUtils.isEmpty(orderByColumn)) {
            return null;
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setColumn(StringUtils.underLine(new StringBuffer(SqlUtil.escapeOrderBySql(orderByColumn))).toString());
        orderItem.setAsc(isAsc());
        return orderItem;
    }

    /**
     * mybatis-plus 分页对象创建
     *
     * @return
     */
    public Page<T> creatPage() {
        Page<T> page = new Page<T>(currentPage, pageSize);
        OrderItem orderItem = creatOrderBy();
        if (orderItem != null) {
            page.addOrder(orderItem);
        }

        return page;
    }

}
