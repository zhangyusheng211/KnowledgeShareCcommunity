<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/blog/getList'">

            <el-form :inline="true" v-show="showSearch" label-width="68px" style="margin-bottom: 8px;">
                <el-input
                    clearable
                    class="filter-item"
                    style="width: 150px;"
                    v-model="queryParams.keyword"
                    placeholder="请输入问答标题"
                    @keyup.enter.native="handleFind"
                    size="small"
                ></el-input>

                <el-select
                    v-model="queryParams.tagKeyword"
                    filterable
                    clearable
                    remote
                    reserve-keyword
                    placeholder="请输入标签名"
                    :remote-method="tagRemoteMethod"
                    :loading="loading"
                    @keyup.enter.native="handleFind"
                    style="width:140px"
                    size="small"
                >
                    <el-option
                        v-for="item in tagOptions"
                        :key="item.uid"
                        :label="item.name"
                        :value="item.uid"
                    ></el-option>
                </el-select>

                <el-select v-model="queryParams.publishKeyword" clearable placeholder="是否发布" size="small"
                           style="width:110px">
                    <el-option
                        v-for="item in blogPublishDictList"
                        :key="item.uid"
                        :label="item.dictLabel"
                        :value="item.dictValue"
                    ></el-option>
                </el-select>

                <el-button style="margin-left: 10px;" class="filter-item" type="primary" icon="el-icon-search"
                           @click="handleFind" size="small" v-permission="'/blog/getList'">查找
                </el-button>

            </el-form>

            <el-row :gutter="10" style="margin-bottom: 8px;">
                <el-col :span="1.5">
                    <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit" size="small"
                               v-permission="'/blog/add'">添加问答
                    </el-button>
                </el-col>


                <el-col :span="1.5">
                    <el-button class="filter-item" type="danger" @click="handleDeleteBatch" icon="el-icon-delete"
                               size="small" v-permission="'/blog/deleteBatch'">删除选中
                    </el-button>
                </el-col>

                <right-toolbar :showSearch.sync="showSearch" @queryTable="resetBlogList"></right-toolbar>
            </el-row>

        </div>

        <el-table :data="tableData"
                  ref="articleTable"
                  style="width: 100%"
                  @selection-change="handleSelectionChange"
                  @sort-change="changeSort"
                  :default-sort="{prop: 'createTime', order: 'descending'}">
            <el-table-column type="selection"></el-table-column>

            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.$index + 1 }}</span>
                </template>
            </el-table-column>

            <el-table-column label="标题" width="250" align="left">
                <template slot-scope="scope">
                    <span @click="onClick(scope.row)" style="cursor:pointer;">{{ scope.row.title }}</span>
                </template>
            </el-table-column>

            <el-table-column label="用户" width="200" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.user"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="标签" width="160" align="center">
                <template slot-scope="scope">
                    <template>
                        <el-tag
                            style="margin-left: 3px"
                            :type="typeList[item.sort%5]"
                            v-if="item"
                            :key="index"
                            v-for="(item, index) in scope.row.questionTagList"
                        >{{ item.name }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="点击数" width="90" align="center" prop="clickCount" sortable="custom"
                             :sort-by="['clickCount']">
                <template slot-scope="scope">
                    <span>{{ scope.row.clickCount }}</span>
                </template>
            </el-table-column>

            <el-table-column label="回答数" width="90" align="center" prop="clickCount" sortable="custom"
                             :sort-by="['clickCount']">
                <template slot-scope="scope">
                    <span>{{ scope.row.replyCount }}</span>
                </template>
            </el-table-column>

            <el-table-column label="问答状态" width="100" align="center" prop="questionStatus" sortable="custom"
                             :sort-by="['questionStatus']">
                <template slot-scope="scope">
                    <template>
                        <el-tag v-for="item in questionStatusDictList" :key="item.uid" :type="item.listClass"
                                v-if="scope.row.questionStatus == item.dictValue">{{ item.dictLabel }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="问答来源" width="100" align="center" prop="questionStatus" sortable="custom"
                             :sort-by="['questionStatus']">
                <template slot-scope="scope">
                    <template>
                        <el-tag v-for="item in articleDictList" :key="item.uid" :type="item.listClass"
                                v-if="scope.row.questionSource == item.dictValue">{{ item.dictLabel }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="开启评论" width="100" align="center" prop="openComment" sortable="custom"
                             :sort-by="['openComment']">
                <template slot-scope="scope">
                    <template>
                        <!--            <el-tag v-for="item in openDictList" :key="item.uid" :type="item.listClass" v-if="scope.row.openComment == item.dictValue">{{item.dictLabel}}</el-tag>-->
                        <el-switch v-model="scope.row.openCommentStatus" active-color="#F5DEB3"
                                   @change="handChangeQuestion(scope.row)"></el-switch>
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
                            @change="handChangeQuestion(scope.row)"
                        />
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="发布状态" width="100" align="center" prop="isPublish" sortable="custom"
                             :sort-by="['isPublish']">
                <template slot-scope="scope">
                    <template>
                        <!--            <el-tag v-for="item in blogPublishDictList" :key="item.uid" :type="item.listClass" v-if="scope.row.isPublish == item.dictValue">{{item.dictLabel}}</el-tag>-->
                        <el-switch v-model="scope.row.publishStatus" active-color="#13ce66" inactive-color="#ff4949"
                                   @change="handChangeQuestion(scope.row)"></el-switch>
                    </template>
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

            <el-table-column :sort-orders="['ascending', 'descending']"
                             label="创建时间"
                             width="160"
                             align="center"
                             prop="createTime"
                             sortable="custom">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="操作" fixed="right" min-width="220">
                <template slot-scope="scope">
                    <el-button @click="handleEdit(scope.row)" type="primary" size="mini" v-permission="'/blog/edit'">
                        编辑
                    </el-button>
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini" v-permission="'/blog/delete'">
                        删除
                    </el-button>
                    <el-button v-if="(scope.row.auditStatus == 0 || scope.row.auditStatus == 1)"
                               @click="handleAudit(scope.row)" type="success" size="mini" v-permission="'/blog/audit'">
                        审核
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
        <el-dialog
            :title="title"
            :visible.sync="dialogFormVisible"
            :before-close="closeDialog"
            fullscreen
        >
            <el-form :model="form" :rules="rules" ref="form">

                <el-row>
                    <el-col :span="16">
                        <el-form-item label="标题" :label-width="formLabelWidth" prop="title">
                            <el-input v-model="form.title" auto-complete="off" @input="contentChange"></el-input>
                        </el-form-item>

                        <el-form-item label="简介" :label-width="formLabelWidth">
                            <el-input v-model="form.summary" auto-complete="off" @input="contentChange"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="6.5">
                        <el-form-item label="标签" :label-width="formLabelWidth">
                            <el-select
                                v-model="tagValue"
                                multiple
                                size="small"
                                placeholder="请选择"
                                style="width:210px"
                                filterable
                            >
                                <el-option
                                    v-for="item in tagData"
                                    :key="item.uid"
                                    :label="item.name"
                                    :value="item.uid"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :span="6.5">
                        <el-form-item label="文章评论" :label-width="formLabelWidth" prop="openComment">
                            <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.openComment"
                                      :label="item.dictValue" border size="small">{{ item.dictLabel }}
                            </el-radio>
                        </el-form-item>
                    </el-col>

                    <el-col :span="4.5">
                        <el-form-item label="是否发布" :label-width="lineLabelWidth" prop="isPublish">
                            <el-radio-group v-model="form.isPublish" size="small">
                                <el-radio v-for="item in blogPublishDictList" :key="item.uid" :label="item.dictValue"
                                          border>{{ item.dictLabel }}
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>

                </el-row>

                <el-form-item label="内容" :label-width="formLabelWidth" prop="content">
                    <CKEditor v-if="systemConfig.editorModel == '0'" ref="editor" :content="form.content"
                              @contentChange="contentChange" :height="360"></CKEditor>
                    <Mavon v-if="systemConfig.editorModel == '1'" :content="form.content" ref="editor"
                           :height="465"></Mavon>
                </el-form-item>

                <el-form-item style="float: right; margin-right: 20px;">
                    <el-button @click="dialogFormVisible = false">取 消</el-button>
                    <el-button type="primary" @click="submitForm(0)">确 定</el-button>
                </el-form-item>
            </el-form>
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
import {addQuestion, auditQuestion, deleteBatchQuestion, editQuestion, getQuestionList} from "@/api/question";
import {getSystemConfig} from "@/api/systemConfig";
import {getQuestionTagList} from "@/api/questionTag";
import {getCookie} from "@/utils/cookieUtils";
import {getListByDictTypeList} from "@/api/sysDictData"
import CheckPhoto from "../../components/CheckPhoto";
import CKEditor from "../../components/CKEditor";
import MarkdownEditor from "../../components/MarkdownEditor";
import Mavon from "../../components/Mavon";
import {Loading} from 'element-ui';
import UserAvatar from "../../components/UserAvatar"

let querystring = require("querystring");

export default {
    components: {
        CheckPhoto,
        CKEditor,
        MarkdownEditor,
        Mavon,
        UserAvatar,
    },
    data() {
        return {
            queryParams: {
                keyword: "",
                tagKeyword: "", //标签搜索
                levelKeyword: "", //等级搜索
                publishKeyword: "", // 发布 搜索
            }, // 搜索条件
            showSearch: null, // 显示搜索条件
            pictureList: [], // 上传的图片列表
            BLOG_WEB_URL: process.env.BLOG_WEB_URL,
            multipleSelection: [], //多选，用于批量删除
            tagOptions: [], //标签候选框
            loading: false, //搜索框加载状态
            uploadLoading: null, //文件上传loading
            CKEditorData: null,
            tableData: [], //问答数据
            tagData: [], //标签数据
            tagValue: [], //保存选中标签id(编辑时)
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "增加问答",
            dialogFormVisible: false, //控制弹出框
            subjectVisible: false, // 是否显示专题
            isFirstSubjectVisible: true, // 专题选择器是否首次显示【用于懒加载】
            formLabelWidth: "120px",
            lineLabelWidth: "120px", //一行的间隔数
            maxLineLabelWidth: "100px",
            isEditForm: false,
            photoVisible: false, //控制图片选择器的显示
            isFirstPhotoVisible: true, // 图片选择器是否首次显示【用于懒加载】
            photoList: [],
            fileIds: "",
            icon: false, //控制删除图标的显示
            interval: null, //定义触发器
            isChange: false, // 表单内容是否改变
            changeCount: 0, // 改变计数器
            blogPublishDictList: [], //是否字典
            questionStatusDictList: [], // 问答状态字典
            openDictList: [], // 是否启动字典
            articleDictList: [], // 文章来源
            blogPublishDefault: null, //问答发布默认值
            openDefault: null, // 是否开启评论默认值
            questionStatusDefault: null, // 问答状态默认值
            fileList: [],
            localUploadVisible: false,
            systemConfig: {}, // 系统配置
            orderByDescColumn: "createTime", // 降序字段
            orderByAscColumn: "", // 升序字段
            isBlack: 1,
            auditForm: {},
            auditDialogVisible: false, // 审批结果
            auditStatusDictList: [], // 审批状态字典
            form: {
                uid: null,
                title: null,
                summary: null,
                content: null,
                questionTagUid: null,
                fileUid: null,
                isPublish: null,
                author: null, //作者
                clickCount: 0
            },
            typeList: ['warning', 'danger', 'success', 'info', 'primary'],
            rules: {
                title: [
                    {required: true, message: '标题不能为空', trigger: 'blur'}
                ],
                isPublish: [
                    {required: true, message: '发布字段不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '发布字段只能为自然数'},
                ],
                isOriginal: [
                    {required: true, message: '原创字段不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '原创字段只能为自然数'},
                ],
                openComment: [
                    {required: true, message: '网站评论不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '网站评论只能为自然数'},
                ],
                content: [
                    {required: true, message: '内容不能为空', trigger: 'blur'}
                ],
                outsideLink: [
                    {required: true, message: '外链地址不能为空', trigger: 'blur'},
                    {pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入有效的URL'},
                ],
            }
        };
    },
    created() {
        let tempTag = this.$route.query.tag;
        if (tempTag != undefined) {
            this.tagRemoteMethod(tempTag.name);
            this.queryParams.tagKeyword = tempTag.tagUid;
        }
        // 判断是否需要展开条件查询
        this.getShowSearch()
        // 获取系统配置
        this.getSystemConfigList()
        // 获取字典
        this.getDictList()
        // 获取标签列表
        this.tagList()
        //获取问答列表
        this.questionList()
    },
    methods: {
        submitAudit: function () {
            let params = {}
            params.uid = this.auditForm.uid
            params.auditStatus = this.auditForm.auditStatus
            params.rejectReason = this.auditForm.rejectReason
            auditQuestion(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.$commonUtil.message.success(response.message)
                    this.auditDialogVisible = false
                    this.questionList()
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
            this.questionList()
        },
        handChangeQuestion(row) {
            this.title = "编辑问答";
            let that = this
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
            that.isEditForm = true;
            this.submitForm(1)
        },
        openLoading() {
            this.uploadLoading = Loading.service({
                lock: true,
                text: '正在努力上传中……'
            })
        },
        closeLoading() {
            this.uploadLoading.close()
        },
        // 判断是否需要展开条件查询
        getShowSearch: function () {
            let showSearch = getCookie("showSearch")
            if (showSearch == "false") { //此时的hasAuth是true
                this.showSearch = false
            } else {
                this.showSearch = true
            }
        },
        tagList: function () {
            let tagParams = {};
            tagParams.pageSize = 500;
            tagParams.currentPage = 1;
            getQuestionTagList(tagParams).then(response => {
                this.tagData = response.data.records;
                this.tagOptions = response.data.records;
            });
        },
        resetBlogList: function () {
            this.queryParams = {}
            this.questionList();
        },
        questionList: function () {
            let params = {};
            params.isBlack = this.isBlack;
            params.keyword = this.queryParams.keyword;
            params.tagUid = this.queryParams.tagKeyword;
            ;
            params.isPublish = this.queryParams.publishKeyword;
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getQuestionList(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
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
                }
            });
        },
        /**
         * 字典查询
         */
        getDictList: function () {

            let dictTypeList = ['sys_publish_status', 'sys_normal_disable', 'sys_question_status', 'sys_article_source', 'sys_audit_status']
            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    let dictMap = response.data;
                    this.blogPublishDictList = dictMap.sys_publish_status.list
                    this.openDictList = dictMap.sys_normal_disable.list
                    this.questionStatusDictList = dictMap.sys_question_status.list
                    this.articleDictList = dictMap.sys_article_source.list
                    this.auditStatusDictList = dictMap.sys_audit_status.list
                    if (dictMap.sys_publish_status.defaultValue) {
                        this.blogPublishDefault = dictMap.sys_publish_status.defaultValue;
                    }
                    if (dictMap.sys_normal_disable.defaultValue) {
                        this.openDefault = dictMap.sys_normal_disable.defaultValue;
                    }
                    if (dictMap.sys_question_status.defaultValue) {
                        this.questionStatusDefault = dictMap.sys_question_status.defaultValue;
                    }
                }
            });
        },
        getFormObject: function () {
            let formObject = {
                uid: null,
                title: null,
                summary: null,
                content: null,
                tagUid: null,
                fileUid: null,
                isPublish: this.blogPublishDefault, //是否发布
                type: this.blogTypeDefault, // 文章类型
                author: null, //作者
                openComment: this.openDefault, // 是否启动
            };
            return formObject;
        },
        // 跳转到用户中心
        goUser: function (question) {
            console.log("go user", question)
            if (question.questionSource == "0") {
                this.$router.push({path: "/authority/admin", query: {nickName: question.user.nickName}});
            } else if (question.questionSource == "1") {
                this.$router.push({path: "/user/user", query: {nickName: question.user.nickName}});
            }
        },
        // 跳转到该问答详情
        onClick: function (row) {
            if (row.isPublish == 0) {
                this.$message.error("问答暂未发布，无法进行浏览")
                return
            }
            window.open(this.BLOG_WEB_URL + "/#/qInfo?questionOid=" + row.oid + "&title=问答");
        },
        //标签远程搜索函数
        tagRemoteMethod: function (query) {
            if (query !== "") {
                let params = {};
                params.keyword = query;
                params.pageSize = 10;
                params.currentPage = 1;
                getQuestionTagList(params).then(response => {
                    this.tagOptions = response.data.records;
                });
            } else {
                this.tagOptions = [];
            }
        },
        // 获取系统配置
        getSystemConfigList: function () {
            getSystemConfig().then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    if (response.data) {
                        this.systemConfig = response.data;
                    }
                }
            });
        },
        submitStr: function (str, index) {
            if (str.length > index) {
                return str.slice(0, index) + "...";
            }
            return str;
        },

        // 关闭窗口
        closeDialog(done) {
            if (this.isChange) {
                this.$confirm("是否关闭问答编辑窗口", "提示", {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "warning"
                })
                    .then(() => {
                        this.isChange = false;
                        this.changeCount = 0
                        done();
                    })
                    .catch(() => {
                        this.$commonUtil.message.info("已取消")
                    });
            } else {
                this.isChange = false;
                this.changeCount = 0
                done();
            }
        },
        handleFind: function () {
            this.currentPage = 1
            this.questionList();
        },
        handleAdd: function () {
            this.title = "增加问答"
            let that = this
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.$nextTick(() => {
                //初始化内容
                that.$refs.editor.initData();
            });
            this.tagValue = [];
            this.isEditForm = false;
        },
        // 内容改变，触发监听
        contentChange: function () {
            let that = this;
            if (this.changeCount > 1) {
                that.isChange = true;
                that.form.content = that.$refs.editor.getData(); //获取CKEditor中的内容
                that.form.tagUid = that.tagValue.join(",");
                // 将内容设置到 WebStorage中
                window.LS.set("form", JSON.stringify(that.form));
            }
            this.changeCount = this.changeCount + 1;
        },
        handleEdit: function (row) {
            let that = this;
            this.title = "编辑问答";
            this.form = row;
            this.$nextTick(() => {
                //DOM现在更新了
                that.$refs.editor.setData(that.form.content); //设置富文本内容
            });
            that.tagValue = [];
            if (row.questionTagList) {
                let json = row.questionTagList;
                for (let i = 0, l = json.length; i < l; i++) {
                    if (json[i] != null) {
                        that.tagValue.push(json[i]["uid"]);
                    }
                }
            }
            that.dialogFormVisible = true;
            that.isEditForm = true;
        },
        handleDelete: function (row) {
            let that = this;
            this.$confirm("此操作将把问答删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let params = {};
                    params.uid = row.uid;
                    let data = [params]
                    deleteBatchQuestion(data).then(response => {
                        that.$commonUtil.message.success(response.message)
                        that.questionList();
                    });
                })
                .catch(() => {
                    that.$commonUtil.message.info("已取消删除")
                });
        },
        handleDeleteBatch: function (row) {
            let that = this;
            if (that.multipleSelection.length <= 0) {
                that.$commonUtil.message.error("请先选中需要删除的问答")
                return;
            }
            this.$confirm("此操作将把选中问答删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    deleteBatchQuestion(that.multipleSelection).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            that.$commonUtil.message.success(response.message)
                            that.questionList();
                        } else {
                            that.$commonUtil.message.error(response.message)
                        }
                    });
                })
                .catch(() => {
                    that.$commonUtil.message.info("已取消删除")
                });
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.questionList();
        },
        submitForm: function (submitType = 0) {

            if (submitType == 0) {
                if (this.tagValue.length <= 0) {
                    this.$commonUtil.message.error("标签不能为空!")
                    return;
                }
                this.$refs.form.validate((valid) => {
                    if (!valid) {

                    } else {
                        this.form.content = this.$refs.editor.getData(); //获取CKEditor中的内容
                        this.form.questionTagUid = this.tagValue.join(",");
                        if (this.isEditForm) {
                            editQuestion(this.form).then(response => {
                                if (response.code == this.$ECode.SUCCESS) {
                                    this.$commonUtil.message.success(response.message)
                                    this.dialogFormVisible = false;
                                    this.questionList();
                                } else {
                                    this.$commonUtil.message.error(response.message)
                                }
                            });

                        } else {
                            addQuestion(this.form).then(response => {
                                if (response.code == this.$ECode.SUCCESS) {
                                    this.$commonUtil.message.success(response.message)
                                    this.dialogFormVisible = false;
                                    this.questionList();
                                } else {
                                    this.$commonUtil.message.error(response.message)
                                }
                            });
                        }
                    }
                })
            } else {
                // 未打开编辑框的方式提交
                editQuestion(this.form).then(response => {
                    if (response.code == this.$ECode.SUCCESS) {
                        this.$commonUtil.message.success(response.message)
                        this.dialogFormVisible = false;
                        this.questionList();
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


</style>
