<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;">
            <el-input clearable class="filter-item" style="width: 200px;" v-model="queryParams.content" size="small"
                      placeholder="请输入评论内容"></el-input>
            <el-input clearable class="filter-item" style="width: 150px;" v-model="queryParams.userName" size="small"
                      placeholder="请输入评论人"></el-input>

            <el-select v-model="queryParams.type" clearable placeholder="评论类型" size="small"
                       style="margin-left:5px;width:110px">
                <el-option
                    v-for="item in commentTypeDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                ></el-option>
            </el-select>

            <el-select v-model="queryParams.source" clearable placeholder="评论来源" size="small"
                       style="margin-left:5px;width:110px">
                <el-option
                    v-for="item in commentSourceDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                ></el-option>
            </el-select>

            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFind" size="small"
                       v-permission="'/comment/getList'">查找
            </el-button>
            <el-button class="filter-item" type="danger" @click="handleDeleteBatch" icon="el-icon-delete" size="small"
                       v-permission="'/comment/deleteBatch'">删除选中
            </el-button>
        </div>

        <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">

            <el-table-column type="selection"></el-table-column>

            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.$index + 1 }}</span>
                </template>
            </el-table-column>

            <el-table-column label="评论人" width="200" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.user"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="被评论人" width="200" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.toUser"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="类型" width="80" align="center">
                <template slot-scope="scope">
                    <template>
                        <el-tag type="danger" v-if="scope.row.type == 1">点赞</el-tag>
                        <el-tag type="success" v-if="scope.row.type == 0">评论</el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="来源" width="80" align="center">
                <template slot-scope="scope">
                    <template>
                        <el-tag type="warning" @click.native="goPage(scope.row.source, scope.row)"
                                style="cursor: pointer;">{{ scope.row.sourceName }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <!--        <el-table-column label="来源博客" width="160" align="center">-->
            <!--          <template slot-scope="scope">-->
            <!--            <template>-->
            <!--              <el-tag type="error" v-if="scope.row.source == 'BLOG_INFO'" @click.native="onClick(scope.row.blog)" style="cursor: pointer;">{{subStr(scope.row.blog.title, 8 )}}</el-tag>-->
            <!--            </template>-->
            <!--          </template>-->
            <!--        </el-table-column>-->

            <el-table-column label="内容" width="300" align="center">
                <template slot-scope="scope">
                    <!--            <el-popover-->
                    <!--              v-if="scope.row.content"-->
                    <!--              placement="top-start"-->
                    <!--              width="400"-->
                    <!--              trigger="hover"-->
                    <!--              :content="scope.row.content">-->
                    <!--              <el-button slot="reference">{{subStr(scope.row.content, 10)}}</el-button>-->
                    <!--            </el-popover>-->
                    <span v-html="$xss(scope.row.content, options)"></span>
                </template>
            </el-table-column>

            <el-table-column label="创建时间" width="160" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
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
                            @change="handleSubmit(scope.row)"
                        />
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="操作" fixed="right" min-width="150">
                <template slot-scope="scope">

                    <el-button @click="handleReply(scope.row)" type="primary" size="mini">回复</el-button>

                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini"
                               v-permission="'/comment/delete'">删除
                    </el-button>

                    <el-button v-if="(scope.row.auditStatus == 0 || scope.row.auditStatus == 1)"
                               @click="handleAudit(scope.row)" type="success" size="mini"
                               v-permission="'/comment/audit'">审核
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

        <!--  评论回复框  -->
        <el-dialog
            title="评论回复"
            :visible.sync="replyDialogVisible"
            width="30%"
            center>
            <div style="text-align: center">
                <el-form>
                    <el-form-item label="回复内容" :label-width="lineLabelWidth" prop="auditStatus">
                        <el-input  maxlength="50" placeholder="请输入回复内容" v-model="replyContent"
                                  clearable></el-input>
                    </el-form-item>
                </el-form>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="replyDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitReply">确 定</el-button>
            </span>
        </el-dialog>

    </div>
</template>

<script>
import {auditComment, deleteBatchComment, deleteComment, editComment, getCommentList, addComment} from "@/api/comment";
import {getListByDictTypeList} from "@/api/sysDictData"
import UserAvatar from "../../components/UserAvatar/index.vue";

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
                    img: ['src', 'alt', 'class', 'style', 'cursor'],
                    ul: ['class'],
                    li: ['class'],
                    br: [],
                }
            },
            queryParams: {
                content: null, //评论名
                userName: null, //用户名
                type: null, //类型
                source: null, //来源
            }, //查询参数
            multipleSelection: [], //多选，用于批量删除
            BLOG_WEB_URL: process.env.BLOG_WEB_URL,
            tableData: [],
            keyword: "",
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "增加友链",
            dialogFormVisible: false, //控制弹出框
            formLabelWidth: '120px',
            isEditForm: false,
            commentTypeDictList: [], //评论类型字典
            commentSourceDictList: [], //评论来源字典
            commentTypeDefaultValue: null, // 评论类型默认值
            defaultAvatar: process.env.defaultAvatar, // 默认头像
            auditForm: {},
            auditDialogVisible: false, // 审批结果
            auditStatusDictList: [], // 审批状态字典
            lineLabelWidth: "120px", //一行的间隔数
            replyDialogVisible:false, // 回复框
            replyContent: "", // 回复内容
        };
    },
    components: {
        UserAvatar,
    },
    created() {
        // 获取评论
        this.commentList();

        // 获取字典
        this.getDictList()
    },
    methods: {
        submitAudit: function () {
            let params = {}
            params.uid = this.auditForm.uid
            params.auditStatus = this.auditForm.auditStatus
            params.rejectReason = this.auditForm.rejectReason
            auditComment(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.$commonUtil.message.success(response.message)
                    this.auditDialogVisible = false
                    this.commentList()
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
        // 跳转到用户中心
        goUser: function (user) {
            console.log("go user", user)
            this.$router.push({path: "/user/user", query: {nickName: user.nickName}});
        },
        // 跳转到该博客详情
        onClick: function (row) {
            console.log("点击跳转", row)
            window.open(this.BLOG_WEB_URL + "/info/" + row.oid);
        },
        // 跳转到前端页面
        goPage: function (type, row) {
            switch (type) {
                case 'MESSAGE_BOARD': {
                    window.open(this.BLOG_WEB_URL + "/messageBoard")
                }
                    break;
                case 'ABOUT': {
                    window.open(this.BLOG_WEB_URL + "/about")
                }
                    break;
                case 'BLOG_INFO': {
                    window.open(this.BLOG_WEB_URL + "/info/" + row.blog.oid);
                }
                    break;
                case 'USER_MOMENT': {
                    window.open(this.BLOG_WEB_URL + "/moment")
                }
                    break;
                case 'QUESTION_INFO': {
                    window.open(this.BLOG_WEB_URL + "/qInfo/" + row.question.oid)
                }
                    break;
                case 'PROBLEM_INFO': {
                    window.open(this.BLOG_WEB_URL + "/cInfo/" + row.problem.oid);
                }
                    break;
            }
        },
        /**
         * 字典查询
         */
        getDictList: function () {
            let dictTypeList = ['sys_comment_type', 'sys_comment_source', 'sys_audit_status']
            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    var dictMap = response.data;
                    this.commentTypeDictList = dictMap.sys_comment_type.list
                    this.commentSourceDictList = dictMap.sys_comment_source.list
                    this.auditStatusDictList = dictMap.sys_audit_status.list
                    if (dictMap.sys_comment_type.defaultValue) {
                        this.commentTypeDefaultValue = dictMap.sys_comment_type.defaultValue
                        this.queryParams.type = this.commentTypeDefaultValue
                        this.commentList()
                    }
                }
            });
        },
        commentList: function () {
            let params = {}
            params.keyword = this.queryParams.content
            if (this.queryParams.source == null || this.queryParams.source == undefined || this.queryParams.source == '') {
                params.source = "all"
            } else {
                params.source = this.queryParams.source
            }
            params.userName = this.queryParams.userName
            params.type = this.queryParams.type
            params.currentPage = this.currentPage
            params.pageSize = this.pageSize

            getCommentList(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    let tableData = response.data.records

                    for (let i = 0; i < tableData.length; i++) {
                        if (tableData[i].isTop == 1) {
                            tableData[i].openTopStatus = true
                        } else {
                            tableData[i].openTopStatus = false
                        }
                    }

                    this.tableData = tableData;
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
            this.currentPage = 1
            this.commentList();
        },
        handleReply: function (row) {
            this.replyDialogVisible = true
            this.replyDialogVisible = ""
            this.form = row
        },
        submitReply: function () {
            if (this.replyContent == "") {
                this.$commonUtil.message.error("回复内容不能为空")
                return
            }
            let form = this.form
            let params = {}
            // 构造回复
            params.userUid = form.userUid;
            params.content = this.replyContent;
            params.blogUid = form.blogUid;
            params.toUid =   form.uid;
            params.toUserUid = form.userUid;
            params.source = form.source
            addComment(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.$commonUtil.message.success(response.message)
                    this.replyContent = ""
                    this.replyDialogVisible = false
                    this.commentList();
                } else {
                    this.$commonUtil.message.error(response.message)
                }
            })
        },
        handleDelete: function (row) {
            this.$confirm("此操作将把该评论删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let params = {}
                    params.uid = row.uid
                    deleteComment(params).then(response => {
                        this.$commonUtil.message.success(response.message)
                        this.commentList();
                    })
                })
                .catch(() => {
                    this.$commonUtil.info("已取消删除")
                });
        },
        handleDeleteBatch: function () {
            var that = this;
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
                    deleteBatchComment(that.multipleSelection).then(response => {
                        if (response.code == that.$ECode.SUCCESS) {
                            that.$commonUtil.message.success(response.message)
                        } else {
                            that.$commonUtil.message.error(response.message)
                        }
                        that.commentList();
                    });
                })
                .catch(() => {
                    that.$commonUtil.info("已取消删除")
                });
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.commentList();
        },
        // 改变多选
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        handleSubmit(row) {
            console.log("切换", row)
            if (row.openTopStatus) {
                row.isTop = 1
            } else {
                row.isTop = 0
            }
            // 未打开编辑框的方式提交
            editComment(row).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.commentList()
                } else {
                    this.$commonUtil.message.error(response.message)
                }
            });

        },
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
</style>

