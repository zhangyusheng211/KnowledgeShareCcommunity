<template>
    <div id="table" class="app-container calendar-list-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;">
            <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit" size="small"
                       v-permission="'/picture/add'">添加
            </el-button>
            <el-button class="button" type="primary" @click="checkAll()" size="small" icon="el-icon-refresh">
                {{ chooseTitle }}
            </el-button>
            <el-button class="filter-item" type="danger" @click="handleDeleteBatch" size="small" icon="el-icon-delete"
                       v-permission="'/picture/delete'">删除选中
            </el-button>
        </div>


        <el-row>
            <el-col
                v-for="(picture, index) in tableData"
                :key="picture.uid"
                style="padding: 6px"
                :xs="24"
                :sm="12"
                :md="12"
                :lg="6"
                :xl="4"
            >
                <el-card
                    :body-style="{ padding: '0px', textAlign: 'center' }"
                    shadow="always"
                >
                    <input style="position: absolute;z-index: 100;" type="checkbox" :id="picture.uid"
                           :checked="fileUids.indexOf(picture.uid)>=0" @click="checked(picture)">
                    <el-image
                        :src="picture.pictureUrl"
                        style="cursor:pointer"
                        fit="scale-down"
                        :preview-src-list="[picture.pictureUrl]"
                    />
                    <div>
                        <span class="media-title" v-if="picture.picName">{{ picture.picName }}</span>
                        <span class="media-title" v-else>图片 {{ index + 1 }}</span>
                    </div>
                    <div>
                        <UserAvatar style="color: #8492a6;" :user="picture.user"></UserAvatar>
                        <span style="font-size: 14px; color: #8492a6; ">上传于 {{picture.createTime}}</span>
                    </div>
                    <div style="margin-bottom: 14px;">
                        <el-button-group>
                            <el-tooltip class="item" effect="dark" content="复制图片地址"
                                        placement="bottom-start" style="margin-right: 2px">
                                <el-button
                                    size="mini"
                                    icon="el-icon-copy-document"
                                    @click="copyUrl(picture.pictureUrl)"
                                />
                            </el-tooltip>

                            <el-tooltip class="item" effect="dark" content="复制Markdown格式图片地址"
                                        placement="bottom-start" style="margin-right: 2px">
                                <el-button
                                    type="primary"
                                    size="mini"
                                    icon="el-icon-document-copy"
                                    @click="copyMarkdownUrl(picture.pictureUrl, picture.pictureUrl)"
                                >
                                </el-button>
                            </el-tooltip>

                            <el-tooltip class="item" effect="dark" content="复制图片UID"
                                        placement="bottom-start" style="margin-right: 2px">
                                <el-button
                                    type="info"
                                    size="mini"
                                    icon="el-icon-copy-document"
                                    @click="copyUrl(picture.fileUid)"
                                />
                            </el-tooltip>


                            <el-tooltip class="item" effect="dark" content="删除图片" placement="bottom-start"
                                        style="margin-right: 2px" v-permission="'/picture/delete'">
                                <el-button
                                    type="danger"
                                    size="mini"
                                    icon="el-icon-delete"
                                    @click="handleDelete(picture)"
                                />
                            </el-tooltip>

                        </el-button-group>
                    </div>
                </el-card>

            </el-col>
        </el-row>

        <!--分页-->
        <div class="block">
            <el-pagination
                @current-change="handleCurrentChange"
                :current-page.sync="currentPage"
                :page-size="pageSize"
                layout="total, prev, pager, next, jumper"
                :total="total">
            </el-pagination>
        </div>


        <!-- 添加或修改对话框 -->
        <el-dialog :title="title" :visible.sync="dialogFormVisible">

            <el-upload
                class="upload-demo"
                drag
                ref="upload"
                name="filedatas"
                :action="uploadPictureHost"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :data="otherData"
                :on-success="fileSuccess"
                multiple>
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                <div class="el-upload__tip" slot="tip">只能上传图片，且不超过5MB</div>
            </el-upload>
        </el-dialog>
    </div>
</template>

