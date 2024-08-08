package com.moxi.mogublog.commons.feign;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.config.feign.FeignConfiguration;
import com.moxi.mogublog.commons.entity.File;
import com.moxi.mogublog.commons.fallback.PictureFeignFallback;
import com.moxi.mogublog.commons.schema.GenerateM3u8FileRequest;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.vo.FileVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * mogu_picture相关接口
 *
 * @author 陌溪
 */
@FeignClient(name = "mogu-picture", configuration = FeignConfiguration.class)
public interface PictureFeignClient {

    /**
     * 获取文件的信息接口
     *
     * @param fileIds 图片uid
     * @param code    分隔符
     * @return
     */
    @RequestMapping(value = "/file/getPicture", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getPicture(@RequestParam("fileIds") String fileIds, @RequestParam("code") String code);

    /**
     * 通过URL List上传图片
     *
     * @param fileVO
     * @return
     */
    @RequestMapping(value = "/file/uploadPicsByUrl", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String uploadPicsByUrl(FileVO fileVO);


    /**
     * 初始化网盘容量大小
     * @param adminUid
     * @param maxStorageSize
     */
    @RequestMapping(value = "/storage/initStorageSize", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String initStorageSize(@RequestParam("adminUid") String adminUid, @RequestParam("maxStorageSize") Long maxStorageSize);

    /**
     * 调整网盘容量大小
     * @param adminUid
     * @param maxStorageSize
     */
    @RequestMapping(value = "/storage/editStorageSize", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String editStorageSize(@RequestParam("adminUid") String adminUid, @RequestParam("maxStorageSize") Long maxStorageSize);

    /**
     * 通过管理员uid列表获取存储信息
     * @param adminUidList
     * @return
     */
    @RequestMapping(value = "/storage/getStorageByAdminUid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getStorageByAdminUid(@RequestParam("adminUidList") List<String> adminUidList);

    /**
     * 获取文件列表
     * @param fileVO
     * @return
     */
    @RequestMapping(value = "/file/getFileList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getFileList( @RequestBody FileVO fileVO);

    /**
     * 获取文件Map
     * @param fileVO
     * @return
     */
    @RequestMapping(value = "/feign/getFileListMap", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    Map<String, File> getFileListMap(@RequestBody FileVO fileVO);

    /**
     *
     * @param fileUidList
     * @return
     */
    @RequestMapping(value = "/file/batchDeleteFile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String batchDeleteFile(@RequestBody List<String> fileUidList);

    /**
     * 获取文件上传路径
     * @return
     */
    @RequestMapping(value = "/file/getFileUploadPath", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getFileUploadPath();


    /**
     * 生成m3u8文件
     * @param req
     * @return
     */
    @RequestMapping(value = "/file/generateM3u8File", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    File generateM3u8File(@RequestBody GenerateM3u8FileRequest req);
}