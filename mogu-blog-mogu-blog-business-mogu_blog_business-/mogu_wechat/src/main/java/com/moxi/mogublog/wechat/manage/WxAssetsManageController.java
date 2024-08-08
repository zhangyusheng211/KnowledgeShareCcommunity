package com.moxi.mogublog.wechat.manage;

import com.moxi.mogublog.wechat.annotion.LoginVerify.LoginVerify;
import com.moxi.mogublog.wechat.common.global.SysConf;
import com.moxi.mogublog.wechat.common.utils.R;
import com.moxi.mogublog.wechat.form.MaterialFileDeleteForm;
import com.moxi.mogublog.wechat.service.WxAssetsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.material.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 微信公众号素材管理
 * 参考官方文档：https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/New_temporary_materials.html
 * 参考WxJava开发文档：https://github.com/Wechat-Group/WxJava/wiki/MP_永久素材管理
 */
@RestController
@RequestMapping("/manage/wxAssets")
@Api(tags = {"公众号素材-管理后台"})
public class WxAssetsManageController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    WxAssetsService wxAssetsService;

    /**
     * 获取素材总数
     *
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/materialCount")
    @ApiOperation(value = "文件素材总数")
    public R materialCount(HttpServletRequest request) throws WxErrorException {
        WxMpMaterialCountResult res = wxAssetsService.materialCount(request.getHeader(SysConf.AppId));
        return R.ok().put(res);
    }

    /**
     * 获取素材总数
     *
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/materialNewsInfo")
    @ApiOperation(value = "图文素材总数")
    public R materialNewsInfo(@CookieValue String appid, String mediaId) throws WxErrorException {
        WxMpMaterialNews res = wxAssetsService.materialNewsInfo(appid, mediaId);
        return R.ok().put(res);
    }


    /**
     * 根据类别分页获取非图文素材列表
     *
     * @param type
     * @param page
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/materialFileBatchGet")
    @ApiOperation(value = "根据类别分页获取非图文素材列表")
    public R materialFileBatchGet(@CookieValue String appid, @RequestParam(defaultValue = "image") String type,
                                  @RequestParam(defaultValue = "1") int page) throws WxErrorException {
        WxMpMaterialFileBatchGetResult res = wxAssetsService.materialFileBatchGet(appid, type, page);
        return R.ok().put(res);
    }

    /**
     * 分页获取图文素材列表
     *
     * @param page
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/materialNewsBatchGet")
    @ApiOperation(value = "分页获取图文素材列表")
    public R materialNewsBatchGet(@CookieValue String appid, @RequestParam(defaultValue = "1") int page) throws WxErrorException {
        WxMpMaterialNewsBatchGetResult res = wxAssetsService.materialNewsBatchGet(appid, page);
        return R.ok().put(res);
    }

    /**
     * 添加图文永久素材
     *
     * @param articles
     * @return
     * @throws WxErrorException
     */
    @PostMapping("/materialNewsUpload")
    @ApiOperation(value = "添加图文永久素材")
    public R materialNewsUpload(@CookieValue String appid, @RequestBody List<WxMpNewsArticle> articles) throws WxErrorException {
        if (articles.isEmpty()) {
            return R.error("图文列表不得为空");
        }
        WxMpMaterialUploadResult res = wxAssetsService.materialNewsUpload(appid, articles);
        return R.ok().put(res);
    }

    /**
     * 修改图文素材文章
     *
     * @param form
     * @return
     * @throws WxErrorException
     */
    @PostMapping("/materialArticleUpdate")
    @ApiOperation(value = "修改图文素材文章")
    public R materialArticleUpdate(@CookieValue String appid, @RequestBody WxMpMaterialArticleUpdate form) throws WxErrorException {
        if (form.getArticles() == null) {
            return R.error("文章不得为空");
        }
        wxAssetsService.materialArticleUpdate(appid, form);
        return R.ok();
    }

    /**
     * 添加多媒体永久素材
     *
     * @param file
     * @param fileName
     * @param mediaType
     * @return
     * @throws WxErrorException
     * @throws IOException
     */
    @PostMapping("/materialFileUpload")
    @ApiOperation(value = "添加多媒体永久素材")
    public R materialFileUpload(@CookieValue String appid, MultipartFile file, String fileName, String mediaType) throws WxErrorException, IOException {
        if (file == null) {
            return R.error("文件不得为空");
        }

        WxMpMaterialUploadResult res = wxAssetsService.materialFileUpload(appid, mediaType, fileName, file);
        return R.ok().put(res);
    }

    /**
     * 删除素材
     *
     * @param form
     * @return
     * @throws WxErrorException
     */
    @PostMapping("/materialDelete")
    @ApiOperation(value = "删除素材")
    public R materialDelete(@CookieValue String appid, @RequestBody MaterialFileDeleteForm form) throws WxErrorException {
        boolean res = wxAssetsService.materialDelete(appid, form.getMediaId());
        return R.ok().put(res);
    }

}
