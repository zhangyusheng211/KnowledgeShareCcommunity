<template>
    <div id="table" class="app-container calendar-list-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;">
            <el-input clearable class="filter-item" style="width: 200px;" v-model="queryParams.keyword" size="small"
                      placeholder="请输入关键字"></el-input>

            <el-select
                v-model="queryParams.resourceSort"
                clearable
                placeholder="资源分类"
                style="width: 110px; margin-left: 5px;"
                size="small"
            >
                <el-option
                    v-for="item in resourceSortDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>

            <el-select
                v-model="queryParams.payType"
                clearable
                placeholder="支付类型"
                style="width: 110px; margin-left: 5px;"
                size="small"
            >
                <el-option
                    v-for="item in payTypeDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>

            <el-select
                v-model="queryParams.chargeType"
                clearable
                placeholder="收费模式"
                style="width: 110px; margin-left: 5px;"
                size="small"
            >
                <el-option
                    v-for="item in chargeTypeDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>

            <el-select
                v-model="queryParams.panType"
                clearable
                placeholder="网盘类型"
                style="width: 110px; margin-left: 5px;"
                size="small"
            >
                <el-option
                    v-for="item in panTypeDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>

            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFind" size="small"
                       v-permission="'/resource/getList'">查找
            </el-button>
            <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit" size="small"
                       v-permission="'/resource/add'">添加
            </el-button>
            <el-button class="filter-item" type="danger" @click="handleDeleteBatch" icon="el-icon-delete" size="small"
                       v-permission="'/resource/deleteBatch'">删除选中
            </el-button>
        </div>

        <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange"
                  :default-sort="{ prop: 'createTime', order: 'descending' }" @sort-change="changeSort">
            <el-table-column type="selection"></el-table-column>

            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.$index + 1 }}</span>
                </template>
            </el-table-column>

            <!--            <el-table-column label="标题图" width="160" align="center">-->
            <!--                <template slot-scope="scope">-->
            <!--                    <img v-if="scope.row.photoList" :src="scope.row.photoList[0]" style="width: 105px;height: 70px;"/>-->
            <!--                </template>-->
            <!--            </el-table-column>-->

            <el-table-column label="名称" width="160" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.name }}</span>
                </template>
            </el-table-column>

            <!--            <el-table-column label="点击数" width="100" align="center">-->
            <!--                <template slot-scope="scope">-->
            <!--                    <span>{{ scope.row.clickCount }}</span>-->
            <!--                </template>-->
            <!--            </el-table-column>-->

            <el-table-column
                label="分类名"
                width="150"
                align="center"
                prop="level"
            >
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in resourceSortDictList"
                        v-if="scope.row.resourceSort == item.dictValue"
                        :key="item.uid"
                        :type="item.listClass"
                    >{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column
                label="支付类型"
                width="100"
                align="center"
                prop="level"
            >
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in payTypeDictList"
                        v-if="scope.row.payType == item.dictValue"
                        :key="item.uid"
                        :type="item.listClass"
                    >{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column
                label="收费模式"
                width="150"
                align="center"
                prop="level"
            >
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in chargeTypeDictList"
                        v-if="scope.row.chargeType == item.dictValue"
                        :key="item.uid"
                        :type="item.listClass"
                    >
                        <span>
                            {{ item.dictLabel }}
                            <span v-if="scope.row.price && scope.row.price > 0">
                            ({{ scope.row.price }}
                            <span v-if="scope.row.payType == 1">
                                积分
                            </span>
                            <span v-if="scope.row.payType == 2">
                                分
                            </span>
                            )
                            </span>

                        </span>

                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column
                label="网盘类型"
                width="100"
                align="center"
                prop="level"
            >
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in panTypeDictList"
                        v-if="scope.row.panType == item.dictValue"
                        :key="item.uid"
                        :type="item.listClass"
                    >{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column label="网盘路径" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.panPath }}</span>
                </template>
            </el-table-column>

            <el-table-column label="网盘Code" width="100" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.panCode }}</span>
                </template>
            </el-table-column>

            <el-table-column
                :sort-orders="['ascending', 'descending']"
                label="创建时间"
                width="160"
                align="center"
                prop="createTime"
                sortable="custom"
            >
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

            <el-table-column label="排序" width="100" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.sort }}</span>
                </template>
            </el-table-column>

            <el-table-column label="操作" fixed="right" min-width="150">
                <template slot-scope="scope">
                    <el-button @click="handleEdit(scope.row)" type="primary" size="mini"
                               v-permission="'/resource/edit'">编辑
                    </el-button>
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini"
                               v-permission="'/resource/deleteBatch'">删除
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
        <el-dialog :title="title" :visible.sync="dialogFormVisible" fullscreen="">


            <el-form :model="form" :rules="rules" ref="form">


                <el-row>
                    <el-col :span="16">
                        <el-form-item label="标题" :label-width="formLabelWidth">
                            <el-input v-model="form.name" auto-complete="off"></el-input>
                        </el-form-item>

                        <el-form-item
                            :label-width="formLabelWidth"
                            label="简介"
                            prop="summary"
                        >
                            <el-input
                                v-model="form.summary"
                                auto-complete="off"
                            />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="标题图" :label-width="formLabelWidth">
                            <div class="imgBody" v-if="form.photoList">
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


                <el-row :gutter="24">
                    <el-col :span="5">
                        <el-form-item label="资源分类" :label-width="formLabelWidth" prop="chargeType">
                            <el-select
                                v-model="form.resourceSort"
                                size="small"
                                placeholder="请选择"
                                style="width: 250px"
                            >
                                <el-option
                                    v-for="item in resourceSortDictList"
                                    :key="item.uid"
                                    :label="item.dictLabel"
                                    :value="parseInt(item.dictValue)"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :span="5">
                        <el-form-item label="收费模式" :label-width="formLabelWidth" prop="chargeType">
                            <el-select
                                v-model="form.chargeType"
                                size="small"
                                placeholder="请选择"
                                style="width: 250px"
                            >
                                <el-option
                                    v-for="item in chargeTypeDictList"
                                    :key="item.uid"
                                    :label="item.dictLabel"
                                    :value="parseInt(item.dictValue)"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :span="5">
                        <el-form-item label="支付类型" :label-width="formLabelWidth" prop="payType">
                            <el-select
                                v-model="form.payType"
                                size="small"
                                placeholder="请选择"
                                style="width: 250px"
                            >
                                <el-option
                                    v-for="item in payTypeDictList"
                                    :key="item.uid"
                                    :label="item.dictLabel"
                                    :value="parseInt(item.dictValue)"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>


                    <el-col :span="5">
                        <el-form-item label="价格(分)" :label-width="formLabelWidth" prop="price"
                                      v-if="form.chargeType != 1">
                            <el-input v-model="form.price" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="6">
                        <el-form-item label="折扣时间" :label-width="formLabelWidth" prop="discountTimeList"
                                      v-if="form.chargeType == 3">
                            <el-date-picker
                                v-model="discountTimeList"
                                type="datetimerange"
                                align="right"
                                unlink-panels
                                range-separator="至"
                                start-placeholder="开始日期"
                                end-placeholder="结束日期"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                :picker-options="pickerOptions">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>

                    <el-col :span="4" v-if="form.chargeType == 3">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="折扣价(分)"
                            prop="discountPrice"
                        >
                            <el-input
                                v-model="form.discountPrice"
                                auto-complete="off"
                            />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="5">
                        <el-form-item label="网盘类型" :label-width="formLabelWidth" prop="panType">
                            <el-select
                                v-model="form.panType"
                                size="small"
                                placeholder="请选择"
                                style="width: 250px"
                            >
                                <el-option
                                    v-for="item in panTypeDictList"
                                    :key="item.uid"
                                    :label="item.dictLabel"
                                    :value="parseInt(item.dictValue)"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="5">
                        <el-form-item label="网盘路径" :label-width="formLabelWidth" prop="panPath">
                            <el-input v-model="form.panPath" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="5">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="网盘密码"
                            prop="panCode"
                        >
                            <el-input
                                style="width: 150px;"
                                v-model="form.panCode"
                                auto-complete="off"
                            />
                        </el-form-item>
                    </el-col>

                    <el-col :span="5">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="排序"
                            prop="sort"
                        >
                            <el-input
                                style="width: 150px;"
                                v-model="form.sort"
                                auto-complete="off"
                            />
                        </el-form-item>
                    </el-col>

                </el-row>


                <el-form-item label="正文" :label-width="formLabelWidth">
                    <CKEditor ref="ckeditor" :content="form.content" :height="400"></CKEditor>
                </el-form-item>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitForm">确 定</el-button>
            </div>
        </el-dialog>

        <CheckPhoto @choose_data="getChooseData" @cancelModel="cancelModel" :photoVisible="photoVisible"
                    :photos="photoList" :files="fileIds" :limit="1"></CheckPhoto>

    </div>
