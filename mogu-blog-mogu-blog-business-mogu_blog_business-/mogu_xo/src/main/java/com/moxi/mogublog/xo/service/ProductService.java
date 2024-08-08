package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Product;
import com.moxi.mogublog.commons.vo.ProductVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 商品表服务类
 *
 * @author 陌溪
 * @date 2024年6月1日21:54:29
 */
public interface ProductService extends SuperService<Product> {
    /**
     * 获取商品列表
     *
     * @param productVO
     * @return
     */
    IPage<Product> getPageList(ProductVO productVO);

    /**
     * 新增商品
     *
     * @param productVO
     */
    String addProduct(ProductVO productVO);

    /**
     * 编辑商品
     *
     * @param productVO
     */
    String editProduct(ProductVO productVO);

    /**
     * 批量删除商品
     *
     * @param productVOList
     */
    String deleteBatchProduct(List<ProductVO> productVOList);

    /**
     * 转化商品信息
     * @param productList
     * @return
     */
    void convertProductList(List<Product> productList);
}
