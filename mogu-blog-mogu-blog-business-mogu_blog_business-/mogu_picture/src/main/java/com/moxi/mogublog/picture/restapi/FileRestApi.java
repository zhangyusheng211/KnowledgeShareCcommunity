package com.moxi.mogublog.picture.restapi;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.annotion.FeignSecurity.FeignSecurity;
import com.moxi.mogublog.commons.entity.File;
import com.moxi.mogublog.commons.entity.SystemConfig;
import com.moxi.mogublog.commons.schema.GenerateM3u8FileRequest;
import com.moxi.mogublog.picture.entity.QiNiuCallback.QiNiuAuditCallback;
import com.moxi.mogublog.picture.service.FileService;
import com.moxi.mogublog.picture.util.FeignUtil;
import com.moxi.mogublog.picture.util.MinioUtil;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.vo.FileVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传接口 【总的文件接口，需要调用本地文件、七牛云、Minio上传服务】
 *
 * @author 陌溪
 * @date 2020年10月21日15:32:03
 */
@RestController
@RequestMapping("/file")
@Api(value = "文件服务相关接口", tags = {"文件服务相关接口"})
@Slf4j
public class FileRestApi {

    @Resource
    private FeignUtil feignUtil;
    @Resource
    private FileService fileService;
    /**
     * 本地图片上传路径
     */
    @Value(value = "${file.upload.path}")
    private String path;


    @ApiOperation(value = "获取文件上传路径", notes = "获取文件上传路径")
    @RequestMapping(value = "/getFileUploadPath", method = RequestMethod.POST)
    public String getFileUploadPath() {
        return path;
    }

    @ApiOperation(value = "截图上传", notes = "截图上传")
    @RequestMapping(value = "/cropperPicture", method = RequestMethod.POST)
    public String cropperPicture(@RequestParam("file") MultipartFile file) {
        return fileService.cropperPicture(file);
    }

    @ApiOperation(value = "获取用户最近上传的文件", notes = "获取用户最近上传的文件")
    @RequestMapping(value = "/getUserRecentlyUploadFile", method = RequestMethod.POST)
    public String getUserRecentlyUploadFile(@RequestBody FileVO fileVO) {
        return ResultUtil.successWithData(fileService.getUserRecentlyUploadFile(fileVO));
    }

    /**
     * 获取文件的信息接口
     * fileIds 获取文件信息的ids
     * code ids用什么分割的，默认“,”
     *
     * @return
     */
    @ApiOperation(value = "通过fileIds获取图片信息接口", notes = "获取图片信息接口")
    @GetMapping("/getPicture")
    public String getPicture(
            @ApiParam(name = "fileIds", value = "文件ids", required = false) @RequestParam(name = "fileIds", required = false) String fileIds,
            @ApiParam(name = "code", value = "切割符", required = false) @RequestParam(name = "code", required = false) String code) {
        return fileService.getPicture(fileIds, code);
    }

    /**
     * 多文件上传
     * 上传图片接口   传入 userId sysUserId ,有那个传哪个，记录是谁传的,
     * projectName 传入的项目名称如 base 默认是base
     * sortName 传入的模块名， 如 admin，user ,等，不在数据库中记录的是不会上传的
     *
     * @return
     */
    @ApiOperation(value = "多图片上传接口", notes = "多图片上传接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filedatas", value = "文件数据", required = true),
            @ApiImplicitParam(name = "userUid", value = "用户UID", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sysUserId", value = "管理员UID", required = false, dataType = "String"),
            @ApiImplicitParam(name = "projectName", value = "项目名", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sortName", value = "模块名", required = false, dataType = "String")
    })
    @PostMapping("/pictures")
    public synchronized Object uploadPics(HttpServletRequest request, List<MultipartFile> filedatas) {
        // 获取系统配置文件
        SystemConfig systemConfig = feignUtil.getSystemConfig();
        return fileService.batchUploadFile(request, filedatas, systemConfig);
    }

