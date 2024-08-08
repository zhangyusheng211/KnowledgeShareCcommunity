package com.moxi.mogublog.xo.manager;

import com.moxi.mogublog.commons.entity.Product;
import com.moxi.mogublog.commons.vo.ProductVO;
import com.moxi.mougblog.base.enums.EBusinessType;

/**
 * 转化详情接口
 *
 * @author 陌溪
 * @date 2024年6月3日22:52:25
 */
public interface ProductManager {

    /**
     * 获取转化详情
     *
     * @return
     */
    Product getProductDetail(ProductVO productVO);

}
