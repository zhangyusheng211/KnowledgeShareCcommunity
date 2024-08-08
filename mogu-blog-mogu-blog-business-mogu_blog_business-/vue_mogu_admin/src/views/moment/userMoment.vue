<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/userMoment/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 200px;"
                v-model="keyword"
                placeholder="请输入关键字"
                size="small"
            ></el-input>
            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFind" size="small"
                       v-permission="'/userMoment/getList'">查找
            </el-button>
            <el-button class="filter-item" type="danger" @click="handleDeleteBatch" icon="el-icon-delete" size="small"
                       v-permission="'/userMoment/deleteBatch'">删除选中
            </el-button>

        </div>

        <el-table :data="tableData"
                  style="width: 100%"
                  @selection-change="handleSelectionChange"
                  @sort-change="changeSort"
                  :default-sort="{prop: 'sort', order: 'descending'}">
            <el-table-column type="selection"></el-table-column>

            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.$index + 1 }}</span>
                </template>
            </el-table-column>

            <el-table-column label="作者" width="200" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.user"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="动态内容" width="400" align="center">
                <template slot-scope="scope">
                    <span v-html="$xss(replaceImg(scope.row.content), options)"></span>
                </template>
            </el-table-column>

            <el-table-column label="动态图" width="200" align="center">
                <template slot-scope="scope">
                    <div class="demo-image__preview" style="margin-top: 10px;" v-if="scope.row.photoList">
                        <el-image
                            v-for="(image, index) in scope.row.photoList"
                            :key="index"
                            fit="contain"
                            style="height: 50px; cursor:zoom-in; margin-right: 5px;"
                            :src="image"
                            :preview-src-list="scope.row.photoList">
                        </el-image>
                    </div>
                </template>
            </el-table-column>

            <el-table-column label="标签" width="100" align="center">
                <template slot-scope="scope">
                    <template>
                        <el-tag
                            style="margin-left: 3px"
                            type="warning"
                            v-if="item"
                            :key="index"
                            v-for="(item, index) in scope.row.userMomentTopicList"
                            :type="typeList[item.sort%5]"
                            hit
                            size="mini"
                            effect="light"
                        >{{ item.content }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="链接" width="100" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.url }}</span>
                </template>
            </el-table-column>

            <el-table-column label="审核人" width="80" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.auditName }}</span>
                </template>
            </el-table-column>

            <el-table-column label="审批状态" width="100" align="center" prop="level" sortable="custom"
                             :sort-by="['auditStatus']">
                <template slot-scope="scope">
                    <el-tag v-for="item in auditStatusDictList" :key="item.uid"
                            v-if="scope.row.auditStatus == item.dictValue" :type="item.listClass">
                        <el-tooltip v-if="scope.row.auditStatus == 1" class="item" placement="top">
                            <div slot="content">{{ scope.row.rejectReason }}</div>
                            <div>{{ item.dictLabel }}</div>
                        </el-tooltip>
                        <span v-else>
              {{ item.dictLabel }}
            </span>
                    </el-tag>
                </template>
            </el-table-column>