    /**
     * 视频文件上传
     * 上传视频接口   传入 userId sysUserId ,有那个传哪个，记录是谁传的,
     * projectName 传入的项目名称如 base 默认是base
     * sortName 传入的模块名， 如 admin，user ,等，不在数据库中记录的是不会上传的
     *
     * @return
     */
    @ApiOperation(value = "视频文件上传", notes = "视频文件上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filedatas", value = "文件数据", required = true),
            @ApiImplicitParam(name = "userUid", value = "用户UID", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sysUserId", value = "管理员UID", required = false, dataType = "String"),
            @ApiImplicitParam(name = "projectName", value = "项目名", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sortName", value = "模块名", required = false, dataType = "String")
    })
    @PostMapping("/uploadVideo")
    public synchronized Object uploadVideo(HttpServletRequest request, MultipartFile file) {
        // 获取系统配置文件
        SystemConfig systemConfig = feignUtil.getSystemConfig();
        return fileService.uploadVideoFile(request, file, systemConfig);
    }

    /**
     * 通过URL将图片上传到自己服务器中【主要用于Github和Gitee的头像上传】
     *
     * @param fileVO
     * @param result
     * @return
     */
    @ApiOperation(value = "通过URL上传图片", notes = "通过URL上传图片")
    @PostMapping("/uploadPicsByUrl")
    public Object uploadPicsByUrl(@Validated({GetList.class}) @RequestBody FileVO fileVO, BindingResult result) {
        return fileService.uploadPictureByUrl(fileVO);
    }


    /**
     * Ckeditor图像中的图片上传
     *
     * @return
     */
    @ApiOperation(value = "Ckeditor图像中的图片上传", notes = "Ckeditor图像中的图片上传")
    @RequestMapping(value = "/ckeditorUploadFile", method = RequestMethod.POST)
    public Object ckeditorUploadFile(HttpServletRequest request) {
        return fileService.ckeditorUploadFile(request);
    }

    /**
     * Ckeditor复制的图片上传
     *
     * @return
     */
    @ApiOperation(value = "复制的图片上传", notes = "复制的图片上传")
    @RequestMapping(value = "/ckeditorUploadCopyFile", method = RequestMethod.POST)
    public synchronized Object ckeditorUploadCopyFile() {
        return fileService.ckeditorUploadCopyFile();
    }

    /**
     * Ckeditor工具栏 “插入\编辑超链接”的文件上传
     *
     * @return
     */
    @ApiOperation(value = "工具栏的文件上传", notes = "工具栏的文件上传")
    @RequestMapping(value = "/ckeditorUploadToolFile", method = RequestMethod.POST)
    public Object ckeditorUploadToolFile(HttpServletRequest request) {
        return fileService.ckeditorUploadToolFile(request);
    }

    /**
     * 获取文件列表【用于素材管理】
     * @param fileVO
     * @return
     */
    @FeignSecurity
    @ApiOperation(value = "获取文件列表", notes = "获取文件列表")
    @RequestMapping(value = "/getFileList", method = RequestMethod.POST)
    public String getFileList(@Validated({GetList.class}) @RequestBody FileVO fileVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(fileService.getFileList(fileVO));
    }

    @FeignSecurity
    @ApiOperation(value = "批量删除源文件", notes = "批量删除源文件")
    @RequestMapping(value = "/batchDeleteFile", method = RequestMethod.POST)
    public String batchDeleteFile(@RequestBody List<String> fileUidList) {
        log.info("批量删除源文件, fileUidList: {}", JsonUtils.objectToJson(fileUidList));
        return ResultUtil.successWithData(fileService.batchDeleteFile(fileUidList));
    }


    @ApiOperation(value = "七牛云通知审核回调", notes = "七牛云通知审核回调")
    @RequestMapping(value = "/qiNiuAuditCallback", method = RequestMethod.POST)
    public String qiNiuAuditCallback(@RequestBody QiNiuAuditCallback qiNiuAuditCallback) {
        log.info("[qiNiuAuditCallback] 七牛云审核回调， request: {}", JsonUtils.objectToJson(qiNiuAuditCallback));
        return fileService.qiNiuAuditCallback(qiNiuAuditCallback);
    }

    @ApiOperation(value = "生成m3u8文件", notes = "生成m3u8文件")
    @RequestMapping(value = "/generateM3u8File", method = RequestMethod.POST)
    public File generateM3u8File(@RequestBody GenerateM3u8FileRequest req) {
        log.info("[generateM3u8File] 开始生成M3U8文件， GenerateM3u8FileRequest: {}", JsonUtils.objectToJson(req));
        return fileService.generateM3u8File(req);
    }

}

