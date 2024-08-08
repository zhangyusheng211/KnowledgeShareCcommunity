package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.annotion.FeignSecurity.FeignSecurity;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.schema.ProductListRequest;
import com.moxi.mogublog.commons.schema.ProductRequest;
import com.moxi.mogublog.commons.schema.ProductVO;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mougblog.base.enums.EChargeType;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用于内部Feign请求的接口
 *
 * @author 陌溪
 * @date 2022年6月16日22:57:34
 */
@RestController
@RefreshScope
@RequestMapping("/webFeign")
@Api(value = "用于内部Feign请求的接口", tags = {"用于内部Feign请求的接口"})
@Slf4j
public class WebFeignRestApi {

    @Autowired
    private BlogService blogService;

    @Autowired
    private MediaInfoService mediaInfoService;
    @Autowired
    private ResourceService resourceService;
    @javax.annotation.Resource
    private SubjectService subjectService;
    @Autowired
    private VipConfigService vipConfigService;
    @Autowired
    private ProductService productService;

    @FeignSecurity
    @ApiOperation(value = "获取商品", notes = "获取商品")
    @RequestMapping(value = "/getProduct", method = RequestMethod.POST)
    public String getProduct(@RequestBody ProductRequest productRequest) {
        EResourceType resourceType = EResourceType.getType(productRequest.getResourceType());
        switch (resourceType) {
            case BLOG: {
                Blog blog = blogService.getById(productRequest.getResourceUid());
                if (blog != null) {
                    return JsonUtils.objectToJson(convertBlog(blog));
                }
            }
            break;
            case RESOURCE: {
                Resource resource = resourceService.getById(productRequest.getResourceUid());
                if (resource != null) {
                    return JsonUtils.objectToJson(convertProduct(resource));
                }
            }
            break;
            case MEDIA: {
                MediaInfo mediaInfo = mediaInfoService.getById(productRequest.getResourceUid());
                if (mediaInfo != null) {
                    return JsonUtils.objectToJson(convertProduct(mediaInfo));
                }
            }
            break;
            case SUBJECT: {
                Subject subject = subjectService.getById(productRequest.getResourceUid());
                if (subject != null) {
                    return JsonUtils.objectToJson(convertProduct(subject));
                }
            }
            break;
            case VIP: {
                VipConfig vipConfig = vipConfigService.getById(productRequest.getResourceUid());
                if (vipConfig != null) {
                    return JsonUtils.objectToJson(convertProduct(vipConfig));
                }
            }
            break;
            case PRODUCT: {
                Product product = productService.getById(productRequest.getResourceUid());
                if (product != null) {
                    return JsonUtils.objectToJson(convertProduct(product));
                }
            }
            break;
        }
        return null;
    }

    @FeignSecurity
    @ApiOperation(value = "获取商品列表", notes = "获取商品")
    @RequestMapping(value = "/getProductList", method = RequestMethod.POST)
    public String getProductList(@RequestBody ProductListRequest productListRequest) {
        if (productListRequest.getResourceUidList().size() == 0) {
            throw new QueryException(MessageConf.PARAM_INCORRECT);
        }
        List<ProductVO> productVOList = new ArrayList<>();
        EResourceType eResourceType = EResourceType.getType(productListRequest.getResourceType());
        // 付费文章
        if (eResourceType == EResourceType.BLOG) {
            Collection<Blog> blogCollection = blogService.listByIds(productListRequest.getResourceUidList());
            for (Blog blog : blogCollection) {
                productVOList.add(convertBlog(blog));

            }
        } else if (eResourceType == EResourceType.MEDIA) {
            Collection<MediaInfo> mediaInfoCollection = mediaInfoService.listByIds(productListRequest.getResourceUidList());
            for (MediaInfo mediaInfo : mediaInfoCollection) {
                productVOList.add(convertProduct(mediaInfo));
            }
        }
        return ResultUtil.successWithData(productVOList);
    }

    public ProductVO convertBlog(Blog blog) {
        if (blog == null) {
            return null;
        }
        ProductVO productVO = new ProductVO();
        productVO.setName(blog.getTitle());
        productVO.setSummary(blog.getSummary());
        productVO.setPrice(blog.getPrice());
        productVO.setResourceType(EResourceType.BLOG.getType());
        productVO.setResourceUid(blog.getUid());
        productVO.setPhotoUrl(blog.getPhotoUrl());
//        productVO.setDiscountStartTime(blog.getDiscountStartTime());
//        productVO.setDiscountEndTime(blog.getDiscountEndTime());
        productVO.setDiscountPrice(blog.getPrice());
        productVO.setChargeType(EChargeType.Normal.getType());
        return productVO;
    }

