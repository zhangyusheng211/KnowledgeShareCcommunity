<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;">
            <el-input
                @keyup.enter.native="handleFind"
                class="filter-item"
                clearable
                placeholder="请输入关键词"
                style="width: 150px;"
                v-model="keyword"
                size="small"
            ></el-input>

            <el-select clearable placeholder="账号类型" style="width:140px" size="small" v-model="accountSourceKeyword">
                <el-option
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                    v-for="item in accountSourceDictList"
                ></el-option>
            </el-select>

            <el-select clearable placeholder="评论状态" style="width:140px" size="small" v-model="commentStatusKeyword">
                <el-option
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                    v-for="item in commentStatusDictList"
                ></el-option>
            </el-select>

            <el-select clearable placeholder="用户标签" style="width:140px" size="small" v-model="userTagKeyword">
                <el-option
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                    v-for="item in userTagDictList"
                ></el-option>
            </el-select>

            <el-button @click="handleFind" class="filter-item" icon="el-icon-search" type="primary" size="small"
                       v-permission="'/user/getList'">查找
            </el-button>

            <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit" size="small"
                       v-permission="'/user/add'">添加用户
            </el-button>
        </div>

        <el-table :data="tableData"
                  style="width: 100%"
                  @sort-change="changeSort"
                  :default-sort="{prop: 'createTime', order: 'descending'}">
            <el-table-column type="selection"></el-table-column>

            <el-table-column align="center" label="序号" width="60">
                <template slot-scope="scope">
                    <span>{{ scope.$index + 1 }}</span>
                </template>
            </el-table-column>

            <el-table-column align="center" label="用户" width="200">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row"></UserAvatar>
                </template>
            </el-table-column>


            <el-table-column align="center" label="账号来源" width="100" prop="source" sortable="custom"
                             :sort-by="['source']">
                <template slot-scope="scope">
                    <template>
                        <el-tag :key="item.uid" :type="item.listClass" v-for="item in accountSourceDictList"
                                v-if="scope.row.source == item.dictValue">{{ item.dictLabel }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column align="center" label="评论状态" width="100" prop="commentStatus" sortable="custom"
                             :sort-by="['commentStatus']">
                <template slot-scope="scope">
                    <template>
                        <el-tag :key="item.uid" :type="item.listClass" v-for="item in commentStatusDictList"
                                v-if="scope.row.commentStatus == item.dictValue">{{ item.dictLabel }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column align="center" label="用户标签" width="100">
                <template slot-scope="scope">
                    <template>
                        <el-tag :key="item.uid" :type="item.listClass" v-for="item in userTagDictList"
                                v-if="scope.row.userTag == item.dictValue">{{ item.dictLabel }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column align="center" label="登录次数" width="100" prop="loginCount" sortable="custom"
                             :sort-by="['loginCount']">
                <template slot-scope="scope">
                    <span>{{ scope.row.loginCount }}</span>
                </template>
            </el-table-column>

            <el-table-column align="center" label="登录IP" width="160">
                <template slot-scope="scope">
                    <span>{{ scope.row.lastLoginIp }}</span>
                </template>
            </el-table-column>

            <el-table-column align="center" label="操作系统" width="160">
                <template slot-scope="scope">
                    <span>{{ scope.row.os }}</span>
                </template>
            </el-table-column>

            <el-table-column align="center" label="浏览器" width="160">
                <template slot-scope="scope">
                    <span>{{ scope.row.browser }}</span>
                </template>
            </el-table-column>

            <el-table-column align="center" label="最后登录时间" width="160" prop="lastLoginTime" sortable="custom"
                             :sort-by="['lastLoginTime']">
                <template slot-scope="scope">
                    <span>{{ scope.row.lastLoginTime }}</span>
                </template>
            </el-table-column>

            <el-table-column align="center" label="经验值" width="80">
                <template slot-scope="scope">
                    <el-tag type="success">{{scope.row.expValue}}</el-tag>
                </template>
            </el-table-column>

            <el-table-column align="center" label="积分" width="80">
                <template slot-scope="scope">
                    <el-tag type="primary">{{scope.row.credits}}</el-tag>
                </template>
            </el-table-column>


            <el-table-column align="center" label="IP来源" width="160">
                <template slot-scope="scope">
                    <span>{{ scope.row.ipSource }}</span>
                </template>
            </el-table-column>

            <el-table-column label="创建时间" width="160" align="center" prop="createTime" sortable="custom"
                             :sort-by="['createTime']">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="更新时间" width="160" align="center" prop="createTime" sortable="custom"
                             :sort-by="['updateTime']">
                <template slot-scope="scope">
                    <span>{{ scope.row.updateTime }}</span>
                </template>
            </el-table-column>

            <el-table-column align="center" label="邮箱" width="200">
                <template slot-scope="scope">
                    <span>{{ scope.row.email }}</span>
                </template>
            </el-table-column>

            <el-table-column align="center" label="背景图" width="200">
                <template slot-scope="scope">
                    <el-image :src="scope.row.backgroundFileUrl" v-if="scope.row.backgroundFileUrl"
                              :preview-src-list="[scope.row.backgroundFileUrl]"></el-image>
                </template>
            </el-table-column>

            <el-table-column label="状态" width="100">
                <template slot-scope="scope">
                    <template v-if="scope.row.status == 1">
                        <span>正常</span>
                    </template>
                    <template v-if="scope.row.status == 2">
                        <span>冻结</span>
                    </template>
                    <template v-if="scope.row.status == 0">
                        <span>已删除</span>
                    </template>
                </template>
            </el-table-column>

            <el-table-column fixed="right" label="操作" min-width="270">
                <template slot-scope="scope">

                    <el-row>
                        <el-button @click="handleEdit(scope.row)" size="mini" type="primary"
                                   v-permission="'/user/edit'">编辑
                        </el-button>
                        <el-button @click="showCredits(scope.row.uid,1)" size="mini" type="info"
                                   v-permission="'/credits/updateCredits'">积分补偿
                        </el-button>
                        <el-button @click="updateCredits(scope.row)" size="mini" type="primary"
                                   v-permission="'/credits/queryCredits'">积分变更
                        </el-button>

                    </el-row>

                    <el-row>
                        <el-button style="margin-top: 10px;" @click="handleDelete(scope.row)" size="mini" type="danger"
                                   v-permission="'/user/delete'">删除
                        </el-button>
                        <el-button style="margin-top: 10px;" @click="handleUpdatePassword(scope.row)" size="mini"
                                   type="warning"
                                   v-permission="'/user/resetUserPassword'">重置密码
                        </el-button>
                    </el-row>
                </template>
            </el-table-column>
        </el-table>

        <!--分页-->
        <div class="block">
            <el-pagination
                :current-page.sync="currentPage"
                :page-size="pageSize"
                :total="total"
                @current-change="handleCurrentChange"
                layout="total, prev, pager, next, jumper"
            ></el-pagination>
        </div>

        <el-dialog title="用户积分记录" :visible.sync="creditsLogListFormVisible">
            <el-table
                :data="creditsLog.tableData"
                border
                style="width: 100%">
                <el-table-column
                    fixed
                    prop="actionName"
                    label="动作">
                </el-table-column>
                <el-table-column
                    prop="title"
                    label="标题">
                </el-table-column>
                <el-table-column
                    prop="changeCredits"
                    label="积分">
                </el-table-column>
                <el-table-column prop="hasGet" label="是否发放">
                    <template slot-scope="scope">
                        <el-tag
                            :type="scope.row.hasGet === '已发放' ? 'success' : 'danger'"
                            disable-transitions>{{ scope.row.hasGet }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column
                    fixed="right"
                    label="操作">
                    <template slot-scope="scope">
                        <el-button v-if="scope.row.hasGet == '未发放'" @click="compensation(scope.row)" type="primary"
                                   size="small" v-permission="'/credits/compensation'">补偿
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="block">
                <el-pagination
                    :current-page.sync="creditsLog.currentPage"
                    :page-size="creditsLog.pageSize"
                    :total="creditsLog.total"
                    @current-change="handleCreditsCurrentChange"
                    layout="total, prev, pager, next, jumper"
                ></el-pagination>
            </div>
        </el-dialog>

        <el-dialog title="变更用户积分" :visible.sync="updateCreditsVisible">
            <el-form :model="creditsLogVO" :rules="updateCreditsRules" ref="form">
                <el-form-item :label-width="formLabelWidth" label="变更积分" prop="changeCredits">
                    <el-input-number v-model="creditsLogVO.changeCredits" label="请输入需要变更的积分：正数表示新增，负数表示扣除"></el-input-number>
                    <el-tag style="margin-left: 10px;">注：正数表示新增，负数表示扣除</el-tag>
                </el-form-item>
                <el-form-item :label-width="formLabelWidth" label="变更原因" prop="remark">
                    <el-input v-model="creditsLogVO.remark" placeholder="请输入积分变更的具体原因"></el-input>
                </el-form-item>
                <el-form-item :label-width="formLabelWidth" label="站内信触达" prop="openMessagePush">
                    <el-radio v-for="item in yesNoDictList" :key="item.uid" v-model="creditsLogVO.openMessagePush"
                              :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                    </el-radio>
                </el-form-item>

            </el-form>

            <div class="dialog-footer" slot="footer">
                <el-button @click="updateCreditsVisible = false">取 消</el-button>
                <el-button @click="submitUpdateCredits" type="primary">确 定</el-button>
            </div>
        </el-dialog>

        <!-- 添加或修改对话框 -->
        <el-dialog :title="title" :visible.sync="dialogFormVisible">
            <el-form :model="form" :rules="rules" ref="form">
                <el-form-item :label-width="formLabelWidth" label="用户头像">
                    <div class="imgBody" v-if="form.photoUrl">
                        <i @click="deletePhoto()" @mouseover="icon = true" class="el-icon-error inputClass"
                           v-show="icon"></i>
                        <img @mouseout="icon = false" @mouseover="icon = true" v-bind:src="form.photoUrl"/>
                    </div>

                    <div @click="checkPhoto" class="uploadImgBody" v-else>
                        <i class="el-icon-plus avatar-uploader-icon"></i>
                    </div>
                </el-form-item>

                <el-row :gutter="24">
                    <el-col :span="9">
                        <el-form-item :label-width="formLabelWidth" label="用户名" prop="userName">
                            <el-input v-model="form.userName"></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :span="9">
                        <el-form-item :label-width="formLabelWidth" label="昵称" prop="nickName">
                            <el-input v-model="form.nickName"></el-input>
                        </el-form-item>
                    </el-col>

                </el-row>

                <el-row :gutter="24">
                    <el-col :span="9">
                        <el-form-item :label-width="formLabelWidth" label="QQ号" prop="qqNumber">
                            <el-input auto-complete="off" v-model="form.qqNumber"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="9">
                        <el-form-item :label-width="formLabelWidth" label="职业">
                            <el-input auto-complete="off" v-model="form.occupation"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="24">
                    <el-col :span="9">
                        <el-form-item :label-width="formLabelWidth" label="邮箱" prop="email">
                            <el-input auto-complete="off" v-model="form.email"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="24">
                    <el-col :span="5">
                        <el-form-item label="评论状态" :label-width="formLabelWidth" prop="commentStatus">
                            <el-select placeholder="请选择" style="width:100px" v-model="form.commentStatus" size="small">
                                <el-option
                                    :key="item.uid"
                                    :label="item.dictLabel"
                                    :value="parseInt(item.dictValue)"
                                    v-for="item in commentStatusDictList"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :span="6">
                        <el-form-item label="用户标签" :label-width="formLabelWidth" prop="userTag">
                            <el-select placeholder="请选择" style="width:120px" v-model="form.userTag" size="small">
                                <el-option
                                    :key="item.uid"
                                    :label="item.dictLabel"
                                    :value="parseInt(item.dictValue)"
                                    v-for="item in userTagDictList"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :span="5">
                        <el-form-item label="用户状态" :label-width="formLabelWidth" prop="userTag">
                            <el-select placeholder="请选择" style="width:120px" v-model="form.status" size="small">
                                <el-option
                                    :key="item.uid"
                                    :label="item.dictLabel"
                                    :value="parseInt(item.dictValue)"
                                    v-for="item in dataStatusDictList"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="性别" :label-width="formLabelWidth" prop="gender">
                            <el-radio :key="gender.uid" :label="gender.dictValue" border
                                      size="small" v-for="gender in genderDictList" v-model="form.gender">
                                {{ gender.dictLabel }}
                            </el-radio>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-form-item label="用户权限" :label-width="formLabelWidth">
                    <el-checkbox-group v-model="authCodeList">
                        <el-checkbox :label="item.dictValue" :key="item.uid"  v-for="item in authCodeDictList">{{item.dictLabel}}</el-checkbox>
                    </el-checkbox-group>
                </el-form-item>

                <el-form-item :label-width="formLabelWidth" label="简介">
                    <el-input
                        :autosize="{ minRows: 3, maxRows: 10}"
                        placeholder="请输入内容"
                        style="width: 70%"
                        type="textarea"
                        v-model="form.summary">
                    </el-input>
                </el-form-item>

            </el-form>
            <div class="dialog-footer" slot="footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button @click="submitForm" type="primary">确 定</el-button>
            </div>
        </el-dialog>



        <avatar-cropper
            :height="300"
            :key="imagecropperKey"
            :url="url"
            :width="300"
            @close="close"
            @crop-upload-success="cropSuccess"
            lang-type="zh"
            v-show="imagecropperShow"
        />
    </div>
</template>

<script>
import {
    addUser,
    compensationByUserUid,
    deleteUser,
    editUser,
    getUserList,
    queryCreditsByUserUid,
    resetUserPassword
} from "@/api/user";
import {
    updateCredits
} from "@/api/credits";

import AvatarCropper from '@/components/AvatarCropper'
import {getListByDictTypeList} from "@/api/sysDictData"
import UserAvatar from "../../components/UserAvatar"

export default {
    data() {
        return {
            authCodeList: [],
            photoList: [],
            fileIds: "",
            icon: false, //控制删除图标的显示
            imagecropperShow: false,
            imagecropperKey: 0,
            url: process.env.PICTURE_API + "/file/cropperPicture",
            tableData: [],
            keyword: "",
            accountSourceKeyword: "",
            commentStatusKeyword: "",
            userTagKeyword: "",
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "增加用户",
            dialogFormVisible: false, //控制弹出框
            creditsLogListFormVisible: false, //积分记录弹框
            creditsLog: {
                userUid: '',
                tableData: [],
                currentPage: 1,
                pageSize: 10,
                total: 0, //总数量
            },
            formLabelWidth: "120px",
            isEditForm: false,
            form: {
                uid: null,
            },
            accountSourceDictList: [], //账号来源字典
            commentStatusDictList: [], //评论状态字典
            genderDictList: [], //评论状态字典
            userTagDictList: [], // 用户标签列表
            dataStatusDictList: [], // 数据状态列表
            authCodeDictList: [], // 用户权限Code
            yesNoDictList: [], // 是否枚举
            defaultAvatar: process.env.defaultAvatar, // 默认头像
            orderByDescColumn: "", // 降序字段
            orderByAscColumn: "", // 升序字段
            updateCreditsVisible: false, // 积分变更
            creditsLogVO: {
              changeCredits: 0,
              remark: "",
              openMessagePush: '1'
            },
            updateCreditsRules: {
                changeCredits: [
                    {required: true, message: '变更积分', trigger: 'blur'},
                ],
                remark: [
                    {required: true, message: '变更原因不能为空', trigger: 'blur'},
                ],
                openMessagePush: [
                    {required: true, message: '站内信触达不能为空', trigger: 'blur'},
                ],
            },
            rules: {
                userName: [
                    {required: true, message: '用户名不能为空', trigger: 'blur'},
                    {min: 5, max: 40, message: '长度在5到40个字符'},
                ],
                nickName: [
                    {required: true, message: '昵称不能为空', trigger: 'blur'},
                    {min: 1, max: 30, message: '长度在1到30个字符'},
                ],
                commentStatus: [
                    {required: true, message: '评论状态不能为空', trigger: 'blur'}
                ],
                userTag: [
                    {required: true, message: '用户标签不能为空', trigger: 'blur'}
                ],
                gender: [
                    {required: true, message: '性别不能为空', trigger: 'blur'},
                ],
                email: [
                    {pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/, message: '请输入正确的邮箱'},
                ],
                qqNumber: [
                    {pattern: /[1-9]([0-9]{5,11})/, message: '请输入正确的QQ号码'}
                ]
            }
        };
    },
    components: {
        AvatarCropper,
        UserAvatar,
    },
    created() {
        //传递过来的pictureSordUid
        let source = this.$route.query.source;
        let nickName = this.$route.query.nickName;
        if (source != undefined || nickName != undefined) {
            this.accountSourceKeyword = source;
            this.keyword = nickName;
            this.userList();
        }

        // 字典查询
        this.getDictList();
        this.userList();
    },
    methods: {
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
            this.userList()
        },
        userList: function () {
            let params = {};
            params.keyword = this.keyword;
            params.commentStatus = this.commentStatusKeyword;
            params.source = this.accountSourceKeyword;
            params.userTag = this.userTagKeyword;
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getUserList(params).then(response => {
                console.log("getUserList", response);
                if (response.code == this.$ECode.SUCCESS) {
                    this.tableData = response.data.records;
                    this.currentPage = response.data.current;
                    this.pageSize = response.data.size;
                    this.total = response.data.total;
                }
            });
        },

        cropSuccess(resData) {
            this.imagecropperShow = false
            this.imagecropperKey = this.imagecropperKey + 1
            this.form.photoUrl = resData.url;
            this.form.avatar = resData.uid
        },
        close() {
            this.imagecropperShow = false
        },
        deletePhoto: function () {
            console.log("删除图片", this.form)
            this.form.photoUrl = null;
            this.form.avatar = "";
            this.icon = false;
        },
        //弹出选择图片框
        checkPhoto() {
            this.imagecropperShow = true
        },

        /**
         * 字典查询
         */
        getDictList: function () {
            var dictTypeList = ['sys_account_source', 'sys_comment_status', 'sys_user_sex', 'sys_user_tag', 'sys_data_status', 'sys_auth_code', 'sys_yes_no']
            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    let dictMap = response.data;
                    this.accountSourceDictList = dictMap.sys_account_source.list
                    this.commentStatusDictList = dictMap.sys_comment_status.list
                    this.genderDictList = dictMap.sys_user_sex.list
                    this.userTagDictList = dictMap.sys_user_tag.list
                    this.dataStatusDictList = dictMap.sys_data_status.list
                    this.authCodeDictList = dictMap.sys_auth_code.list
                    this.yesNoDictList = dictMap.sys_yes_no.list
                    console.log("authCodeDictList", this.authCodeDictList)
                }
            });
        },
        valueFormat(value) {
            return '经验值：' + value
        },
        getFormObject: function () {
            let formObject = {
                uid: null,
                authCodeList: [],
            };
            return formObject;
        },
        handleFind: function () {
            this.currentPage = 1
            this.userList();
        },
        handleAdd: function () {
            this.dialogFormVisible = true;
            this.authCodeList = []
            this.form = this.getFormObject();
            this.isEditForm = false;
        },
        handleEdit: function (row) {
            this.title = "编辑用户";
            this.dialogFormVisible = true;
            this.isEditForm = true;
            this.authCodeList = row.authCodeList? JSON.parse(row.authCodeList): []
            this.form = row;
        },
        updateCredits: function (row) {
            this.updateCreditsVisible = true;
            this.form = row;
        },
        submitUpdateCredits() {
            let params = {};
            params.userUid = this.form.uid;
            params.updateCredits = this.creditsLogVO.changeCredits
            params.remark = this.creditsLogVO.remark
            params.openMessagePush = this.creditsLogVO.openMessagePush
            updateCredits(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.$commonUtil.message.success(response.message)
                    this.creditsLogVO = {}
                    this.updateCreditsVisible = false
                } else {
                    this.$commonUtil.message.success(response.message)
                }
            });
        },
        handleCreditsCurrentChange: function (val) {
            this.showCredits(this.creditsLog.userUid, val);
        },
        showCredits: function (uid, currentPage) {
            let that = this;
            that.creditsLog.userUid = uid;
            let params = {};
            params.uid = uid;
            params.currentPage = currentPage;
            params.pageSize = 5;
            queryCreditsByUserUid(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    that.creditsLog.tableData = response.data.records;
                    that.creditsLog.currentPage = response.data.current;
                    that.creditsLog.pageSize = response.data.size;
                    that.creditsLog.total = response.data.total;
                } else {
                    this.$commonUtil.message.error(response.message)
                }
            })
            this.creditsLogListFormVisible = true;
        },
        handleDelete: function (row) {
            let that = this;
            this.$confirm("此操作将把用户删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    var params = {};
                    params.uid = row.uid;
                    deleteUser(params).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                            that.userList();
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除")
                });
        },
        handleUpdatePassword: function (row) {
            var that = this;
            this.$confirm("此操作将把用户密码重置为初始密码?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let params = {};
                    params.uid = row.uid;
                    resetUserPassword(params).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                            that.userList();
                        } else {
                            this.$commonUtil.message.success(response.message)
                        }
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消重置")
                });
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.userList();
        },
        submitForm: function () {
            console.log("提交权限code", this.form)
            this.form.authCodeList = JSON.stringify(this.authCodeList)
            this.$refs.form.validate((valid) => {
                if (!valid) {
                    console.log("校验出错")
                } else {
                    if (this.isEditForm) {
                        editUser(this.form).then(response => {
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$notify({
                                    title: "成功",
                                    message: response.message,
                                    type: "success"
                                });
                                this.dialogFormVisible = false
                                this.userList();
                            } else {
                                this.$notify.error({
                                    title: "失败",
                                    message: response.message
                                });
                            }
                        });
                    } else {
                        addUser(this.form).then(response => {
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$notify({
                                    title: "成功",
                                    message: response.message,
                                    type: "success"
                                });
                                this.dialogFormVisible = false
                                this.userList();
                            } else {
                                this.$notify.error({
                                    title: "失败",
                                    message: response.message
                                });
                            }
                        });
                    }
                }
            })
        },
        //积分补偿
        compensation: function (row) {
            let that = this;
            let params = {};
            params.userUid = this.creditsLog.userUid;
            params.actionCode = row.actionCode;
            params.resourceUid = row.resourceUid;
            console.log(params);
            compensationByUserUid(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.$commonUtil.message.success(response.message)
                } else {
                    this.$commonUtil.message.success(response.message)
                }
                that.showCredits(that.creditsLog.userUid, that.creditsLog.currentPage);
            });

        }
    }
};
</script>
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
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
}

.imgBody {
    width: 100px;
    height: 100px;
    border: solid 2px #ffffff;
    float: left;
    position: relative;
}

.uploadImgBody {
    margin-left: 5px;
    width: 100px;
    height: 100px;
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

img {
    width: 100px;
    height: 100px;
}
</style>