<script>
import {addPicture, batchDeleteFile, getFileList} from "@/api/picture";
import {getToken} from '@/utils/auth'
import PictureCropper from '@/components/PictureCropper'
import UserAvatar from "../../components/UserAvatar/index.vue";
export default {
    components: {
        PictureCropper,
        UserAvatar,
    },
    data() {
        return {
            checkedPicture: {}, // 单选的图片
            pictureCropperVisible: false, // 裁剪图片框是否显示
            uploadPictureHost: null,
            fileList: [],
            pictureSortUid: undefined, // 当前选中的图片分类uid
            pictureSort: {}, //当前选中的图片分类
            fileUids: [], //图片uid集合
            pictureUploadList: [], //图片上传列表
            chooseTitle: "全选",
            isCheckedAll: false, //是否全选
            form: {
                uid: null,
                fileUid: null,
                picName: null,
                pictureSortUid: null
            },
            tableData: [], //显示的图片列表
            count: 0, //计数器，用于记录上传次数
            loading: true,
            currentPage: 1,
            pageSize: 18,
            total: null,
            title: "增加图片",
            dialogFormVisible: false,
            keyword: "",
            reFresh: true, //是否刷新组件
            activeName: "0",
            pictureSorts: [],
            havePictureSorts: false, //是否加载完图片分类
        };
    },
    watch: {
        checkedPicture(val) {
            this.reFresh = false
        }
    },
    created() {
        //传递过来的pictureSordUid
        this.pictureSortUid = this.$route.query.pictureSortUid;
        //图片上传地址
        this.uploadPictureHost = process.env.PICTURE_API + "/file/pictures";
        //其它数据
        this.otherData = {
            source: "picture",
            userUid: "uid00000000000000000000000000000000",
            adminUid: "uid00000000000000000000000000000000",
            projectName: "blog",
            sortName: "admin",
            token: getToken()
        };

        this.getFileListMethod()
    },
    methods: {
        getFileListMethod() {
            let params = {};
            params.currentPage = this.currentPage ;
            params.pageSize = this.pageSize;
            params.showUser = true
            getFileList(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    if (response.data.records.length > 0) {
                        this.currentPage = response.data.current
                        this.total = response.data.total
                        this.tableData = response.data.records
                    }
                } else {
                    this.$commonUtil.message.error(response.message)
                }
            });
        },
        handleCurrentChange: function (val) {
            console.log("切换分页", val)
            this.currentPage = val
            this.getFileListMethod()
        },
        getFormObject: function () {
            let formObject = {
                uid: null,
                fileUid: null,
                picName: null,
                pictureSortUid: null
            };
            return formObject;
        },
        copyUrl(url) {
            this.$commonUtil.copyText(url)
            this.$commonUtil.message.success('复制链接到剪切板成功')
        },
        copyMarkdownUrl(name, url) {
            const text = '![' + name + '](' + url + ')'
            this.$commonUtil.copyText(text)
            this.$commonUtil.message.success('复制Markdown格式链接到剪切板成功')
        },
        //点击单选
        checked: function (data) {
            let idIndex = this.fileUids.indexOf(data.uid);
            if (idIndex >= 0) {
                //选过了
                this.fileUids.splice(idIndex, 1);
            } else {
                this.fileUids.push(data.uid);
            }
            console.log("选择列表", this.fileUids)
        },
        checkAll: function () {
            //如果是全选
            if (this.isCheckedAll) {
                this.fileUids = [];
                this.isCheckedAll = false;
                this.chooseTitle = "全选";
            } else {
                this.fileUids = [];
                console.log("tableData", this.tableData)
                this.tableData.forEach(function (file) {
                    this.fileUids.push(file.uid);
                }, this);
                this.isCheckedAll = true;
                this.chooseTitle = "取消全选";
            }
        },
        handleDelete: function (file) {
            console.log("要删除的文件", file)
            this.fileUids = [file.uid]
            this.handleDeleteBatch()
        },
        handleDeleteBatch: function () {
            if (this.fileUids.length <= 0) {
                this.$commonUtil.message.error("请先选中图片！")
                return;
            }
            this.$confirm("是否删除素材图片？, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    batchDeleteFile(this.fileUids).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                            // 清空选中的列表
                            this.fileUids = []
                            this.checkedPicture = []
                            this.handleCurrentChange(this.currentPage);
                            this.chooseTitle = "全选";
                            this.isCheckedAll = false;
                        }
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除")
                });
        },
        handleAdd: function () {
            this.dialogFormVisible = true;
        },
        handlePreview: function () {

        },
        handleRemove: function () {

        },
        fileSuccess: function (response, file, fileList) {
            if (response.code == this.$ECode.SUCCESS) {
                let file = response.data;
                for (let index = 0; index < file.length; index++) {
                    let picture = {};
                    picture.fileUid = file[index].uid;
                    picture.pictureSortUid = this.pictureSortUid
                    picture.picName = file[index].picName
                    this.pictureUploadList.push(picture)
                }
                this.count = this.count + 1;
                if (this.count % fileList.length == 0) {
                    addPicture(this.pictureUploadList).then(res => {
                        if (res.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(res.message)
                            this.handleCurrentChange(this.currentPage);
                        } else {
                            this.$commonUtil.message.error(res.message)
                        }
                        this.$refs.upload.clearFiles();
                        this.fileUids = "";
                        this.pictureUploadList = []
                    });
                }
            } else {
                this.$commonUtil.message.error(response.message)
            }
        }
    }
};
</script>

<style scoped>
.media-title {
    color: #8492a6;
    font-size: 14px;
    padding: 3px;
    display: inline-block;
    white-space: nowrap;
    width: 60%;
    overflow: hidden;
    text-overflow: ellipsis;
}

.el-image {
    width: 100%;
    height: 160px;
}
</style>
