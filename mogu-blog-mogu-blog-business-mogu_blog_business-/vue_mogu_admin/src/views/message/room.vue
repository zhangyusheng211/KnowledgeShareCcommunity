<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;">
            <el-input clearable class="filter-item" style="width: 200px;" size="small" v-model="queryParams.title"
                      placeholder="请输入房间标题"></el-input>
            <el-input clearable class="filter-item" style="width: 200px;" size="small"
                      v-model="queryParams.belongUserId" placeholder="归属人id"></el-input>
            <el-input clearable class="filter-item" style="width: 200px;" size="small" v-model="queryParams.receiveId"
                      placeholder="接收人id"></el-input>

            <el-select v-model="queryParams.roomType" clearable placeholder="房间类型" size="small"
                       style="margin-left:5px;width:150px">
                <el-option
                    v-for="item in messageTypeDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                ></el-option>
            </el-select>

            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFind" size="small"
                       v-permission="'/room/getList'">查找
            </el-button>

            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleAdd" size="small"
                       v-permission="'/room/getList'">新增
            </el-button>

            <el-button class="filter-item" type="danger" @click="handleDeleteBatch" icon="el-icon-delete" size="small"
                       v-permission="'/room/deleteBatch'">删除选中
            </el-button>
        </div>

        <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">

            <el-table-column type="selection"></el-table-column>

            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.$index + 1 }}</span>
                </template>
            </el-table-column>

            <el-table-column label="房间所属人" width="200" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.user"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="接收人" width="200" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.toUser"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="类型" width="120" align="center" prop="level" sortable="custom"
                             :sort-by="['level']">
                <template slot-scope="scope">
                    <el-tag v-for="item in messageTypeDictList" :key="item.uid"
                            v-if="scope.row.roomType == item.dictValue" :type="item.listClass">{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>


            <el-table-column label="房间标题" width="400" align="center">
                <template slot-scope="scope">
                    <span> {{ scope.row.title }}</span>
                </template>
            </el-table-column>

            <el-table-column label="创建时间" width="160" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="操作" fixed="right" min-width="150">
                <template slot-scope="scope">
                    <el-button @click="handleEdit(scope.row)" type="primary" size="mini" v-permission="'/room/edit'">
                        编辑
                    </el-button>
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini" v-permission="'/room/delete'">
                        删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

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
            <el-form :model="form">

                <el-form-item label="群聊头像" :label-width="formLabelWidth" prop="fileUid">
                    <div class="imgBody" v-if="form.avatar">
                        <i class="el-icon-error inputClass" v-show="icon" @click="deletePhoto()"
                           @mouseover="icon = true"></i>
                        <img @mouseover="icon = true" @mouseout="icon = false" v-bind:src="form.avatar"
                             style="display:inline; width: 105px;height: 105px;"/>
                    </div>
                    <div v-else class="uploadImgBody" @click="checkPhoto">
                        <i class="el-icon-plus avatar-uploader-icon"></i>
                    </div>
                </el-form-item>
                <el-form-item label="头像" :label-width="formLabelWidth">
                    <el-input v-model="form.title" auto-complete="off"></el-input>
                </el-form-item>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitForm">确 定</el-button>
            </div>
        </el-dialog>

        <avatar-cropper
            :key="imagecropperKey"
            :url="url"
            :width="195"
            :height="195"
            @close="close"
            @crop-upload-success="cropSuccess"
            lang-type="zh"
            v-show="imagecropperShow"
            noSquare
            noCircle
        />

    </div>
</template>

<script>
import {deleteBatchRoom, getRoomList} from "@/api/chat";
import {getListByDictTypeList} from "@/api/sysDictData"
import {addRoom, editRoom} from "../../api/chat";
import AvatarCropper from '@/components/AvatarCropper'
import UserAvatar from "../../components/UserAvatar"

export default {
    data() {
        return {
            // xss白名单配置
            options: {
                whiteList: {
                    a: ['href', 'title', 'target', 'class'],
                    span: ['class'],
                    p: ['class'],
                    h1: ['class'],
                    h2: ['class'],
                    h3: ['class'],
                    h4: ['class'],
                    pre: ['class'],
                    code: ['class'],
                    div: ['class'],
                    img: ['src', 'alt'],
                    ul: ['class'],
                    li: ['class'],
                }
            },
            queryParams: {
                title: null, // 标题
                belongUserId: null, // 评论人uid
                receiveId: null, // 被评论人uid
                messageType: null, //用户名
            }, //查询参数
            multipleSelection: [],  //多选，用于批量删除
            BLOG_WEB_URL: process.env.BLOG_WEB_URL,
            tableData: [],
            keyword: "",
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "创建群聊",
            dialogFormVisible: false, //控制弹出框
            formLabelWidth: '120px',
            isEditForm: false,
            form: {},
            messageTypeDictList: [], //消息类型字典
            defaultAvatar: process.env.defaultAvatar, // 默认头像
            orderByDescColumn: '',
            orderByAscColumn: 'send_time',
            photoVisible: false, //控制图片选择器的显示
            photoList: [],
            fileIds: "",
            icon: false, //控制删除图标的显示
            imagecropperShow: false,
            imagecropperKey: 0,
            url: process.env.PICTURE_API + "/file/cropperPicture",
        };
    },
    components: {
        AvatarCropper,
        UserAvatar,
    },
    created() {
        // 获取字典
        this.getDictList()
        // 获取评论
        this.roomList();
    },
    methods: {
        cropSuccess(resData) {
            this.imagecropperShow = false
            this.imagecropperKey = this.imagecropperKey + 1
            this.form.avatar = resData.url
        },
        close() {
            this.imagecropperShow = false
        },
        deletePhoto: function () {
            this.form.avatar = "";
            this.icon = false;
        },
        //弹出选择图片框
        checkPhoto() {
            this.imagecropperShow = true
        },
        submitForm: function() {
            if (this.isEditForm) {
                editRoom(this.form).then(response => {
                    console.log(response);
                    if (response.code == this.$ECode.SUCCESS) {
                        this.$commonUtil.message.success(response.message)
                        this.dialogFormVisible = false;
                        this.roomList();
                    } else {
                        this.$commonUtil.message.error(response.message)
                    }
                });
            } else {
                addRoom(this.form).then(response => {
                    console.log(response);
                    if (response.code == this.$ECode.SUCCESS) {
                        this.$commonUtil.message.success(response.message)
                        this.dialogFormVisible = false;
                        this.roomList();
                    } else {
                        this.$commonUtil.message.error(response.message)
                    }
                });
            }
        },
        /**
         * 字典查询
         */
        getDictList: function () {
            let dictTypeList = ['sys_message_type']
            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    let dictMap = response.data;
                    this.messageTypeDictList = dictMap.sys_message_type.list
                }
            });
        },
        roomList: function () {
            let params = {}
            params.title = this.queryParams.title
            params.belongUserId = this.queryParams.belongUserId
            params.receiveId = this.queryParams.receiveId
            params.roomType = this.queryParams.roomType
            params.currentPage = this.currentPage
            params.pageSize = this.pageSize
            params.orderByDescColumn = this.orderByDescColumn

            getRoomList(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.tableData = response.data.records;
                    this.currentPage = response.data.current;
                    this.pageSize = response.data.size;
                    this.total = response.data.total;
                }
            });
        },
        subStr(str, index) {
            if (str == null || str == undefined) {
                return "";
            }
            if (str.length < index) {
                return str;
            } else {
                return str.substring(0, index) + ".."
            }
        },
        handleFind: function () {
            this.roomList();
        },
        getFormObject: function() {
            let formObject = {
                uid: null,
                title: null,
                avatar: null
            };
            return formObject;
        },
        handleAdd: function() {
            this.title = "增加分类"
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.isEditForm = false;
        },
        handleEdit: function (row) {
            this.title = "编辑"
            this.dialogFormVisible = true;
            this.isEditForm = true;
            this.form = row;
        },
        handleDelete: function (row) {
            this.$confirm("此操作将把该房间删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let params = {}
                    params.uid = row.uid
                    let roomUidList = [params]
                    deleteBatchRoom(roomUidList).then(response => {
                        this.$commonUtil.message.success(response.message)
                        this.roomList();
                    })
                })
                .catch(() => {
                    this.$commonUtil.info("已取消删除")
                });
        },
        handleDeleteBatch: function () {
            let that = this;
            if (that.multipleSelection.length <= 0) {
                this.$message({
                    type: "error",
                    message: "请先选中需要删除的内容！"
                });
                return;
            }
            this.$confirm("此操作将把选中的评论删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    deleteBatchRoom(that.multipleSelection).then(response => {
                        if (response.code == that.$ECode.SUCCESS) {
                            that.$commonUtil.message.success(response.message)
                        } else {
                            that.$commonUtil.message.error(response.message)
                        }
                        that.roomList();
                    });
                })
                .catch(() => {
                    that.$commonUtil.info("已取消删除")
                });
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.roomList();
        },
        // 改变多选
        handleSelectionChange(val) {
            this.multipleSelection = val;
        }

    }
};
</script>
<style>
@import "../../assets/css/emoji.css";

.emoji-item-common {
    background: url("../../assets/img/emoji_sprite.png");
    display: inline-block;
}

.emoji-item-common:hover {
    cursor: pointer;
}

.emoji-size-small {
    zoom: 0.3;
    margin: 5px;
    vertical-align: middle;
}

.messageInfo img {
    max-height: 30px;
}
</style>

<style scoped>

.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    margin: 0, 0, 0, 10px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
}

.avatar-uploader .el-upload:hover {
    border-color: #409eff;
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 105px;
    height: 105px;
    line-height: 105px;
    text-align: center;
}

.imgBody {
    width: 105px;
    height: 105px;
    border: solid 2px #ffffff;
    float: left;
    position: relative;
}

.uploadImgBody {
    margin-left: 5px;
    width: 105px;
    height: 105px;
    border: dashed 1px #c0c0c0;
    float: left;
    position: relative;
}

.uploadImgBody :hover {
    border: dashed 1px #00ccff;
}

.inputClass {
    position: absolute;
}

.img {
    width: 100%;
    height: 100%;
}

.media-title {
    color: #8492a6;
    font-size: 14px;
    padding: 14px;
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

