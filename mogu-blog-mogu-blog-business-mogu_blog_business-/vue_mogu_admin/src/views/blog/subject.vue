<template>
    <div id="table" class="app-container calendar-list-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;">
            <el-input clearable class="filter-item" style="width: 200px;" v-model="queryParams.keyword" size="small"
                      placeholder="请输入专栏名称"></el-input>

            <el-select
                v-model="queryParams.subjectSortUid"
                clearable
                placeholder="专栏分类"
                style="width: 150px"
                size="small"
            >
                <el-option
                    v-for="item in subjectSortData"
                    :key="item.uid"
                    :label="item.name"
                    :value="item.uid"
                />
            </el-select>

            <el-select
                v-model="queryParams.visitAuth"
                clearable
                placeholder="访问权限"
                style="width: 110px"
                size="small"
            >
                <el-option
                    v-for="item in visitAuthDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>

            <el-select
                v-model="queryParams.isOriginal"
                clearable
                placeholder="是否原创"
                style="width: 110px"
                size="small"
            >
                <el-option
                    v-for="item in originalDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>

            <el-select
                v-model="queryParams.isSelection"
                clearable
                placeholder="是否精选"
                style="width: 110px"
                size="small"
            >
                <el-option
                    v-for="item in selectionDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>

            <el-select
                v-model="queryParams.isPublish"
                clearable
                placeholder="是否发布"
                style="width: 110px"
                size="small"
            >
                <el-option
                    v-for="item in publishDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>



            <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFind"
                       v-permission="'/subject/getList'">查找
            </el-button>
            <el-button class="filter-item" type="primary" @click="handleAdd" size="small" icon="el-icon-edit"
                       v-permission="'/subject/add'">添加
            </el-button>
            <el-button class="button" type="primary" @click="checkAll()" size="small" icon="el-icon-refresh">
                {{ chooseTitle }}
            </el-button>
            <el-button class="filter-item" type="danger" @click="handleDeleteBatch" size="small" icon="el-icon-delete"
                       v-permission="'/subject/deleteBatch'">删除选中
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

            <el-table-column align="center" label="专栏图" width="240">
                <template slot-scope="scope">
                    <el-image v-if="scope.row.photoList && scope.row.photoList[0]" :src="scope.row.photoList[0]"
                              :preview-src-list="scope.row.photoList"
                              style="width: 184px; height: 80px;"
                    ></el-image>
                </template>
            </el-table-column>

            <el-table-column label="专栏名称" width="150" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.subjectName }}</span>
                </template>
            </el-table-column>

            <el-table-column label="专栏作者" width="150" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.author }}</span>
                </template>
            </el-table-column>

            <el-table-column
                label="专栏分类"
                width="120"
                align="center"
                prop="subjectSortUid"
            >
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in subjectSortData"
                        v-if="scope.row.subjectSortUid == item.uid"
                        :key="item.uid"
                    >{{ item.name }}
                    </el-tag
                    >
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['isOriginal']"
                label="是否原创"
                width="100"
                align="center"
                prop="isOriginal"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <el-tag v-if="scope.row.isOriginal == 1" type="success">原创</el-tag>
                    <el-tag v-else-if="scope.row.isOriginal == 0" type="info">转载</el-tag>
                </template>
            </el-table-column>

            <el-table-column label="精选" width="100" align="center" prop="isSelection" sortable="custom"
                             :sort-by="['isSelection']">
                <template slot-scope="scope">
                    <template>
                        <el-switch v-model="scope.row.selectionStatus" active-color="#F5DEB3"
                                   @change="handChange(scope.row)"></el-switch>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="图片标题" width="120" align="center" prop="openPictureTitle" sortable="custom"
                             :sort-by="['openPictureTitle']">
                <template slot-scope="scope">
                    <template>
                        <el-switch v-model="scope.row.openPictureTitleStatus" active-color="#F5DEB3"
                                   @change="handChange(scope.row)"></el-switch>
                    </template>
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['visitAuth']"
                label="访问权限"
                width="100"
                align="center"
                prop="level"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <span></span>
                    <el-tag
                        v-for="item in visitAuthDictList"
                        v-if="scope.row.visitAuth.split(',').includes(item.dictValue)"
                        :key="item.uid"
                        :type="item.listClass"
                        style="margin-right: 2px; margin-top: 2px;"
                    >{{ item.dictLabel }}
                    </el-tag
                    >
                </template>
            </el-table-column>

            <el-table-column label="上架" width="100" align="center" prop="isPublish" sortable="custom"
                             :sort-by="['isPublish']">
                <template slot-scope="scope">
                    <template>
                        <el-switch v-model="scope.row.publishStatus" active-color="#13ce66" inactive-color="#ff4949"
                                   @change="handChange(scope.row)"></el-switch>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="章节数" width="100" align="center" prop="subjectItemCount"
                             :sort-by="['subjectItemCount']">
                <template slot-scope="scope">
                    <span>{{ scope.row.subjectItemCount }}</span>
                </template>
            </el-table-column>

            <el-table-column label="点击数" width="100" align="center" prop="clickCount" sortable="custom"
                             :sort-by="['clickCount']">
                <template slot-scope="scope">
                    <span>{{ scope.row.clickCount }}</span>
                </template>
            </el-table-column>

            <el-table-column label="排序" width="100" align="center" sortable="custom" prop="sort" :sort-by="['sort']">
                <template slot-scope="scope">
                    <el-tag type="warning">{{ scope.row.sort }}</el-tag>
                </template>
            </el-table-column>

            <el-table-column label="创建时间" width="160" align="center" prop="createTime" sortable="custom"
                             :sort-by="['createTime']" :sort-orders="['ascending', 'descending']">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="操作" fixed="right" min-width="160">
                <template slot-scope="scope">
                    <el-row>
                        <el-button @click="handleEdit(scope.row)" type="primary" size="mini"
                                   v-permission="'/subject/edit'">编辑
                        </el-button>
                        <el-button @click="handleDelete(scope.row)" type="danger" size="mini"
                                   v-permission="'/subject/deleteBatch'">删除
                        </el-button>
                    </el-row>
                    <el-row style="margin-top: 10px;">
                        <el-button type="success" size="mini" @click="goSubjectItem(scope.row)">管理</el-button>
                        <el-button type="warning" size="mini" @click="goWebSubject(scope.row)">查看</el-button>
                    </el-row>
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
        <el-dialog :title="title" v-if="dialogFormVisible" :visible.sync="dialogFormVisible" fullscreen>
            <el-form :model="form" :rules="rules" ref="form">

                <el-row>
                    <el-col :xs="24" :sm="16">
                        <el-form-item label="专栏名" :label-width="formLabelWidth" prop="subjectName">
                            <el-input v-model="form.subjectName" size="small" auto-complete="off"></el-input>
                        </el-form-item>

                        <el-form-item label="简介" :label-width="formLabelWidth">
                            <el-input v-model="form.summary" size="small" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="8">
                        <el-form-item label="封面图" :label-width="formLabelWidth" prop="fileUid">
                            <div class="imgBody" v-if="form.photoList && form.photoList.length > 0">
                                <i class="el-icon-error inputClass" v-show="icon" @click="deletePhoto()"
                                   @mouseover="icon = true"></i>
                                <img @mouseover="icon = true" @mouseout="icon = false" v-bind:src="form.photoList[0]"
                                     style="display:inline; width: 195px;height: 105px;"/>
                            </div>
                            <div v-else class="uploadImgBody" @click="checkPhoto">
                                <i class="el-icon-plus avatar-uploader-icon"></i>
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="是否原创"
                            prop="isOriginal"
                        >
                            <el-radio-group v-model="form.isOriginal" size="small">
                                <el-radio
                                    v-for="item in originalDictList"
                                    :key="item.uid"
                                    :label="item.dictValue"
                                    border
                                >{{ item.dictLabel }}
                                </el-radio
                                >
                            </el-radio-group>
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="精选"
                            prop="isOriginal"
                        >
                            <el-radio-group v-model="form.isSelection" size="small">
                                <el-radio
                                    v-for="item in openDictList"
                                    :key="item.uid"
                                    :label="parseInt(item.dictValue)"
                                    border
                                >{{ item.dictLabel }}
                                </el-radio
                                >
                            </el-radio-group>
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="图片标题"
                            prop="isOriginal"
                        >
                            <el-radio-group v-model="form.openPictureTitle" size="small">
                                <el-radio
                                    v-for="item in openDictList"
                                    :key="item.uid"
                                    :label="parseInt(item.dictValue)"
                                    border
                                >{{ item.dictLabel }}
                                </el-radio
                                >
                            </el-radio-group>
                        </el-form-item>
                    </el-col>


                </el-row>

                <el-form-item
                    v-if="form.isOriginal == 0"
                    :label-width="formLabelWidth"
                    label="专栏出处"
                >
                    <el-input v-model="form.origin" auto-complete="off"/>
                </el-form-item>

                <el-row>
                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="专栏分类"
                            prop="subjectSortUid"
                        >
                            <el-select
                                v-model="form.subjectSortUid"
                                size="small"
                                placeholder="请选择专栏分类"
                                style="width: 240px"
                                clearable
                                filterable
                            >
                                <el-option
                                    v-for="item in subjectSortData"
                                    :key="item.uid"
                                    :label="item.name"
                                    :value="item.uid"
                                />
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item label="专栏作者" :label-width="formLabelWidth" prop="author">
                            <el-input v-model="form.author" size="small" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item label="排序" :label-width="formLabelWidth" prop="sort">
                            <el-input v-model="form.sort" size="small" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">

                    </el-col>
                </el-row>


                <!--访问扩展字段-->
                <visit-auth ref="visitAuthRef" :showLoadingArea="true" :excludeVisitAuth="[3,7,8,9,11]"
                            :formLabelWidth="formLabelWidth" :visitAuth="form.visitAuth"
                            :visitAuthExtra="form.visitAuthExtraVo"></visit-auth>

                <el-form-item :label-width="formLabelWidth" label="专栏描述" prop="content">
                    <CKEditor
                        v-if="systemConfig.editorModel == '0'"
                        ref="editor"
                        :content="form.content"
                        :height="400"
                        @contentChange="contentChange"
                    />
                    <Mavon
                        v-if="systemConfig.editorModel == '1'"
                        ref="editor"
                        :content="form.content"
                        :height="465"
                        @contentChange="contentChange"
                    />
                </el-form-item>

            </el-form>

            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitForm(0)">确 定</el-button>
            </div>
        </el-dialog>

        <avatar-cropper
            :key="imagecropperKey"
            :url="url"
            :width="740"
            :height="320"
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
import {addSubject, deleteBatchSubject, editSubject, getSubjectList} from "@/api/subject";
import {getSubjectSortList,} from "@/api/subjectSort";
import AvatarCropper from '@/components/AvatarCropper'
import VisitAuth from '@/components/VisitAuth/index'
import {getListByDictTypeList} from '@/api/sysDictData'
import CKEditor from '@/components/CKEditor'
import Mavon from '@/components/Mavon'

