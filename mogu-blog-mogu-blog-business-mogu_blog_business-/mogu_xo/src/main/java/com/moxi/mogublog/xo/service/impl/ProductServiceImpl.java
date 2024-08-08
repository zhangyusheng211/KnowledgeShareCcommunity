package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.CommonCategory;
import com.moxi.mogublog.commons.entity.Product;
import com.moxi.mogublog.commons.schema.VipExtraConfig;
import com.moxi.mogublog.commons.vo.ProductVO;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.ProductMapper;
import com.moxi.mogublog.xo.mapper.ProductMapper;
import com.moxi.mogublog.xo.service.CommonCategoryService;
import com.moxi.mogublog.xo.service.ProductService;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 商品信息
 *
 * @author 陌溪
 * @date 2024年6月1日22:48:00
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProductServiceImpl extends SuperServiceImpl<ProductMapper, Product> implements ProductService {

    @Resource
    private ProductService productService;
    @Resource
    private CommonCategoryService commonCategoryService;
    @Resource
    private FileFeignUtil fileFeignUtil;


    @Override
    public IPage<Product> getPageList(ProductVO productVO) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(productVO.getKeyword()) && !StringUtils.isEmpty(productVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.NAME, productVO.getKeyword().trim()).or().like(SQLConf.SUMMARY, productVO.getKeyword().trim());
        }

        if (productVO.getPayType() != null) {
            queryWrapper.eq(SQLConf.PAY_TYPE, productVO.getPayType());
        }
        if (productVO.getChargeType() != null) {
            queryWrapper.eq(SQLConf.CHARGE_TYPE, productVO.getChargeType());
        }

        if (productVO.getIsPublish() != null) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, productVO.getIsPublish());
        }

        if (StringUtils.isNotEmpty(productVO.getCategoryUid())) {
            queryWrapper.eq(SQLConf.CATEGORY_UID, productVO.getCategoryUid());
        }

        if (StringUtils.isNotEmpty(productVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(productVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(productVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(productVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.IS_TOP);
            queryWrapper.orderByDesc(SQLConf.SORT, SQLConf.CREATE_TIME);
        }

        Page<Product> page = new Page<>();
        page.setCurrent(productVO.getCurrentPage());
        page.setSize(productVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        IPage<Product> pageList = productService.page(page, queryWrapper);
        List<Product> productList = pageList.getRecords();
        convertProductList(productList);
        pageList.setRecords(productList);
        return pageList;
    }

    @Override
    public String addProduct(ProductVO productVO) {
        Product product = new Product();
        BeanUtils.copyProperties(productVO, product, SysConf.STATUS);
        product.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editProduct(ProductVO productVO) {
        Product product = productService.getById(productVO.getUid());
        BeanUtils.copyProperties(productVO, product, SysConf.STATUS, SysConf.UID);
        product.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchProduct(List<ProductVO> productVOList) {
        if (productVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        productVOList.forEach(item -> {
            uids.add(item.getUid());
        });
        Collection<Product> blogSortList = productService.listByIds(uids);
        blogSortList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        boolean save = productService.updateBatchById(blogSortList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }

    @Override
    public void convertProductList(List<Product> productList) {
        List<String> productCategoryUidList = new ArrayList<>();
        List<String> fileUidList = new ArrayList<>();
        for (Product product : productList) {
            if (StringUtils.isNotEmpty(product.getCategoryUid())) {
                productCategoryUidList.add(product.getCategoryUid());
            }
            if (StringUtils.isNotEmpty(product.getCoverFileUid())) {
                fileUidList.add(product.getCoverFileUid());
            }
            if (StringUtils.isNotEmpty(product.getSlidesFileUidList())) {
                List<String> slidesFileUidList = StringUtils.changeStringToString(product.getSlidesFileUidList(), SysConf.FILE_SEGMENTATION);
                fileUidList.addAll(slidesFileUidList);
            }
        }

        Map<String, String> pictureMap = fileFeignUtil.fileUidToFileUrlMap(fileUidList);
        Map<String, CommonCategory> commonCategoryMap = new HashMap<>();
        if (productCategoryUidList.size() > 0) {
           List<CommonCategory> commonCategoryList = commonCategoryService.listByIds(productCategoryUidList);
            commonCategoryList.forEach(item -> {
                commonCategoryMap.put(item.getUid(), item);
            });
       }

        for (Product product : productList) {
            product.setCommonCategory(commonCategoryMap.get(product.getCategoryUid()));
            if (StringUtils.isNotEmpty(product.getCoverFileUid())) {
                product.setCoverPhotoUrl(pictureMap.get(product.getCoverFileUid()));
            }
            if (StringUtils.isNotEmpty(product.getSlidesFileUidList())) {
                List<String> slidesFileUidList = StringUtils.changeStringToString(product.getSlidesFileUidList(), SysConf.FILE_SEGMENTATION);
                List<String> slidesPhotoUrlList = new ArrayList<>();
                slidesFileUidList.forEach(item -> {
                    slidesPhotoUrlList.add(pictureMap.get(item));
                });
                product.setSlidesPhotoUrlList(slidesPhotoUrlList);
            }
        }
    }
}
