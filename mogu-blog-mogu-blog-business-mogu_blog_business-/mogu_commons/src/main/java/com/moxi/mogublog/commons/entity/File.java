package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 文件实体类
 *
 * @author 陌溪
 * @date 2018-09-17
 */
@TableName("t_file")
@Data
public class File extends SuperEntity<File> {

    private static final long serialVersionUID = 1L;

    private String fileOldName;

    private Long fileSize;

    private String fileSortUid;

    /**
     * 图片扩展名
     */
    private String picExpandedName;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 图片url地址
     */
    private String picUrl;

    /**
     * 管理员Uid
     */
    private String adminUid;

    /**
     * 用户Uid
     */
    private String userUid;

    /**
     * 七牛云Url
     */
    private String qiNiuUrl;

    /**
     * Minio文件URL
     */
    private String minioUrl;
    /**
     * 阿里oss文件url
     */
    private String aliOssUrl;

    /**
     * 文件md5码
     */
    private String fileMd5;

    /**
     * m3u8 url地址
     */
    private String m3u8Url;

    /**
     * 媒资长度
     */
    private String videoLength;

    // 以下字段不存入数据库

    /**
     * 当前的url地址
     */
    @TableField(exist = false)
    private String pictureUrl;

    /**
     * 上传的用户
     */
    @TableField(exist = false)
    private User user;
}