export default {
    components: {
        AvatarCropper,
        VisitAuth,
        CKEditor,
        Mavon,
    },
    created() {
        this.getDictList()
        this.subjectSortList()
        this.subjectList();
    },
    data() {
        return {
            queryParams: {
                keyword: "",
                visitAuth: null,
                subjectSortUid: null,
                isOriginal: null,
                sPublish: null,
                isSelection: null,
            },
            imagecropperShow: false,
            imagecropperKey: 0,
            url: process.env.PICTURE_API + "/file/cropperPicture",
            BLOG_WEB_URL: process.env.BLOG_WEB_URL,
            tableData: [],
            form: {},
            loading: true,
            dialogVisible: false, //控制增加框和修改框的显示
            currentPage: 1,
            total: null,
            pageSize: 18,
            keyword: "",
            chooseTitle: "全选",
            isCheckedAll: false, //是否全选
            selectUids: [], //专栏uid集合
            title: "增加专栏",
            subjectSortData: [], // 专栏分类
            originalDictList: [], // 是否原创字典
            originalDefault: null, // 是否原创默认值
            openDictList: [], // 是否开启
            openDefault: null, // 是否开启默认值
            formLabelWidth: "120px", //弹框的label边框
            dialogFormVisible: false,
            isEditForm: false,
            photoVisible: false, //控制图片选择器的显示
            photoList: [],
            fileIds: "",
            icon: false, //控制删除图标的显示
            orderByDescColumn: 'createTime', // 降序字段
            orderByAscColumn: '', // 升序字段
            isFirstPhotoVisible: true, // 图片选择器是否首次显示【用于懒加载】
            rules: {
                author: [
                    {required: true, message: '专栏作者不能为空', trigger: 'blur'}
                ],
                subjectName: [
                    {required: true, message: '专栏名不能为空', trigger: 'blur'},
                    {min: 1, max: 20, message: '长度在1到20个字符'},
                ],
                sort: [
                    {required: true, message: '排序字段不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '排序字段只能为自然数'},
                ],
                visitAuth: [
                    {required: true, message: '访问权限不能为空', trigger: 'blur'},
                ],
                price: [
                    {required: true, message: '价格不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '价格只能为自然数'}
                ],
                payType: [
                    {required: true, message: '支付方式不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '支付方式只能为自然数'}
                ],
                subjectSortUid: [
                    {required: true, message: '专栏分类不能为空', trigger: 'blur'},
                ],
                // loadingArea: [
                //     {required: true, message: '限制区域不能为空', trigger: 'blur'},
                //     {pattern: /^[0-9]\d*$/, message: '限制区域只能为自然数'}
                // ],
            },
            visitAuthDictList: [], // 访问权限字典
            visitAuthDefault: null,
            payTypeDictList: [], // 收费模式字典
            payTypeDefault: [], // 收费模式
            publishDictList: [], // 是否发布字典
            selectionDictList: [], // 是否精选
            systemConfig: this.$store.state.app.systemConfig, // 系统配置
        };
    },
    watch: {
        '$store.state.app.systemConfig': function (newFlag, oldFlag) {
            this.systemConfig = this.$store.state.app.systemConfig
        }
    },
    methods: {
        subjectList: function () {
            let params = {};
            params.keyword = this.queryParams.keyword;
            params.visitAuth = this.queryParams.visitAuth;
            params.isOriginal = this.queryParams.isOriginal
            params.isPublish = this.queryParams.isPublish
            params.subjectSortUid = this.queryParams.subjectSortUid
            params.isSelection = this.queryParams.isSelection
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getSubjectList(params).then(response => {
                let tableData = response.data.records;
                for (let i = 0; i < tableData.length; i++) {
                    if (tableData[i].isSelection == "1") {
                        tableData[i].selectionStatus = true
                    } else {
                        tableData[i].selectionStatus = false
                    }
                    if (tableData[i].openPictureTitle == "1") {
                        tableData[i].openPictureTitleStatus = true
                    } else {
                        tableData[i].openPictureTitleStatus = false
                    }
                    if (tableData[i].isPublish == "1") {
                        tableData[i].publishStatus = true
                    } else {
                        tableData[i].publishStatus = false
                    }
                }
                this.tableData = tableData;
                this.currentPage = response.data.current;
                this.pageSize = response.data.size;
                this.total = response.data.total;
            });
        },

        /**
         * 字典查询
         */
        getDictList: function () {
            const dictTypeList = [
                'sys_original_status',
                'sys_visit_auth',
                'sys_pay_type',
                'sys_normal_disable',
                'sys_publish_status',
                'sys_selection'
            ]
            getListByDictTypeList(dictTypeList).then((response) => {
                if (response.code == this.$ECode.SUCCESS) {
                    const dictMap = response.data
                    this.originalDictList = dictMap.sys_original_status.list
                    this.visitAuthDictList = dictMap.sys_visit_auth.list
                    this.payTypeDictList = dictMap.sys_pay_type.list
                    this.openDictList = dictMap.sys_normal_disable.list
                    this.publishDictList = dictMap.sys_publish_status.list
                    this.selectionDictList = dictMap.sys_selection.list
                    if (dictMap.sys_original_status.defaultValue) {
                        this.originalDefault = dictMap.sys_original_status.defaultValue
                    }
                    if (dictMap.sys_visit_auth.defaultValue) {
                        this.visitAuthDefault = dictMap.sys_visit_auth.defaultValue
                    }
                    if (dictMap.sys_pay_type.defaultValue) {
                        this.payTypeDefaultValue = dictMap.sys_pay_type.defaultValue
                    }
                    if (dictMap.sys_normal_disable.defaultValue) {
                        this.openDefault = dictMap.sys_normal_disable.defaultValue
                    }
                    if (dictMap.sys_normal_disable.defaultValue) {
                        this.openDefault = dictMap.sys_normal_disable.defaultValue
                    }
                }
            })
        },
        // 专栏分类
        subjectSortList: function () {
            let params = {};
            params.currentPage = 1;
            params.pageSize = 100;
            params.systemPreinstall = 0
            getSubjectSortList(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.subjectSortData = response.data.records;
                }
            });
        },

        // 从后台获取数据,重新排序
        changeSort(val) {
            console.log("默认排序", val)
            // 根据当前排序重新获取后台数据,一般后台会需要一个排序的参数
            if (val.order == "ascending") {
                this.orderByAscColumn = val.prop
                this.orderByDescColumn = ""
            } else {
                this.orderByAscColumn = ""
                this.orderByDescColumn = val.prop
            }
            this.subjectList()
        },
        handleFind: function () {
            this.currentPage = 1
            this.subjectList();
        },
        getFormObject: function () {
            let formObject = {
                uid: null,
                subjectName: null,
                summary: null,
                fileUid: null,
                sort: 0,
                isOriginal: this.originalDefault, // 是否原创
                visitAuth: parseInt(this.visitAuthDefault), // 推荐等级，默认是正常
                payType: parseInt(this.payTypeDefaultValue),
                openPictureTitle: parseInt(this.openDefault),
                isSelection: parseInt(this.openDefault),
            };
            return formObject;
        },
        //点击单选
        checked: function (data) {
            let idIndex = this.selectUids.indexOf(data.uid);
            if (idIndex >= 0) {
                //选过了
                this.selectUids.splice(idIndex, 1);
            } else {
                this.selectUids.push(data.uid);
            }
        },
        checkAll: function () {
            //如果是全选
            if (this.isCheckedAll) {
                this.selectUids = [];
                this.isCheckedAll = false;
                this.chooseTitle = "全选";
            } else {
                this.selectUids = [];
                this.tableData.forEach(function (picture) {
                    this.selectUids.push(picture.uid);
                }, this);
                this.isCheckedAll = true;
                this.chooseTitle = "取消全选";
            }
        },
        cropSuccess(resData) {
            this.imagecropperShow = false
            this.imagecropperKey = this.imagecropperKey + 1
            this.form.photoList = [resData.url];
            this.form.fileUid = resData.uid
        },
        close() {
            this.imagecropperShow = false
        },
        deletePhoto: function () {
            console.log("删除图片", this.form)
            this.form.photoList = [];
            this.form.fileUid = "";
            this.icon = false;
        },
        //弹出选择图片框
        checkPhoto() {
            this.imagecropperShow = true
        },

        //改变页码
        handleCurrentChange(val) {
            let that = this;
            this.currentPage = val; //改变当前所指向的页数
            this.subjectList();
        },
        //点击新增
        handleAdd: function () {
            this.title = "增加专栏"
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.isEditForm = false;
        },
        //点击编辑
        handleEdit: function (row) {
            this.title = "编辑专栏"
            this.dialogFormVisible = true;
            this.isEditForm = true;
            this.form = row;
        },
        handleDelete: function (row) {
            this.$confirm("此操作将会把该专栏删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    var params = [];
                    params.push(row);
                    deleteBatchSubject(params).then(response => {
                        if (response.code == "success") {
                            this.$commonUtil.message.success(response.message)
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                        this.subjectList();
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已经取消删除")
                });
        },
        goSubjectItem: function (row) {
            let uid = row.uid;
            this.$router.push({
                path: "subjectItem",
                query: {subjectUid: uid}
            });
        },
        goWebSubject: function (row) {
            window.open(this.BLOG_WEB_URL + "/subject/" + row.uid, "_blank");
        },
        handleDeleteBatch: function () {
            let that = this;
            if (that.selectUids.length <= 0) {
                this.$commonUtil.message.error("请先选中需要删除的内容！")
                return;
            }
            this.$confirm("此操作将把选中的专栏删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let deleteList = []
                    for (let i = 0; i < this.selectUids.length; i++) {
                        let params = {}
                        params.uid = this.selectUids[i]
                        deleteList.push(params)
                    }
                    deleteBatchSubject(deleteList).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                        that.subjectList();
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除")
                });
        },
        // 改变多选
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        handChange(row) {
            if (row.selectionStatus) {
                row.isSelection = 1
            } else {
                row.isSelection = 0
            }
            if (row.publishStatus) {
                row.isPublish = 1
            } else {
                row.isPublish = 0
            }
            if (row.openPictureTitleStatus) {
                row.openPictureTitle = 1
            } else {
                row.openPictureTitle = 0
            }
            this.form = row;
            this.submitForm(1)
        },
        // 内容改变，触发监听
        contentChange: function () {
            if (this.changeCount > 1) {
                // 获取CKEditor中的内容
                this.form.content = this.$refs.editor.getData()
            }
        },
        submitForm: function (submitType) {
            if (submitType === 0) {
                console.log("获取的扩展信息", this.form)
                let visitAuthInfo = this.$refs.visitAuthRef.getVisitAuthInfo()
                this.form.visitAuth = visitAuthInfo.visitAuth
                this.form.visitAuthExtraVo = visitAuthInfo.visitAuthExtra
                // 从中解析出收费模式和价格
                if (visitAuthInfo.visitAuthExtra) {
                    this.form.payType = visitAuthInfo.visitAuthExtra.payType
                    this.form.price = visitAuthInfo.visitAuthExtra.price
                    this.form.loadingArea = visitAuthInfo.visitAuthExtra.loadingArea
                }
                console.log("校验数据", this.form)
                this.form.content = this.$refs.editor.getData()
                this.$refs.form.validate((valid) => {
                    if (!valid) {
                        console.log("校验出错")
                    } else {
                        if (this.isEditForm) {
                            editSubject(this.form).then(response => {
                                if (response.code == this.$ECode.SUCCESS) {
                                    this.$commonUtil.message.success(response.message)
                                    this.dialogFormVisible = false;
                                    this.subjectList();
                                } else {
                                    this.$commonUtil.message.error(response.message)
                                }
                            });
                        } else {
                            addSubject(this.form).then(response => {
                                if (response.code == this.$ECode.SUCCESS) {
                                    this.$commonUtil.message.success(response.message)
                                    this.dialogFormVisible = false;
                                    this.subjectList();
                                } else {
                                    this.$commonUtil.message.error(response.message)
                                }
                            });
                        }
                    }
                })
            } else {
                editSubject(this.form).then(response => {
                    if (response.code == this.$ECode.SUCCESS) {
                        this.$commonUtil.message.success(response.message)
                        this.dialogFormVisible = false;
                        this.subjectList();
                    } else {
                        this.$commonUtil.message.error(response.message)
                    }
                });
            }
        },
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
    width: 195px;
    height: 105px;
    line-height: 105px;
    text-align: center;
}

.imgBody {
    width: 195px;
    height: 105px;
    border: solid 2px #ffffff;
    float: left;
    position: relative;
}

.uploadImgBody {
    margin-left: 5px;
    width: 195px;
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

.subjectName {
    position: absolute;
    right: 10px;
    top: 10px;
    z-index: 2;
    /*top: 15px;*/
    background: rgba(232, 40, 74, .8);
    color: #FFF;
    padding: 3px 8px;
    font-size: 12px;
    border-radius: 3px;
}
</style>