<!--            <el-table-column label="发布状态" width="100" align="center" prop="isPublish" sortable="custom"-->
<!--                             :sort-by="['isPublish']">-->
<!--                <template slot-scope="scope">-->
<!--                    <el-tag-->
<!--                        :type="scope.row.isPublish == '1' ? 'success' : 'primary'"-->
<!--                        disable-transitions>{{ scope.row.isPublish == '1' ? '已发布' : '已下架' }}-->
<!--                    </el-tag>-->
<!--                </template>-->
<!--            </el-table-column>-->

            <el-table-column label="开启评论" width="100" align="center" prop="openComment" sortable="custom"
                             :sort-by="['openComment']">
                <template slot-scope="scope">
                    <template>
                        <el-switch v-model="scope.row.openCommentStatus" active-color="#F5DEB3"
                                   @change="handChangeMoment(scope.row)"></el-switch>
                    </template>
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['isTop']"
                label="置顶"
                width="80"
                align="center"
                prop="isTop"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <template>
                        <el-switch
                            v-model="scope.row.openTopStatus"
                            active-color="#F5DEB3"
                            @change="handChangeMoment(scope.row)"
                        />
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="发布状态" width="100" align="center" prop="isPublish" sortable="custom"
                             :sort-by="['isPublish']">
                <template slot-scope="scope">
                    <template>
                        <el-switch v-model="scope.row.publishStatus" active-color="#13ce66" inactive-color="#ff4949"
                                   @change="handChangeMoment(scope.row)"></el-switch>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="创建时间" width="160" align="center" prop="createTime" sortable="custom"
                             :sort-by="['createTime']">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="状态" width="100" align="center">
                <template slot-scope="scope">
                    <template v-if="scope.row.status == 1">
                        <span>正常</span>
                    </template>
                    <template v-if="scope.row.status == 2">
                        <span>推荐</span>
                    </template>
                    <template v-if="scope.row.status == 0">
                        <span>已删除</span>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="操作" fixed="right" min-width="230">
                <template slot-scope="scope">
                    <el-button @click="handleEdit(scope.row)" type="primary" size="mini"
                               v-permission="'/userMoment/edit'">编辑
                    </el-button>
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini"
                               v-permission="'/userMoment/deleteBatch'">删除
                    </el-button>
                    <el-button v-if="(scope.row.auditStatus == 0 || scope.row.auditStatus == 1)"
                               @click="handleAudit(scope.row)" type="success" size="mini"
                               v-permission="'/userMoment/audit'">审核
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
                :total="total"
            ></el-pagination>
        </div>

        <!-- 添加或修改对话框 -->
        <el-dialog :title="title" :visible.sync="dialogFormVisible">
            <el-form :model="form" :rules="rules" ref="form">

                <el-form-item label="动态内容" :label-width="formLabelWidth" prop="content">
                    <el-input v-model="form.content" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="话题" :label-width="formLabelWidth" prop="topicUids">
                    <el-select
                        v-model="momentTopicValue"
                        multiple
                        size="small"
                        placeholder="请选择"
                        style="width:210px"
                        filterable
                        :multiple-limit="3"
                    >
                        <el-option
                            v-for="item in momentTopicData"
                            :key="item.uid"
                            :label="item.content"
                            :value="item.uid"
                        ></el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="图片" :label-width="formLabelWidth" prop="content">
          <span class="imgBody" v-for="(image, index) in form.photoList">
            <el-image
                class="uploadImgBody"
                fit="contain"
                style="height: 105px; cursor:zoom-in; margin-right: 5px;"
                :src="image"
                :preview-src-list="form.photoList">
            </el-image>
            <i
                class="el-icon-error inputClass"
                @click="handleDeletePhoto(index)"
            ></i>
          </span>
                    <div v-if="form.photoList == 0 || !form.photoList" class="addPicture" @click="handleAddPicture()">
                        <span>+</span>
                    </div>
                </el-form-item>

                <el-form-item label="链接" :label-width="formLabelWidth" prop="content">
                    <el-input v-model="form.url" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="链接信息" :label-width="formLabelWidth" prop="content">
                    <el-input v-model="form.urlInfo" auto-complete="off"></el-input>
                </el-form-item>

                <!--        <el-form-item label="排序" :label-width="formLabelWidth" prop="sort">-->
                <!--          <el-input v-model="form.sort" auto-complete="off"></el-input>-->
                <!--        </el-form-item>-->

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitForm(0)">确 定</el-button>
            </div>
        </el-dialog>

        <!-- 添加或修改对话框 -->
        <el-dialog title="上传图片" :visible.sync="dialogUploadFormVisible">
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
        <!--  审批弹出框  -->
        <el-dialog
            title="请选择审批结果"
            :visible.sync="auditDialogVisible"
            width="30%"
            center>
            <div style="text-align: center">
                <el-form>
                    <el-form-item label="审批状态" :label-width="lineLabelWidth" prop="auditStatus">
                        <el-radio-group v-model="auditForm.auditStatus" size="small">
                            <el-radio v-for="item in auditStatusDictList" v-if="item.dictValue != 0" :key="item.uid"
                                      :label="item.dictValue" border>{{ item.dictLabel }}
                            </el-radio>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item label="拒绝原因" :label-width="lineLabelWidth" v-if="auditForm.auditStatus == 1"
                                  prop="rejectReason">
                        <el-input v-model="auditForm.rejectReason" maxlength="50" placeholder="拒绝的原因"
                                  clearable></el-input>
                    </el-form-item>
                </el-form>


            </div>
            <span slot="footer" class="dialog-footer">
    <el-button @click="auditDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="submitAudit">确 定</el-button>
    </span>
        </el-dialog>
    </div>