</template>

<script>
import {addResource, deleteBatchResource, editResource, getResourceList} from "@/api/resource";

import CKEditor from "../../components/CKEditor";
import CheckPhoto from "../../components/CheckPhoto";
import {getListByDictTypeList} from '@/api/sysDictData'

export default {
    components: {
        CheckPhoto,
        CKEditor
    },
    created() {
        this.getDictList()
        this.resourceList();
    },
    data() {
        return {
            multipleSelection: [], //多选，用于批量删除
            tableData: [],
            resourceSortData: [], //资源分类列表
            queryParams: {
                keyword: null,
                resourceSort: null,
                payType: null,
                chargeType: null,
                panType: null,
            },
            form: {
                uid: null,
                name: null,
                fileUid: null
            },
            orderByDescColumn: 'createTime', // 降序字段
            orderByAscColumn: '', // 升序字段
            payTypeDictList: [], // 支付类型
            chargeTypeDictList: [], // 收费模式
            publishDictList: [], // 发布
            resourceSortDictList: [], // 资源分类
            panTypeDictList: [], // 网盘类型
            payTypeDefaultValue: null,
            chargeTypeDefaultValue: null,
            publishDefaultValue: null,
            resourceSortDefaultValue: null,
            panTypeDefaultValue: null,
            sortKeyword: "",
            sortOptions: [], //分类候选项
            loading: false,
            dialogVisible: false, //控制增加框和修改框的显示
            currentPage: 1,
            total: null,
            pageSize: 10,
            keyword: "",
            title: "增加资源",
            formLabelWidth: "120px", //弹框的label边框
            dialogFormVisible: false,
            isEditForm: false,
            photoVisible: false, //控制图片选择器的显示
            photoList: [],
            fileIds: "",
            icon: false, //控制删除图标的显示
            rules: {
                resourceSor: [
                    {required: true, message: '分类不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '分类只能为自然数'}
                ],
                panType: [
                    {required: true, message: '网盘类型不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '网盘类型只能为自然数'}
                ],
                price: [
                    {required: true, message: '价格不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '价格只能为自然数'}
                ],
                chargeType: [
                    {required: true, message: '收费模式不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '收费模式只能为自然数'}
                ],
                panCode: [
                    {required: true, message: '网盘密码不能为空', trigger: 'blur'},
                ],
                name: [
                    {required: true, message: '标题不能为空', trigger: 'blur'},
                ],
                openComment: [
                    {required: true, message: '网站评论不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '网站评论只能为自然数'}
                ],
                content: [{required: true, message: '内容不能为空', trigger: 'blur'}],
                panPath: [
                    {required: true, message: '网盘地址不能为空', trigger: 'blur'},
                    {
                        pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/,
                        message: '请输入有效的URL'
                    }
                ]
            },
            discountTimeList: null,
            pickerOptions: {
                shortcuts: [
                    {
                        text: '一周',
                        onClick(picker) {
                            const start = new Date();
                            const end = new Date();
                            end.setTime(start.getTime() + 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', [start, end]);
                        }
                    },
                    {
                        text: '一个月',
                        onClick(picker) {
                            const start = new Date();
                            const end = new Date();
                            end.setTime(start.getTime() + 3600 * 1000 * 24 * 30);
                            picker.$emit('pick', [start, end]);
                        }
                    },
                    {
                        text: '三个月',
                        onClick(picker) {
                            const start = new Date();
                            const end = new Date();
                            end.setTime(start.getTime() + 3600 * 1000 * 24 * 90);
                            picker.$emit('pick', [start, end]);
                        }
                    },
                    {
                        text: '一年',
                        onClick(picker) {
                            const start = new Date();
                            const end = new Date();
                            end.setTime(start.getTime() + 3600 * 1000 * 24 * 365);
                            picker.$emit('pick', [start, end]);
                        }
                    }

                ]
            },
        };
    },
    methods: {
        // 从后台获取数据,重新排序
        changeSort(val) {
            // 根据当前排序重新获取后台数据,一般后台会需要一个排序的参数
            if (val.order == 'ascending') {
                this.orderByAscColumn = val.prop
                this.orderByDescColumn = ''
            } else {
                this.orderByAscColumn = ''
                this.orderByDescColumn = val.prop
            }
            this.resourceList()
        },
        resourceList: function () {
            let params = {};
            params.keyword = this.queryParams.keyword;
            params.resourceSort = this.queryParams.resourceSort;
            params.payType = this.queryParams.payType;
            params.chargeType = this.queryParams.chargeType;
            params.panType = this.queryParams.panType;

            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getResourceList(params).then(response => {
                this.tableData = response.data.records;
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
                'sys_pay_type',
                'sys_charge_type',
                'sys_publish_status',
                'sys_resource_sort',
                'sys_pan_type',
            ]
            getListByDictTypeList(dictTypeList).then((response) => {
                if (response.code == this.$ECode.SUCCESS) {
                    const dictMap = response.data
                    this.payTypeDictList = dictMap.sys_pay_type.list
                    this.chargeTypeDictList = dictMap.sys_charge_type.list
                    this.publishDictList = dictMap.sys_publish_status.list
                    this.resourceSortDictList = dictMap.sys_resource_sort.list
                    this.panTypeDictList = dictMap.sys_pan_type.list

                    if (dictMap.sys_pay_type.defaultValue) {
                        this.payTypeDefaultValue = dictMap.sys_pay_type.defaultValue
                    }
                    if (dictMap.sys_charge_type.defaultValue) {
                        this.chargeTypeDefaultValue = dictMap.sys_charge_type.defaultValue
                    }
                    if (dictMap.sys_resource_sort.defaultValue) {
                        this.resourceSortDefaultValue = dictMap.sys_resource_sort.defaultValue
                    }
                    if (dictMap.sys_pan_type.defaultValue) {
                        this.panTypeDefaultValue = dictMap.sys_pan_type.defaultValue
                    }

                }
            })
        },
        handleFind: function () {
            this.currentPage = 1
            this.resourceList();
        },
        getFormObject: function () {
            let formObject = {
                name: null,
                summary: null,
                content: null,
                fileUid: null,
                sort: 0,
                payType: parseInt(this.payTypeDefaultValue),
                chargeType: parseInt(this.chargeTypeDefaultValue),
                resourceSort: parseInt(this.resourceSortDefaultValue),
                panType: parseInt(this.panTypeDefaultValue),
            };
            return formObject;
        },
        //弹出选择图片框
        checkPhoto: function () {
            this.photoList = [];
            this.fileIds = "";
            this.photoVisible = true;
        },
        getChooseData(data) {
            this.photoVisible = false;
            this.photoList = data.photoList;
            this.fileIds = data.fileIds;
            let fileId = this.fileIds.replace(",", "");
            if (this.photoList.length >= 1) {
                this.form.fileUid = fileId;
                this.form.photoList = this.photoList;
            }
        },
        //关闭模态框
        cancelModel() {
            this.photoVisible = false;
        },
        deletePhoto: function () {
            this.form.photoList = null;
            this.form.fileUid = "";
        },
        //改变页码
        handleCurrentChange(val) {
            console.log(`当前页: ${val}`);
            this.currentPage = val; //改变当前所指向的页数
            this.resourceList();
        },
        //点击新增
        handleAdd: function () {
            this.title = "增加资源"
            let that = this;
            try {
                that.$refs.ckeditor.initData(); //清空CKEditor中内容
            } catch (error) {
                // TODO 第一次还未加载的时候，可能会报错，不过不影响使用，暂时还没有想到可能解决的方法
            }
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.isEditForm = false;
        },
        //点击编辑
        handleEdit: function (row) {
            this.title = "编辑资源"
            this.dialogFormVisible = true;
            this.isEditForm = true;
            this.form = row;
            let that = this;
            if (row.discountStartTime && row.discountEndTime) {
                this.discountTimeList = [row.discountStartTime, row.discountEndTime]
            }
            try {
                that.$refs.ckeditor.setData(this.form.content); //设置富文本内容
            } catch (error) {
                // 第一次还未加载的时候，可能会报错，不过不影响使用
                // 暂时还没有想到可能解决的方法
            }
        },
        handleDelete: function (row) {
            this.$confirm("此操作将会把分类下全部资源删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    var params = [];
                    params.push(row);
                    deleteBatchResource(params).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                            this.resourceList();
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
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
            this.$confirm("此操作将把选中的资源删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    deleteBatchResource(that.multipleSelection).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                            this.resourceList();
                        } else {
                            this.$commonUtil.message.success(response.message)
                        }
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除")
                });
        },
        submitForm: function () {
            console.log("提交折扣时间", this.discountTimeList)
            let discountTimeList = this.discountTimeList
            if (discountTimeList && discountTimeList.length > 0) {
                this.form.discountStartTime = discountTimeList[0]
                this.form.discountEndTime = discountTimeList[1]
            }
            this.$refs.form.validate((valid) => {
                if (!valid) {
                    console.log("校验出错")
                } else {
                    this.form.content = this.$refs.ckeditor.getData(); //获取CKEditor中的内容
                    if (this.isEditForm) {
                        editResource(this.form).then(response => {
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$commonUtil.message.success(response.message)
                                this.dialogFormVisible = false;
                                this.resourceList();
                            } else {
                                this.$commonUtil.message.error(response.message)
                            }
                        });
                    } else {
                        addResource(this.form).then(response => {
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$commonUtil.message.success(response.message)
                                this.dialogFormVisible = false;
                                this.resourceList();
                            } else {
                                this.$commonUtil.message.error(response.message)
                            }
                        });
                    }
                }
            })
        },
        // 改变多选
        handleSelectionChange(val) {
            this.multipleSelection = val;
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
</style>
