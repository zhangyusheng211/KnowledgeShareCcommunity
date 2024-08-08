package com.moxi.mogublog.xo.utils;

import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.commons.schema.FileDto;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mougblog.base.global.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 文件服务Feign调用工具类
 *
 * @author 陌溪
 * @date 2021年12月21日21:29:07
 */
@Slf4j
@Component
public class FileFeignUtil {

    @Resource
    private PictureFeignClient pictureFeignClient;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private WebUtil webUtil;

    /**
     * 获取图片Map集合
     *
     * @return
     */
    public Map<String, String> fileUidToFileUrlMap(List<String> fileUidList) {
        Map<String, String> fileMap = new HashMap<>();
        final StringBuffer noCacheFileUids = new StringBuffer();
        for (String fileUid : fileUidList) {
            String fileUrl = redisUtil.get(RedisConf.FILE_UID_TO_URL + Constants.DELIMITER_COLON + fileUid);
            if (StringUtils.isNotEmpty(fileUrl)) {
                fileMap.put(fileUid, fileUrl);
            } else {
                noCacheFileUids.append(fileUid).append(SysConf.FILE_SEGMENTATION);
            }
        }
        // 解析map中的数据
        if (StringUtils.isNotEmpty(noCacheFileUids.toString())) {
            String pictureList = this.pictureFeignClient.getPicture(noCacheFileUids.toString(), SysConf.FILE_SEGMENTATION);
            List<Map<String, Object>> picList = webUtil.getPictureMap(pictureList);
            for (Map<String, Object> item : picList) {
                String fileUid = item.get(SQLConf.UID).toString();
                String fileUrl = item.get(SQLConf.URL).toString();
                fileMap.put(fileUid, fileUrl);
                // 将记录存储在redis中，设置过期时间10分钟
                if (StringUtils.isNotEmpty(fileUrl)) {
                    redisUtil.setEx(RedisConf.FILE_UID_TO_URL + Constants.DELIMITER_COLON + fileUid, fileUrl, 10, TimeUnit.MINUTES);
                }
            }
        }
        return fileMap;
    }

    /**
     * 获取m3u8文件集合
     * @param fileUidList
     * @return
     */
    public Map<String, FileDto> fileUidToFileDtoMap(List<String> fileUidList) {
        Map<String, FileDto> fileMap = new HashMap<>();
        final StringBuffer noCacheFileUids = new StringBuffer();
        for (String fileUid : fileUidList) {
            String fileDtoJson = redisUtil.get(RedisConf.FILE_UID_TO_FILE_DTO + Constants.DELIMITER_COLON + fileUid);
            if (StringUtils.isEmpty(fileDtoJson)) {
                noCacheFileUids.append(fileUid).append(SysConf.FILE_SEGMENTATION);
                continue;
            }
            // 格式化缓存对象
            FileDto fileDto = JsonUtils.jsonToPojo(fileDtoJson, FileDto.class);
            if (fileDto == null) {
                noCacheFileUids.append(fileUid).append(SysConf.FILE_SEGMENTATION);
                continue;
            }
            fileMap.put(fileUid, fileDto);
        }
        // 解析map中的数据
        if (StringUtils.isNotEmpty(noCacheFileUids.toString())) {
            String pictureList = this.pictureFeignClient.getPicture(noCacheFileUids.toString(), SysConf.FILE_SEGMENTATION);
            List<Map<String, Object>> picList = webUtil.getPictureMap(pictureList);
            for (Map<String, Object> item : picList) {
                String fileUid = item.get(SysConf.UID).toString();
                FileDto fileDto = new FileDto();
                fileDto.setFileUid(fileUid);
                fileDto.setUrl(item.get(SysConf.URL).toString());
                if (item.get(SysConf.M3U8_URL) != null) {
                    fileDto.setM3u8Url(item.get(SysConf.M3U8_URL).toString());
                }

                redisUtil.setEx(RedisConf.FILE_UID_TO_FILE_DTO + Constants.DELIMITER_COLON + fileUid, JsonUtils.objectToJson(fileDto), 10, TimeUnit.MINUTES);
            }
        }
        return fileMap;
    }

    /**
     * 将文件uid转换成url
     *
     * @param fileUid
     * @return
     */
    public String fileUidToFileUrl(String fileUid) {
        String fileUrl = "";
        List<String> list = new ArrayList<>();
        list.add(fileUid);
        Map<String, String> map = this.fileUidToFileUrlMap(list);
        if (map.get(fileUid) != null) {
            fileUrl = map.get(fileUid);
        }
        return fileUrl;
    }

}