    public ProductVO convertProduct(Subject subject) {
        if (subject == null) {
            return null;
        }
        subject = subjectService.convertSubject(subject);
        ProductVO productVO = new ProductVO();
        productVO.setName(subject.getSubjectName());
        productVO.setSummary(subject.getSummary());
        productVO.setPrice(subject.getPrice());
        productVO.setResourceType(EResourceType.SUBJECT.getType());
        productVO.setResourceUid(subject.getUid());
        if (subject.getPhotoList().size() > 0) {
            productVO.setPhotoUrl(subject.getPhotoList().get(0));
        }
//        productVO.setDiscountStartTime(mediaInfo.getDiscountStartTime());
//        productVO.setDiscountEndTime(mediaInfo.getDiscountEndTime());
        productVO.setDiscountPrice(subject.getPrice());
        productVO.setChargeType(EChargeType.Normal.getType());
        return productVO;
    }

    /**
     * 转化VIP
     * @param vipConfig
     * @return
     */
    public ProductVO convertProduct(VipConfig vipConfig) {
        if (vipConfig == null) {
            return null;
        }
        ProductVO productVO = new ProductVO();
        productVO.setName(vipConfig.getName());
        productVO.setSummary(vipConfig.getSummary());
        productVO.setPrice(vipConfig.getPrice());
        productVO.setResourceType(EResourceType.VIP.getType());
        productVO.setResourceUid(vipConfig.getUid());
        productVO.setDiscountStartTime(vipConfig.getDiscountStartTime());
        productVO.setDiscountEndTime(vipConfig.getDiscountEndTime());
        productVO.setDiscountPrice(vipConfig.getDiscountPrice());
        productVO.setChargeType(vipConfig.getChargeType());
        productVO.setPayType(vipConfig.getPayType());
        return productVO;
    }

    /**
     * 转化普通商品
     * @param product
     * @return
     */
    public ProductVO convertProduct(Product product) {
        if (product == null) {
            return null;
        }
        ProductVO productVO = new ProductVO();
        productVO.setName(product.getName());
        productVO.setSummary(product.getSummary());
        productVO.setPrice(product.getPrice());
        productVO.setResourceType(EResourceType.PRODUCT.getType());
        productVO.setResourceUid(product.getUid());
        productVO.setDiscountStartTime(product.getDiscountStartTime());
        productVO.setDiscountEndTime(product.getDiscountEndTime());
        productVO.setDiscountPrice(product.getDiscountPrice());
        productVO.setChargeType(product.getChargeType());
        productVO.setPayType(product.getPayType());
        return productVO;
    }


    /**
     * 课程转化商品
     *
     * @param mediaInfo
     * @return
     */
    public ProductVO convertProduct(MediaInfo mediaInfo) {
        if (mediaInfo == null) {
            return null;
        }
        ProductVO productVO = new ProductVO();
        productVO.setName(mediaInfo.getTitle());
        productVO.setSummary(mediaInfo.getSummary());
        productVO.setPrice(mediaInfo.getPrice().intValue());
        productVO.setResourceType(EResourceType.MEDIA.getType());
        productVO.setResourceUid(mediaInfo.getUid());
        productVO.setPhotoUrl(mediaInfo.getImages());
//        productVO.setDiscountStartTime(mediaInfo.getDiscountStartTime());
//        productVO.setDiscountEndTime(mediaInfo.getDiscountEndTime());
//        productVO.setDiscountPrice(mediaInfo.getDiscountPrice());
        productVO.setChargeType(EChargeType.Free.getType());
        return productVO;
    }

    /**
     * 网盘资源转化商品
     *
     * @param resource
     * @return
     */
    public ProductVO convertProduct(Resource resource) {
        if (resource == null) {
            return null;
        }
        ProductVO productVO = new ProductVO();
        productVO.setName(resource.getName());
        productVO.setSummary(resource.getSummary());
        productVO.setPrice(resource.getPrice().intValue());
        productVO.setResourceType(EResourceType.RESOURCE.getType());
        productVO.setResourceUid(resource.getUid());
        productVO.setPhotoUrl(resource.getPhotoUrl());
        productVO.setDiscountStartTime(resource.getDiscountStartTime());
        productVO.setDiscountEndTime(resource.getDiscountEndTime());
        productVO.setDiscountPrice(resource.getDiscountPrice());
        productVO.setChargeType(resource.getChargeType());
        return productVO;
    }

    /**
     * 构建会员
     *
     * @return
     */
    public ProductVO convertVip() {
        ProductVO productVO = new ProductVO();
        productVO.setName("终身会员");
        productVO.setSummary("终身会员");
        productVO.setPrice(38800);
        productVO.setResourceType(EResourceType.VIP.getType());
        productVO.setResourceUid("");
        return productVO;
    }


}
