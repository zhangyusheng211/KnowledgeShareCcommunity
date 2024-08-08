package com.moxi.mogublog.picture.enums;

public enum UploadTypeEnum {
    LOCAL_UPLOAD("localUpload", "本地上传文件"),
    ALIOSS_UPLOAD("AliOssUpload", "阿里云上传文件"),

    MINIO_UPLOAD("MinioUpload", "Minio上传文件"),
    QINIU_UPLOAD("QiNiuUpload", "七牛云上传文件"),
    ;

    private final String code;
    private final String name;

    private UploadTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }
}