</template>

<script>
import {addUserMoment, auditMoment, deleteBatchUserMoment, editUserMoment, getUserMomentList} from "@/api/userMoment";
import {getUserMomentTopicList} from "@/api/userMomentTopic";
import {getListByDictTypeList} from "@/api/sysDictData";
import {getToken} from '@/utils/auth'
import UserAvatar from "../../components/UserAvatar/index.vue";

export default {
    data() {
        return {
            // xss白名单配置
            options: {
                whiteList: {
                    a: ['href', 'title', 'target', 'style', 'class'],
                    span: ['class'],
                    h1: ['class'],
                    h2: ['class'],
                    h3: ['class'],
                    h4: ['class'],
                    pre: [],
                    code: ['class'],
                    p: ['class'],
                    blockquote: ['class'],
                    ul: ['class'],
                    li: ['class'],
                    img: ['class', 'src', 'alt', 'style'],
                    iframe: ['class', 'src', 'style', 'scrolling', 'border', 'frameborder', 'allowfullscreen'],
                    hr: [],
                    video: ['class', 'src', 'controls'],
                    source: ['src', 'type']
                }
            },
            multipleSelection: [], //多选，用于批量删除
            tableData: [],
            keyword: "",
            isBlack: true,
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "增加标签",
            dialogFormVisible: false, //控制弹出框
            dialogUploadFormVisible: false, //控制图片上传弹出
            formLabelWidth: "120px",
            isEditForm: false,
            orderByDescColumn: "", // 降序字段
            orderByAscColumn: "", // 升序字段
            publishDictList: [], //是否发布字典
            openDictList: [], // 是否启动字典
            auditStatusDictList: [], // 审批状态字典
            publishDefault: null, //博客发布默认值
            openDefault: null, // 是否开启评论默认值
            auditStatusDefault: null, // 审批状态默认值
            momentTopicValue: [], //保存选中标签id(编辑时)
            momentTopicData: [],
            momentTopicOptions: [],
            uploadPictureHost: null, // 图片上传地址
            auditForm: {},
            auditDialogVisible: false, // 审批结果
            lineLabelWidth: "120px", //一行的间隔数
            typeList: ['warning', 'danger', 'success', 'info', 'primary'],
            form: {
                content: ""
            },
            rules: {
                content: [
                    {required: true, message: '用户动态不能为空', trigger: 'blur'}
                ],
            }
        };
    },
    created() {
        this.momentTopicList()
        this.getDictList();
        this.userMomentList();

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
    },
    components: {
        UserAvatar,
    },
    methods: {
        submitAudit: function () {
            let params = {}
            params.uid = this.auditForm.uid
            params.auditStatus = this.auditForm.auditStatus
            params.rejectReason = this.auditForm.rejectReason
            auditMoment(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.$commonUtil.message.success(response.message)
                    this.auditDialogVisible = false
                    this.userMomentList()
                } else {
                    this.$commonUtil.message.error(response.message)
                }
            });
        },
        getAuditFormObject: function () {
            let auditForm = {
                uid: "",
                auditStatus: this.auditStatusDefault, // 审批状态默认值
                rejectReason: null, // 审批拒绝原因
            }
            return auditForm
        },
        // 点击审核
        handleAudit: function (row) {
            this.auditForm = this.getAuditFormObject();
            this.auditForm.uid = row.uid
            this.auditDialogVisible = true
        },
        /**
         * 字典查询
         */
        getDictList: function () {
            let dictTypeList = ['sys_publish_status', 'sys_normal_disable', 'sys_audit_status']
            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    let dictMap = response.data;
                    this.publishDictList = dictMap.sys_publish_status.list
                    this.openDictList = dictMap.sys_normal_disable.list
                    this.auditStatusDictList = dictMap.sys_audit_status.list
                    if (dictMap.sys_publish_status.defaultValue) {
                        this.publishDefault = dictMap.sys_publish_status.defaultValue;
                    }
                    if (dictMap.sys_normal_disable.defaultValue) {
                        this.openDefault = dictMap.sys_normal_disable.defaultValue;
                    }
                    if (dictMap.sys_audit_status.defaultValue) {
                        this.auditStatusDefault = dictMap.sys_audit_status.defaultValue;
                    }
                }
            });
        },
        replaceImg: function (text) {
            text = this.$commonUtil.replaceMinImg(text)
            text = this.$commonUtil.replaceMinIframe(text)
            return text
        },
        momentTopicList: function () {
            let params = {};
            params.pageSize = 100
            params.currentPage = 1
            getUserMomentTopicList(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    console.log("为了", response)
                    this.momentTopicData = response.data.records;
                    this.momentTopicOptions = response.data.records;
                }
            });
        },
        // 添加图片
        handleAddPicture: function () {
            this.dialogUploadFormVisible = true;
        },
        handlePreview: function () {

        },
        handleRemove: function () {

        },
        fileSuccess: function (response, file, fileList) {
            if (response.code == this.$ECode.SUCCESS) {
                let file = response.data;
                let fileUids = ""
                let photoList = []
                console.log("获取fileUids", this.form.fileUids)
                if (this.form.fileUids) {
                    fileUids = this.form.fileUids
                }
                console.log("获取fileUids", this.form.photoList)
                if (this.form.photoList) {
                    photoList = this.form.photoList
                }
                for (let index = 0; index < file.length; index++) {
                    fileUids += file[index].uid + ","
                    photoList.push(file[index].pictureUrl)
                }
                this.form.fileUids = fileUids
                this.form.photoList = photoList
                this.dialogUploadFormVisible = false
                console.log("裁剪成功", this.form.fileUids)
            } else {
                this.$commonUtil.message.error(response.message)
            }
        },
        // 跳转到用户中心
        goUser: function (item) {
            this.$router.push({path: "/user/user", query: {nickName: item.user.nickName}});
        },
        // 从后台获取数据,重新排序
        changeSort(val) {
            // 根据当前排序重新获取后台数据,一般后台会需要一个排序的参数
            if (val.order == "ascending") {
                this.orderByAscColumn = val.prop
                this.orderByDescColumn = ""
            } else {
                this.orderByAscColumn = ""
                this.orderByDescColumn = val.prop
            }
            this.userMomentList()
        },
        userMomentList: function () {
            let params = {};
            params.isBlack = this.isBlack;
            params.keyword = this.keyword;
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getUserMomentList(params).then(response => {
                let tableData = response.data.records;
                this.currentPage = response.data.current;
                this.pageSize = response.data.size;
                this.total = response.data.total;

                for (let i = 0; i < tableData.length; i++) {
                    if (tableData[i].openComment == 1) {
                        tableData[i].openCommentStatus = true
                    } else {
                        tableData[i].openCommentStatus = false
                    }
                    if (tableData[i].openLoadingValid == 1) {
                        tableData[i].openLoadingValidStatus = true
                    } else {
                        tableData[i].openLoadingValidStatus = false
                    }
                    if (tableData[i].isPublish == 1) {
                        tableData[i].publishStatus = true
                    } else {
                        tableData[i].publishStatus = false
                    }
                    if (tableData[i].isTop == 1) {
                        tableData[i].openTopStatus = true
                    } else {
                        tableData[i].openTopStatus = false
                    }
                }
                this.tableData = tableData
            });
        },
        handleDeletePhoto: function (index) {
            console.log("获取form", this.form, index)
            let fileIds = this.form.fileUids
            let resultFileIds = ""
            let fileIdList = fileIds.split(",")
            for (let a = 0; a < fileIdList.length; a++) {
                if (a == index || fileIdList[a] == "") {
                    continue
                }
                resultFileIds += fileIdList[a] + ","
            }
            this.form.fileUids = resultFileIds
            this.form.photoList.splice(index, 1)
        },
        getFormObject: function () {
            let formObject = {
                uid: null,
                content: null,
                clickCount: 0,
                sort: 0
            };
            return formObject;
        },
        handleFind: function () {
            this.currentPage = 1
            this.userMomentList();
        },
        handleAdd: function () {
            this.title = "增加动态"
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.isEditForm = false;
        },
        handleEdit: function (row) {
            console.log("编辑动态", row)
            this.title = "编辑动态";
            this.dialogFormVisible = true;
            this.isEditForm = true;
            this.momentTopicValue = []
            if (row.userMomentTopicList) {
                let json = row.userMomentTopicList;
                for (let i = 0, l = json.length; i < l; i++) {
                    if (json[i] != null) {
                        this.momentTopicValue.push(json[i]["uid"]);
                    }
                }
            }

            this.form = row;
        },
        handleDelete: function (row) {
            let that = this;
            this.$confirm("此操作将把标签删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let params = [];
                    params.push(row);
                    deleteBatchUserMoment(params).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                        that.userMomentList();
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除")
                });
        },
        handleDeleteBatch: function () {
            let that = this;
            if (that.multipleSelection.length <= 0) {
                this.$commonUtil.message.error("请先选中需要删除的内容")
                return;
            }
            this.$confirm("此操作将把选中的动态删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    deleteBatchUserMoment(that.multipleSelection).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                        that.userMomentList();
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除")
                });
        },
        handChangeMoment(row) {
            if (row.openCommentStatus) {
                row.openComment = "1"
            } else {
                row.openComment = "0"
            }
            if (row.publishStatus) {
                row.isPublish = "1"
            } else {
                row.isPublish = "0"
            }
            if (row.openTopStatus) {
                row.isTop = 1
            } else {
                row.isTop = 0
            }
            this.form = row;
            this.submitForm(1)
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.userMomentList();
        },
        submitForm: function (submitType) {
            this.form.topicUids = this.momentTopicValue.join(",");
            // 在编辑界面提交
            if (submitType == 0) {
                this.$refs.form.validate((valid) => {
                    if (!valid) {
                        console.log('校验失败')
                        return;
                    } else {
                        if (this.isEditForm) {
                            editUserMoment(this.form).then(response => {
                                if (response.code == this.$ECode.SUCCESS) {
                                    this.$commonUtil.message.success(response.message)
                                    this.dialogFormVisible = false;
                                    this.userMomentList();
                                } else {
                                    this.$commonUtil.message.error(response.message)
                                }
                            });
                        } else {
                            addUserMoment(this.form).then(response => {
                                if (response.code == this.$ECode.SUCCESS) {
                                    this.$commonUtil.message.success(response.message)
                                    this.dialogFormVisible = false;
                                    this.userMomentList();
                                } else {
                                    this.$commonUtil.message.error(response.message)
                                }
                            });
                        }
                    }
                })
            } else {
                editUserMoment(this.form).then(response => {
                    if (response.code == this.$ECode.SUCCESS) {
                        this.$commonUtil.message.success(response.message)
                        this.dialogFormVisible = false;
                        this.userMomentList();
                    } else {
                        this.$commonUtil.message.error(response.message)
                    }
                });
            }

        },
        // 改变多选
        handleSelectionChange(val) {
            this.multipleSelection = val;
        }
    }
};
</script>

<style scoped>
.uploadImgBody {
    margin-left: 5px;
    height: 105px;
    border: dashed 1px #c0c0c0;

}

.addPicture {
    width: 195px;
    height: 105px;
    float: left;
    margin-left: 10px;
    border: solid 1px #c7aeae;
    line-height: 105px;
    text-align: center;
    cursor: pointer;
}

.addPicture span {
    font-size: 30px;
    color: #97a8be;
    height: 60px;
    margin: 0 auto;
}


</style>
